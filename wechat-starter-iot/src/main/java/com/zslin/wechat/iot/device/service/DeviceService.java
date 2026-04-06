package com.zslin.wechat.iot.device.service;

import com.zslin.wechat.iot.device.entity.Device;

import java.util.List;

/**
 * 设备服务接口
 * <p>
 * 提供设备管理功能：注册、查询、解绑等
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
public interface DeviceService {

    /**
     * 注册设备
     *
     * @param device 设备信息
     * @return 注册成功的设备
     */
    Device register(Device device);

    /**
     * 根据设备 ID 查询设备
     *
     * @param deviceId 设备 ID
     * @return 设备信息
     */
    Device findById(String deviceId);

    /**
     * 根据用户 ID 查询设备列表
     *
     * @param openId 用户 OpenID
     * @return 设备列表
     */
    List<Device> findByUser(String openId);

    /**
     * 更新设备状态
     *
     * @param deviceId  设备 ID
     * @param status   新状态
     * @return true-更新成功
     */
    boolean updateStatus(String deviceId, String status);

    /**
     * 解绑设备
     *
     * @param deviceId 设备 ID
     * @param openId  用户 OpenID（用于验证）
     * @return true-解绑成功
     */
    boolean unbind(String deviceId, String openId);

    /**
     * 删除设备
     *
     * @param deviceId 设备 ID
     * @return true-删除成功
     */
    boolean delete(String deviceId);
}
