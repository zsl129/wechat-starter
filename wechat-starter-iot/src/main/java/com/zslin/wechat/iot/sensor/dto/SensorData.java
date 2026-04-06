package com.zslin.wechat.iot.sensor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 传感器数据 DTO
 * <p>
 * 代表一次传感器数据采集
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据 ID
     */
    private String dataId;

    /**
     * 设备 ID
     */
    private String deviceId;

    /**
     * 传感器类型（temperature、humidity、pressure 等）
     */
    private String sensorType;

    /**
     * 数据值
     */
    private Double value;

    /**
     * 单位
     */
    private String unit;

    /**
     * 数据时间戳
     */
    private Long timestamp;

    /**
     * 质量标志（0-正常，1-异常）
     */
    private Integer quality;

    /**
     * 扩展数据（JSON 格式）
     */
    private String extraData;

    /**
     * 获取采集时间
     */
    public Date getCollectTime() {
        return timestamp != null ? new Date(timestamp) : null;
    }

    /**
     * 检查数据是否正常
     */
    public boolean isNormal() {
        return quality == null || quality == 0;
    }
}
