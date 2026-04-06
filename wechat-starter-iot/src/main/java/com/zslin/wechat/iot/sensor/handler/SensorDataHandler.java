package com.zslin.wechat.iot.sensor.handler;

import com.zslin.wechat.core.util.JsonUtils;
import com.zslin.wechat.iot.sensor.dto.SensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 传感器数据处理类
 * <p>
 * 处理传感器数据的接收、存储和推送
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Component
public class SensorDataHandler {

    private static final Logger log = LoggerFactory.getLogger(SensorDataHandler.class);

    /**
     * 传感器数据队列（用于异步处理）
     */
    private final ConcurrentLinkedQueue<SensorData> dataQueue = new ConcurrentLinkedQueue<>();

    /**
     * 数据接收监听器列表
     */
    private final List<DataListener> listeners = new ArrayList<>();

    /**
     * 接收传感器数据
     *
     * @param deviceId   设备 ID
     * @param sensorType 传感器类型
     * @param value      数据值
     * @param unit       单位
     * @return 处理结果
     */
    public SensorData receiveData(String deviceId, String sensorType, Double value, String unit) {
        log.debug("接收传感器数据：deviceId={}, sensorType={}, value={}", 
            deviceId, sensorType, value);

        SensorData data = createSensorData(deviceId, sensorType, value, unit);
        
        // 添加到队列
        dataQueue.offer(data);
        
        // 通知监听器
        notifyListeners(data);
        
        // TODO: 实现数据持久化
        // saveToDatabase(data);
        
        log.debug("传感器数据处理完成：dataId={}", data.getDataId());
        return data;
    }

    /**
     * 接收批量传感器数据
     *
     * @param deviceId   设备 ID
     * @param sensorData 传感器数据 JSON 字符串
     * @return 处理结果列表
     */
    public List<SensorData> receiveBatchData(String deviceId, String sensorData) {
        log.info("接收批量传感器数据：deviceId={}", deviceId);

        try {
            // 解析 JSON 数据
            // 这里简化处理，实际应该解析具体的 JSON 格式
            
            // 示例：接收一个默认数据
            SensorData data = receiveData(deviceId, "default", 0.0, "unit");
            return new ArrayList<>(java.util.Collections.singletonList(data));
            
        } catch (Exception e) {
            log.error("解析传感器数据失败：deviceId={}", deviceId, e);
            throw new RuntimeException("解析传感器数据失败", e);
        }
    }

    /**
     * 注册数据监听器
     *
     * @param listener 监听器
     */
    public void registerListener(DataListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            log.info("注册数据监听器：{}", listener.getClass().getSimpleName());
        }
    }

    /**
     * 移除数据监听器
     *
     * @param listener 监听器
     */
    public void unregisterListener(DataListener listener) {
        if (listeners.remove(listener)) {
            log.info("移除数据监听器：{}", listener.getClass().getSimpleName());
        }
    }

    /**
     * 获取最近 N 条数据
     *
     * @param deviceId 设备 ID
     * @param limit   数据条数
     * @return 数据列表
     */
    public List<SensorData> getRecentData(String deviceId, int limit) {
        List<SensorData> result = new ArrayList<>();
        
        for (SensorData data : dataQueue) {
            if (data.getDeviceId().equals(deviceId)) {
                result.add(data);
                if (result.size() >= limit) {
                    break;
                }
            }
        }
        
        return result;
    }

    // ==================== 私有方法 ====================

    /**
     * 创建传感器数据
     */
    private SensorData createSensorData(String deviceId, String sensorType, Double value, String unit) {
        SensorData data = new SensorData();
        data.setDataId(generateDataId());
        data.setDeviceId(deviceId);
        data.setSensorType(sensorType);
        data.setValue(value);
        data.setUnit(unit);
        data.setTimestamp(System.currentTimeMillis());
        data.setQuality(0); // 默认正常
        
        return data;
    }

    /**
     * 生成数据 ID
     */
    private String generateDataId() {
        return "DATA_" + System.currentTimeMillis() + "_" + 
               UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    /**
     * 通知所有监听器
     */
    private void notifyListeners(SensorData data) {
        for (DataListener listener : listeners) {
            try {
                listener.onDataReceived(data);
            } catch (Exception e) {
                log.error("通知监听器失败：{}", listener.getClass().getSimpleName(), e);
            }
        }
    }

    /**
     * 数据监听器接口
     */
    public interface DataListener {
        /**
         * 数据接收回调
         *
         * @param data 传感器数据
         */
        void onDataReceived(SensorData data);
    }
}
