package com.zslin.wechat.iot.device.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 设备实体
 * <p>
 * 代表一个 IoT 设备的基本信息
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备 ID（唯一标识）
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 设备类型（如：sensor、controller、actuator）
     */
    private String type;

    /**
     * 设备状态（online、offline、error）
     */
    private String status;

    /**
     * 绑定的用户 OpenID
     */
    private String ownerOpenId;

    /**
     * 设备标识（如 MAC 地址、序列号）
     */
    private String deviceIdentify;

    /**
     * 设备密钥（用于通信加密）
     */
    private String deviceSecret;

    /**
     * 最后在线时间
     */
    private Date lastOnlineTime;

    /**
     * 注册时间
     */
    private Date registerTime;

    /**
     * 设备版本
     */
    private String version;

    /**
     * 扩展信息（JSON 格式）
     */
    private String extraInfo;

    /**
     * 检查设备是否在线
     */
    public boolean isOnline() {
        return "online".equals(status);
    }

    /**
     * 检查设备是否属于某个用户
     */
    public boolean belongsTo(String openId) {
        return ownerOpenId != null && ownerOpenId.equals(openId);
    }
}
