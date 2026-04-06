package com.zslin.wechat.iot.device.service.impl;

import com.zslin.wechat.core.exception.WechatException;
import com.zslin.wechat.core.exception.WechatExceptionCodes;
import com.zslin.wechat.core.util.StringUtils;
import com.zslin.wechat.iot.device.entity.Device;
import com.zslin.wechat.iot.device.service.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设备服务实现类
 * <p>
 * 提供设备管理功能（内存存储，实际使用时需要接入数据库）
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Service
public class DeviceServiceImpl implements DeviceService {

    private static final Logger log = LoggerFactory.getLogger(DeviceServiceImpl.class);

    /**
     * 设备存储（设备 ID -> 设备）
     * 实际使用时应该使用数据库
     */
    private final Map<String, Device> deviceStore = new ConcurrentHashMap<>();

    @Override
    public Device register(Device device) {
        log.info("注册设备：name={}, type={}", device.getName(), device.getType());

        // 参数校验
        if (device == null) {
            throw new WechatException(WechatExceptionCodes.DEVICE_REGISTER_FAILED, "设备信息不能为空");
        }

        if (StringUtils.isBlank(device.getName())) {
            throw new WechatException(WechatExceptionCodes.DEVICE_REGISTER_FAILED, "设备名称不能为空");
        }

        if (StringUtils.isBlank(device.getType())) {
            throw new WechatException(WechatExceptionCodes.DEVICE_REGISTER_FAILED, "设备类型不能为空");
        }

        if (StringUtils.isBlank(device.getDeviceIdentify())) {
            throw new WechatException(WechatExceptionCodes.DEVICE_REGISTER_FAILED, "设备标识不能为空");
        }

        // 检查设备标识是否已存在
        if (isDeviceIdentifyExists(device.getDeviceIdentify())) {
            throw new WechatException(WechatExceptionCodes.DEVICE_REGISTER_FAILED, 
                String.format("设备标识 %s 已注册", device.getDeviceIdentify()));
        }

        // 生成设备 ID 和密钥
        if (StringUtils.isBlank(device.getDeviceId())) {
            device.setDeviceId(generateDeviceId());
        }

        if (StringUtils.isBlank(device.getDeviceSecret())) {
            device.setDeviceSecret(generateDeviceSecret());
        }

        // 设置默认值
        device.setStatus("offline");
        device.setRegisterTime(new Date());
        device.setVersion(device.getVersion() != null ? device.getVersion() : "1.0.0");

        // 存储设备
        deviceStore.put(device.getDeviceId(), device);

        log.info("设备注册成功：deviceId={}, deviceIdentify={}", 
            device.getDeviceId(), device.getDeviceIdentify());

        // 返回设备信息（不包含密钥）
        Device result = new Device();
        copyDeviceInfo(device, result);
        result.setDeviceSecret(null); // 不返回密钥
        
        return result;
    }

    @Override
    public Device findById(String deviceId) {
        if (StringUtils.isBlank(deviceId)) {
            throw new WechatException(WechatExceptionCodes.DEVICE_NOT_FOUND, "设备 ID 不能为空");
        }

        Device device = deviceStore.get(deviceId);
        if (device == null) {
            log.warn("设备未找到：deviceId={}", deviceId);
            throw new WechatException(WechatExceptionCodes.DEVICE_NOT_FOUND, 
                String.format("设备未找到：{}", deviceId));
        }

        log.debug("查询设备成功：deviceId={}", deviceId);
        
        // 返回副本（保护原始数据）
        Device result = new Device();
        copyDeviceInfo(device, result);
        return result;
    }

    @Override
    public List<Device> findByUser(String openId) {
        if (StringUtils.isBlank(openId)) {
            throw new WechatException(WechatExceptionCodes.DEVICE_NOT_FOUND, "用户 OpenID 不能为空");
        }

        List<Device> result = new ArrayList<>();
        for (Device device : deviceStore.values()) {
            if (device.getOwnerOpenId() != null && device.getOwnerOpenId().equals(openId)) {
                Device copy = new Device();
                copyDeviceInfo(device, copy);
                copy.setDeviceSecret(null); // 不返回密钥
                result.add(copy);
            }
        }

        log.debug("查询用户设备成功：openId={}, count={}", openId, result.size());
        return result;
    }

    @Override
    public boolean updateStatus(String deviceId, String status) {
        if (StringUtils.isBlank(deviceId)) {
            throw new WechatException(WechatExceptionCodes.DEVICE_NOT_FOUND, "设备 ID 不能为空");
        }

        if (StringUtils.isBlank(status)) {
            throw new WechatException(WechatExceptionCodes.DEVICE_OFFLINE, "设备状态不能为空");
        }

        Device device = deviceStore.get(deviceId);
        if (device == null) {
            throw new WechatException(WechatExceptionCodes.DEVICE_NOT_FOUND, "设备未找到");
        }

        device.setStatus(status);
        if ("online".equals(status)) {
            device.setLastOnlineTime(new Date());
        }

        log.debug("更新设备状态成功：deviceId={}, status={}", deviceId, status);
        return true;
    }

    @Override
    public boolean unbind(String deviceId, String openId) {
        if (StringUtils.isBlank(deviceId)) {
            throw new WechatException(WechatExceptionCodes.DEVICE_UNBIND_FAILED, "设备 ID 不能为空");
        }

        Device device = deviceStore.get(deviceId);
        if (device == null) {
            throw new WechatException(WechatExceptionCodes.DEVICE_NOT_FOUND, "设备未找到");
        }

        // 验证所有权
        if (!device.belongsTo(openId)) {
            throw new WechatException(WechatExceptionCodes.DEVICE_UNBIND_FAILED, "无权解绑该设备");
        }

        device.setOwnerOpenId(null);
        device.setStatus("offline");

        log.info("设备解绑成功：deviceId={}, openId={}", deviceId, openId);
        return true;
    }

    @Override
    public boolean delete(String deviceId) {
        if (StringUtils.isBlank(deviceId)) {
            throw new WechatException(WechatExceptionCodes.DEVICE_NOT_FOUND, "设备 ID 不能为空");
        }

        if (deviceStore.remove(deviceId) != null) {
            log.info("设备删除成功：deviceId={}", deviceId);
            return true;
        }

        log.warn("设备删除失败：设备未找到 deviceId={}", deviceId);
        return false;
    }

    // ==================== 私有方法 ====================

    /**
     * 检查设备标识是否已存在
     */
    private boolean isDeviceIdentifyExists(String deviceIdentify) {
        for (Device device : deviceStore.values()) {
            if (device.getDeviceIdentify().equals(deviceIdentify)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 生成设备 ID
     */
    private String generateDeviceId() {
        return "DEV_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    /**
     * 生成设备密钥
     */
    private String generateDeviceSecret() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 复制设备信息
     */
    private void copyDeviceInfo(Device source, Device target) {
        target.setDeviceId(source.getDeviceId());
        target.setName(source.getName());
        target.setType(source.getType());
        target.setStatus(source.getStatus());
        target.setOwnerOpenId(source.getOwnerOpenId());
        target.setDeviceIdentify(source.getDeviceIdentify());
        target.setDeviceSecret(source.getDeviceSecret());
        target.setLastOnlineTime(source.getLastOnlineTime());
        target.setRegisterTime(source.getRegisterTime());
        target.setVersion(source.getVersion());
        target.setExtraInfo(source.getExtraInfo());
    }
}
