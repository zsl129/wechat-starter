package com.zslin.wechat.iot.protocol.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zslin.wechat.iot.sensor.dto.SensorData;
import com.zslin.wechat.iot.sensor.handler.SensorDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * HTTP 协议适配器
 * <p>
 * 将 HTTP 请求转换为传感器数据
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Component
public class HttpProtocolAdapter {

    private static final Logger log = LoggerFactory.getLogger(HttpProtocolAdapter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SensorDataHandler sensorDataHandler;

    /**
     * 处理 HTTP 传感器数据上报
     *
     * @param deviceId   设备 ID
     * @param sensorType 传感器类型
     * @param value      数据值
     * @param unit       单位
     * @return 处理结果
     */
    public SensorData handleSensorData(String deviceId, String sensorType, Double value, String unit) {
        log.debug("处理 HTTP 传感器数据：deviceId={}, sensorType={}, value={}", 
            deviceId, sensorType, value);

        return sensorDataHandler.receiveData(deviceId, sensorType, value, unit);
    }

    /**
     * 处理 HTTP 批量数据上报
     *
     * @param payload JSON 数组：[{"deviceId":"xxx","sensorType":"xxx","value":123,"unit":"xxx"},...]
     * @return 处理结果列表
     */
    public List<SensorData> handleBatchData(String payload) {
        log.debug("处理 HTTP 批量数据上报");

        try {
            JsonNode arrayNode = objectMapper.readTree(payload);
            if (!arrayNode.isArray()) {
                log.warn("无效的批量数据格式：期待 JSON 数组");
                return new ArrayList<>();
            }

            List<SensorData> results = new ArrayList<>();
            for (JsonNode element : arrayNode) {
                String deviceId = element.path("deviceId").asText();
                String sensorType = element.path("sensorType").asText();
                Double value = element.path("value").asDouble(0.0);
                String unit = element.path("unit").asText();

                SensorData data = sensorDataHandler.receiveData(deviceId, sensorType, value, unit);
                results.add(data);
            }

            log.info("批量 HTTP 数据处理完成：count={}", results.size());
            return results;

        } catch (Exception e) {
            log.error("处理 HTTP 批量数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 处理设备状态上报
     *
     * @param deviceId 设备 ID
     * @param status  状态
     * @param extraData 额外数据（JSON）
     * @return 处理结果
     */
    public SensorData handleStatusUpdate(String deviceId, String status, String extraData) {
        log.debug("处理 HTTP 状态上报：deviceId={}, status={}", deviceId, status);

        // 将状态转换为数值（online=1, offline=0, error=-1）
        Double statusValue = 0.0;
        if ("online".equals(status)) {
            statusValue = 1.0;
        } else if ("offline".equals(status)) {
            statusValue = 0.0;
        } else if ("error".equals(status)) {
            statusValue = -1.0;
        }

        SensorData data = sensorDataHandler.receiveData(deviceId, "status", statusValue, "");
        
        if (extraData != null && !extraData.isEmpty()) {
            data.setExtraData(extraData);
        }

        return data;
    }
}
