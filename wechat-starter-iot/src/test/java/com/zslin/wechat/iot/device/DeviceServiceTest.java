package com.zslin.wechat.iot.device.service;

import com.zslin.wechat.core.exception.WechatException;
import com.zslin.wechat.iot.device.entity.Device;
import com.zslin.wechat.iot.device.service.impl.DeviceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 设备服务测试
 *
 * @author 子墨
 * @since 1.0.0
 */
public class DeviceServiceTest {

    private DeviceService deviceService;

    @BeforeEach
    public void setUp() {
        deviceService = new DeviceServiceImpl();
    }

    @Test
    public void testRegister_success() {
        // 准备测试数据
        Device device = new Device();
        device.setName("测试设备");
        device.setType("sensor");
        device.setDeviceIdentify("MAC_00:11:22:33:44:55");
        device.setOwnerOpenId("oTest1234567890");

        // 执行注册
        Device result = deviceService.register(device);

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getDeviceId());
        assertNotNull(result.getDeviceSecret());
        assertEquals("测试设备", result.getName());
        assertEquals("sensor", result.getType());
        assertEquals("offline", result.getStatus());
        assertEquals("oTest1234567890", result.getOwnerOpenId());
        assertNotNull(result.getRegisterTime());
    }

    @Test
    public void testRegister_duplicateIdentify() {
        // 注册第一个设备
        Device device1 = new Device();
        device1.setName("测试设备 1");
        device1.setType("sensor");
        device1.setDeviceIdentify("MAC_00:11:22:33:44:55");

        Device result1 = deviceService.register(device1);
        assertNotNull(result1);

        // 注册第二个设备（相同的标识）
        Device device2 = new Device();
        device2.setName("测试设备 2");
        device2.setType("sensor");
        device2.setDeviceIdentify("MAC_00:11:22:33:44:55");

        // 应该抛出异常
        assertThrows(WechatException.class, () -> {
            deviceService.register(device2);
        });
    }

    @Test
    public void testRegister_invalidName() {
        Device device = new Device();
        device.setName("");
        device.setType("sensor");
        device.setDeviceIdentify("MAC_00:11:22:33:44:55");

        assertThrows(WechatException.class, () -> {
            deviceService.register(device);
        });
    }

    @Test
    public void testRegister_invalidType() {
        Device device = new Device();
        device.setName("测试设备");
        device.setType("");
        device.setDeviceIdentify("MAC_00:11:22:33:44:55");

        assertThrows(WechatException.class, () -> {
            deviceService.register(device);
        });
    }

    @Test
    public void testRegister_invalidIdentify() {
        Device device = new Device();
        device.setName("测试设备");
        device.setType("sensor");
        device.setDeviceIdentify("");

        assertThrows(WechatException.class, () -> {
            deviceService.register(device);
        });
    }

    @Test
    public void testRegister_nullRequest() {
        assertThrows(WechatException.class, () -> {
            deviceService.register(null);
        });
    }

    @Test
    public void testFindByDeviceId_success() {
        // 先注册设备
        Device device = new Device();
        device.setName("测试设备");
        device.setType("sensor");
        device.setDeviceIdentify("MAC_00:11:22:33:44:55");
        
        Device registered = deviceService.register(device);
        String deviceId = registered.getDeviceId();

        // 查询设备
        Device result = deviceService.findById(deviceId);

        // 验证结果
        assertNotNull(result);
        assertEquals(deviceId, result.getDeviceId());
        assertEquals("测试设备", result.getName());
        assertNull(result.getDeviceSecret()); // 不应该返回密钥
    }

    @Test
    public void testFindByDeviceId_notFound() {
        String nonExistentDeviceId = "NON_EXISTENT_DEVICE_ID";

        // 应该抛出异常
        assertThrows(WechatException.class, () -> {
            deviceService.findById(nonExistentDeviceId);
        });
    }

    @Test
    public void testFindByDeviceId_nullId() {
        assertThrows(WechatException.class, () -> {
            deviceService.findById(null);
        });
    }

    @Test
    public void testFindByUser_success() {
        String userOpenId = "oTest1234567890";

        // 注册多个设备
        Device device1 = new Device();
        device1.setName("设备 1");
        device1.setType("sensor");
        device1.setDeviceIdentify("MAC_00:11:22:33:44:55");
        device1.setOwnerOpenId(userOpenId);
        deviceService.register(device1);

        Device device2 = new Device();
        device2.setName("设备 2");
        device2.setType("controller");
        device2.setDeviceIdentify("MAC_00:11:22:33:44:56");
        device2.setOwnerOpenId(userOpenId);
        deviceService.register(device2);

        // 查询用户设备
        List<Device> results = deviceService.findByUser(userOpenId);

        // 验证结果
        assertNotNull(results);
        assertEquals(2, results.size());
        
        // 验证设备不包含密钥
        for (Device device : results) {
            assertNull(device.getDeviceSecret());
        }
    }

    @Test
    public void testFindByUser_emptyResult() {
        String userOpenId = "oNonExistentUser123";

        List<Device> results = deviceService.findByUser(userOpenId);

        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    public void testUpdateStatus_success() {
        // 先注册设备
        Device device = new Device();
        device.setName("测试设备");
        device.setType("sensor");
        device.setDeviceIdentify("MAC_00:11:22:33:44:55");
        
        Device registered = deviceService.register(device);
        String deviceId = registered.getDeviceId();

        // 更新状态
        boolean result = deviceService.updateStatus(deviceId, "online");

        assertTrue(result);

        // 验证状态已更新
        Device updated = deviceService.findById(deviceId);
        assertEquals("online", updated.getStatus());
        assertNotNull(updated.getLastOnlineTime());
    }

    @Test
    public void testUpdateStatus_notFound() {
        String nonExistentDeviceId = "NON_EXISTENT_DEVICE_ID";

        assertThrows(WechatException.class, () -> {
            deviceService.updateStatus(nonExistentDeviceId, "online");
        });
    }

    @Test
    public void testUnbind_success() {
        String userOpenId = "oTest1234567890";

        // 注册设备
        Device device = new Device();
        device.setName("测试设备");
        device.setType("sensor");
        device.setDeviceIdentify("MAC_00:11:22:33:44:55");
        device.setOwnerOpenId(userOpenId);
        
        Device registered = deviceService.register(device);
        String deviceId = registered.getDeviceId();

        // 解绑设备
        boolean result = deviceService.unbind(deviceId, userOpenId);

        assertTrue(result);

        // 验证设备已解绑
        Device unbound = deviceService.findById(deviceId);
        assertNull(unbound.getOwnerOpenId());
        assertEquals("offline", unbound.getStatus());
    }

    @Test
    public void testUnbind_wrongOwner() {
        String userOpenId1 = "oUser1123456789";
        String userOpenId2 = "oUser2123456789";

        // 注册用户 1 的设备
        Device device = new Device();
        device.setName("测试设备");
        device.setType("sensor");
        device.setDeviceIdentify("MAC_00:11:22:33:44:55");
        device.setOwnerOpenId(userOpenId1);
        
        Device registered = deviceService.register(device);
        String deviceId = registered.getDeviceId();

        // 用户 2 尝试解绑（应该失败）
        assertThrows(WechatException.class, () -> {
            deviceService.unbind(deviceId, userOpenId2);
        });
    }

    @Test
    public void testDelete_success() {
        // 注册设备
        Device device = new Device();
        device.setName("测试设备");
        device.setType("sensor");
        device.setDeviceIdentify("MAC_00:11:22:33:44:55");
        
        Device registered = deviceService.register(device);
        String deviceId = registered.getDeviceId();

        // 删除设备
        boolean result = deviceService.delete(deviceId);

        assertTrue(result);

        // 验证设备已删除
        assertThrows(WechatException.class, () -> {
            deviceService.findById(deviceId);
        });
    }
}
