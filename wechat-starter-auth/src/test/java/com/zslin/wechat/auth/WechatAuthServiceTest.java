package com.zslin.wechat.auth;

import com.zslin.wechat.auth.service.impl.WechatAuthServiceImpl;
import com.zslin.wechat.core.exception.WechatException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WechatAuthService 测试
 *
 * @author 子墨
 */
public class WechatAuthServiceTest {

    @Test
    void testLoginWithEmptyCode() {
        // 测试空 code 抛出异常
        WechatAuthServiceImpl service = new WechatAuthServiceImpl();
        
        assertThrows(WechatException.class, () -> {
            service.login("");
        });
    }

    @Test
    void testLoginWithNullCode() {
        // 测试 null code 抛出异常
        WechatAuthServiceImpl service = new WechatAuthServiceImpl();
        
        assertThrows(WechatException.class, () -> {
            service.login(null);
        });
    }

    @Test
    void testGetUserInfoWithEmptyOpenId() {
        // 测试空 OpenID 抛出异常
        WechatAuthServiceImpl service = new WechatAuthServiceImpl();
        
        assertThrows(WechatException.class, () -> {
            service.getUserInfo("");
        });
    }

    @Test
    void testRefreshTokenWithNullToken() {
        // 测试 null token 抛出异常
        WechatAuthServiceImpl service = new WechatAuthServiceImpl();
        
        assertThrows(WechatException.class, () -> {
            service.refreshToken(null);
        });
    }
}
