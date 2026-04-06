package com.zslin.wechat.iot.sensor.controller;

import com.zslin.wechat.iot.sensor.dto.SensorData;
import com.zslin.wechat.iot.sensor.handler.SensorDataHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 传感器数据控制器
 * <p>
 * 提供传感器数据的 HTTP 接口
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/iot/sensor")
public class SensorController {

    @Autowired
    private SensorDataHandler sensorDataHandler;

    /**
     * 接收传感器数据
     *
     * @param deviceId   设备 ID
     * @param sensorType 传感器类型
     * @param value      数据值
     * @param unit       单位
     * @return 处理结果
     */
    @PostMapping("/data")
    public SensorData receiveData(
            @RequestParam String deviceId,
            @RequestParam String sensorType,
            @RequestParam Double value,
            @RequestParam(required = false, defaultValue = "") String unit) {
        return sensorDataHandler.receiveData(deviceId, sensorType, value, unit);
    }

    /**
     * 接收批量传感器数据
     *
     * @param deviceId   设备 ID
     * @param sensorData 传感器数据 JSON
     * @return 处理结果列表
     */
    @PostMapping("/data/batch")
    public List<SensorData> receiveBatchData(
            @RequestParam String deviceId,
            @RequestBody String sensorData) {
        return sensorDataHandler.receiveBatchData(deviceId, sensorData);
    }

    /**
     * 获取设备最近的数据
     *
     * @param deviceId 设备 ID
     * @param limit   数据条数
     * @return 数据列表
     */
    @GetMapping("/data/{deviceId}")
    public List<SensorData> getRecentData(
            @PathVariable String deviceId,
            @RequestParam(defaultValue = "100") int limit) {
        return sensorDataHandler.getRecentData(deviceId, limit);
    }

    /**
     * 设备上报温度数据
     *
     * @param deviceId 设备 ID
     * @param value   温度值
     * @return 处理结果
     */
    @PostMapping("/temperature")
    public SensorData reportTemperature(
            @RequestParam String deviceId,
            @RequestParam Double value) {
        return sensorDataHandler.receiveData(deviceId, "temperature", value, "°C");
    }

    /**
     * 设备上报湿度数据
     *
     * @param deviceId 设备 ID
     * @param value   湿度值
     * @return 处理结果
     */
    @PostMapping("/humidity")
    public SensorData reportHumidity(
            @RequestParam String deviceId,
            @RequestParam Double value) {
        return sensorDataHandler.receiveData(deviceId, "humidity", value, "%");
    }

    /**
     * 设备上报压力数据
     *
     * @param deviceId 设备 ID
     * @param value   压力值
     * @return 处理结果
     */
    @PostMapping("/pressure")
    public SensorData reportPressure(
            @RequestParam String deviceId,
            @RequestParam Double value) {
        return sensorDataHandler.receiveData(deviceId, "pressure", value, "hPa");
    }
}
