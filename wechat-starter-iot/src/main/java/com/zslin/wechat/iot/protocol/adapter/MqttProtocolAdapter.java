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
 * MQTT 协议适配器
 * <p>
 * 将 MQTT 消息转换为传感器数据
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Component
public class MqttProtocolAdapter {

    private static final Logger log = LoggerFactory.getLogger(MqttProtocolAdapter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SensorDataHandler sensorDataHandler;

    /**
     * 处理 MQTT 消息
     *
     * @param topic  主题
     * @param payload 消息内容
     * @return 处理结果列表
     */
    public List<SensorData> handleMessage(String topic, String payload) {
        log.debug("处理 MQTT 消息：topic={}, payload={}", topic, payload);

        try {
            // 解析主题：device/{deviceId}/data/{sensorType}
            String[] topicParts = topic.split("/");
            if (topicParts.length < 4 || !topicParts[0].equals("device") || !topicParts[2].equals("data")) {
                log.warn("无效的 MQTT 主题格式：{}", topic);
                return new ArrayList<>();
            }

            String deviceId = topicParts[1];
            String sensorType = topicParts[3];

            // 解析消息内容
            JsonNode root = objectMapper.readTree(payload);
            
            Double value = root.path("value").asDouble(0.0);
            String unit = root.path("unit").asText("");

            SensorData data = sensorDataHandler.receiveData(deviceId, sensorType, value, unit);
            return new ArrayList<>(java.util.Collections.singletonList(data));

        } catch (Exception e) {
            log.error("处理 MQTT 消息失败：topic={}", topic, e);
            return new ArrayList<>();
        }
    }

    /**
     * 处理批量 MQTT 消息
     *
     * @param topic   主题
     * @param payload 消息内容（JSON 数组）
     * @return 处理结果列表
     */
    public List<SensorData> handleBatchMessage(String topic, String payload) {
        log.debug("处理批量 MQTT 消息：topic={}", topic);

        try {
            JsonNode arrayNode = objectMapper.readTree(payload);
            if (!arrayNode.isArray()) {
                log.warn("无效的批量消息格式：期待 JSON 数组");
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

            log.info("批量 MQTT 消息处理完成：count={}", results.size());
            return results;

        } catch (Exception e) {
            log.error("处理批量 MQTT 消息失败", e);
            return new ArrayList<>();
        }
    }
}
