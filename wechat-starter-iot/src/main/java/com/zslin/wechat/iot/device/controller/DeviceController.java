package com.zslin.wechat.iot.device.controller;

import com.zslin.wechat.iot.device.entity.Device;
import com.zslin.wechat.iot.device.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备管理控制器
 * <p>
 * 提供设备管理的 HTTP 接口
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/iot/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    /**
     * 注册设备
     *
     * @param device 设备信息
     * @return 注册成功的设备
     */
    @PostMapping("/register")
    public Device register(@RequestBody Device device) {
        return deviceService.register(device);
    }

    /**
     * 查询设备详情
     *
     * @param deviceId 设备 ID
     * @return 设备信息
     */
    @GetMapping("/{deviceId}")
    public Device getDevice(@PathVariable String deviceId) {
        return deviceService.findById(deviceId);
    }

    /**
     * 查询用户所有设备
     *
     * @param openId 用户 OpenID
     * @return 设备列表
     */
    @GetMapping("/user/{openId}")
    public List<Device> getUserDevices(@PathVariable String openId) {
        return deviceService.findByUser(openId);
    }

    /**
     * 更新设备状态
     *
     * @param deviceId 设备 ID
     * @param status  新状态
     * @return 是否更新成功
     */
    @PutMapping("/{deviceId}/status")
    public boolean updateStatus(
            @PathVariable String deviceId,
            @RequestParam String status) {
        return deviceService.updateStatus(deviceId, status);
    }

    /**
     * 解绑设备
     *
     * @param deviceId 设备 ID
     * @param openId  用户 OpenID
     * @return 是否解绑成功
     */
    @DeleteMapping("/{deviceId}/unbind")
    public boolean unbind(
            @PathVariable String deviceId,
            @RequestParam String openId) {
        return deviceService.unbind(deviceId, openId);
    }

    /**
     * 删除设备
     *
     * @param deviceId 设备 ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{deviceId}")
    public boolean delete(@PathVariable String deviceId) {
        return deviceService.delete(deviceId);
    }

    /**
     * 设备上线（心跳）
     *
     * @param deviceId 设备 ID
     * @return 是否成功
     */
    @PostMapping("/{deviceId}/heartbeat")
    public boolean heartbeat(@PathVariable String deviceId) {
        return deviceService.updateStatus(deviceId, "online");
    }
}
