package com.zslin.wechat.core.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WechatProperties 测试
 *
 * @author 子墨
 */
public class WechatPropertiesTest {

    @Test
    void testDefaultValues() {
        WechatProperties properties = new WechatProperties();
        
        // 测试默认值
        assertNotNull(properties.getMiniapp());
        assertNotNull(properties.getPay());
        assertNotNull(properties.getRedis());
        
        // 测试 Redis 默认值
        assertFalse(properties.getRedis().isEnabled());
        assertEquals("localhost", properties.getRedis().getHost());
        assertEquals(6379, properties.getRedis().getPort());
        assertEquals(7200, properties.getRedis().getExpireSeconds());
    }

    @Test
    void testMiniAppProperties() {
        WechatProperties.MiniApp miniApp = new WechatProperties.MiniApp();
        miniApp.setAppId("wx123");
        miniApp.setAppSecret("secret123");
        
        assertEquals("wx123", miniApp.getAppId());
        assertEquals("secret123", miniApp.getAppSecret());
        assertEquals("/wechat/callback", miniApp.getTokenPath());
    }

    @Test
    void testPayProperties() {
        WechatProperties.Pay pay = new WechatProperties.Pay();
        pay.setMchId("123456");
        pay.setApiKey("key123");
        
        assertEquals("123456", pay.getMchId());
        assertEquals("key123", pay.getApiKey());
        assertFalse(pay.isSandbox());
        assertEquals("123456", pay.getCertPassword());
    }
}
