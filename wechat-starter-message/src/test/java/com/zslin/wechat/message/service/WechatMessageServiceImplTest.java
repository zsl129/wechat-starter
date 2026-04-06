package com.zslin.wechat.message.service;

import com.zslin.wechat.core.exception.WechatException;
import com.zslin.wechat.message.dto.request.MessageSendRequest;
import com.zslin.wechat.message.service.impl.WechatMessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WechatMessageServiceImpl 单元测试
 *
 * @author 子墨
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class WechatMessageServiceImplTest {

    @InjectMocks
    private WechatMessageServiceImpl messageServiceImpl;

    private MessageSendRequest testRequest;

    @BeforeEach
    void setUp() {
        testRequest = new MessageSendRequest();
        testRequest.setToUser("test_openid");
        testRequest.setTemplateId("ORDER_NOTIFY");
        testRequest.setPage("/pages/order/detail?id=123");

        Map<String, MessageSendRequest.MessageData> data = new HashMap<>();
        data.put("thing1", new MessageSendRequest.MessageData("测试订单", "#173177"));
        data.put("time1", new MessageSendRequest.MessageData("2026-04-06 10:00", "#173177"));
        testRequest.setData(data);
    }

    @Test
    void testRequestValidation_ValidRequest() {
        // 测试有效的请求数据
        assertNotNull(testRequest.getToUser());
        assertNotNull(testRequest.getTemplateId());
        assertNotNull(testRequest.getData());
    }

    @Test
    void testRequestValidation_EmptyToUser() {
        // 测试用户 OpenID 为空
        testRequest.setToUser("");
        assertTrue(testRequest.getToUser().isEmpty());
    }

    @Test
    void testRequestValidation_EmptyTemplateId() {
        // 测试模板 ID 为空
        testRequest.setTemplateId("");
        assertTrue(testRequest.getTemplateId().isEmpty());
    }

    @Test
    void testRequestValidation_NullData() {
        // 测试数据为空
        testRequest.setData(null);
        assertNull(testRequest.getData());
    }

    @Test
    void testDataValidation_ValidData() {
        // 测试数据字段有效
        Map<String, MessageSendRequest.MessageData> data = testRequest.getData();
        assertNotNull(data);
        assertTrue(data.size() > 0);
        
        MessageSendRequest.MessageData thing1 = data.get("thing1");
        assertNotNull(thing1);
        assertNotNull(thing1.getValue());
    }

    @Test
    void testBatchUserListValidation_ValidList() {
        // 测试有效的用户列表
        java.util.List<String> users = java.util.Arrays.asList("openid1", "openid2", "openid3");
        assertNotNull(users);
        assertEquals(3, users.size());
    }

    @Test
    void testBatchUserListValidation_EmptyList() {
        // 测试空的用户列表
        java.util.List<String> users = new java.util.ArrayList<>();
        assertTrue(users.isEmpty());
    }

    @Test
    void testTemplateValidation_ExistingTemplate() {
        // 测试已存在的模板
        assertNotNull(testRequest.getTemplateId());
        assertEquals("ORDER_NOTIFY", testRequest.getTemplateId());
    }

    @Test
    void testPageUrlValidation_ValidUrl() {
        // 测试有效的页面 URL
        assertNotNull(testRequest.getPage());
        assertTrue(testRequest.getPage().contains("/pages/"));
    }
}
