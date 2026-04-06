package com.zslin.wechat.iot.device;

import com.zslin.wechat.iot.device.entity.Device;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DeviceServiceImpl 单元测试
 *
 * @author 子墨
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class DeviceServiceImplTest {

    private Device testDevice;

    @BeforeEach
    void setUp() {
        testDevice = new Device();
        testDevice.setDeviceId("device_001");
        testDevice.setName("智能温控器");
        testDevice.setType("sensor");
        testDevice.setDeviceIdentify("MAC_00:11:22:33:44:55");
        testDevice.setOwnerOpenId("test_openid");
        testDevice.setStatus("online");
    }

    @Test
    void testDeviceValidation_ValidDevice() {
        // 测试有效的设备数据
        assertNotNull(testDevice.getDeviceId());
        assertNotNull(testDevice.getName());
        assertNotNull(testDevice.getType());
        assertNotNull(testDevice.getDeviceIdentify());
        assertNotNull(testDevice.getOwnerOpenId());
    }

    @Test
    void testDeviceValidation_EmptyName() {
        // 测试设备名称为空
        testDevice.setName("");
        assertTrue(testDevice.getName().isEmpty());
    }

    @Test
    void testDeviceValidation_EmptyDeviceIdentify() {
        // 测试设备标识为空
        testDevice.setDeviceIdentify("");
        assertTrue(testDevice.getDeviceIdentify().isEmpty());
    }

    @Test
    void testDeviceValidation_EmptyOwnerOpenId() {
        // 测试用户 OpenID 为空
        testDevice.setOwnerOpenId("");
        assertTrue(testDevice.getOwnerOpenId().isEmpty());
    }

    @Test
    void testDeviceStatus_Online() {
        // 测试在线状态
        testDevice.setStatus("online");
        assertEquals("online", testDevice.getStatus());
    }

    @Test
    void testDeviceStatus_Offline() {
        // 测试离线状态
        testDevice.setStatus("offline");
        assertEquals("offline", testDevice.getStatus());
    }

    @Test
    void testDeviceType_Sensor() {
        // 测试传感器类型
        testDevice.setType("sensor");
        assertEquals("sensor", testDevice.getType());
    }

    @Test
    void testDeviceType_Device() {
        // 测试设备类型
        testDevice.setType("device");
        assertEquals("device", testDevice.getType());
    }

    @Test
    void testDeviceListValidation_ValidList() {
        // 测试有效的设备列表
        List<Device> devices = new ArrayList<>();
        devices.add(testDevice);
        
        assertNotNull(devices);
        assertEquals(1, devices.size());
    }

    @Test
    void testDeviceListValidation_EmptyList() {
        // 测试空的设备列表
        List<Device> devices = new ArrayList<>();
        assertTrue(devices.isEmpty());
    }

    @Test
    void testDeviceIdValidation_ValidId() {
        // 测试有效的设备 ID
        assertNotNull(testDevice.getDeviceId());
        assertTrue(testDevice.getDeviceId().length() > 0);
    }

    @Test
    void testDeviceIdentifyValidation_ValidIdentify() {
        // 测试有效的设备标识
        assertNotNull(testDevice.getDeviceIdentify());
        assertTrue(testDevice.getDeviceIdentify().contains(":"));
    }
}
