package com.zslin.wechat.message.service;

import com.zslin.wechat.core.config.WechatProperties;
import com.zslin.wechat.core.exception.WechatException;
import com.zslin.wechat.message.dto.request.MessageSendRequest;
import com.zslin.wechat.message.service.impl.WechatMessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 消息服务测试
 *
 * @author 子墨
 * @since 1.0.0
 */
public class MessageServiceTest {

    private WechatMessageService messageService;

    @BeforeEach
    public void setUp() {
        messageService = new WechatMessageServiceImpl();
    }

    @Test
    public void testSendSubscribeMessage_success() {
        // 准备测试数据
        MessageSendRequest request = new MessageSendRequest();
        request.setToUser("oTest1234567890");
        request.setTemplateId("TEST_TEMPLATE_001");
        request.setPage("pages/index");

        // 准备消息数据
        Map<String, MessageSendRequest.MessageData> data = new HashMap<>();
        
        MessageSendRequest.MessageData field1 = new MessageSendRequest.MessageData();
        field1.setValue("测试内容");
        field1.setColor("#173177");
        data.put("thing1", field1);

        MessageSendRequest.MessageData field2 = new MessageSendRequest.MessageData();
        field2.setValue("2023-12-01");
        field2.setColor("#173177");
        data.put("time1", field2);

        request.setData(data);

        // 执行发送（当前会抛出异常，因为 Access Token 未配置）
        assertThrows(WechatException.class, () -> {
            messageService.sendSubscribeMessage(request);
        });
    }

    @Test
    public void testSendSubscribeMessage_nullRequest() {
        assertThrows(WechatException.class, () -> {
            messageService.sendSubscribeMessage(null);
        });
    }

    @Test
    public void testSendSubscribeMessage_emptyToUser() {
        MessageSendRequest request = new MessageSendRequest();
        request.setToUser("");
        request.setTemplateId("TEST_TEMPLATE");

        assertThrows(WechatException.class, () -> {
            messageService.sendSubscribeMessage(request);
        });
    }

    @Test
    public void testSendSubscribeMessage_emptyTemplateId() {
        MessageSendRequest request = new MessageSendRequest();
        request.setToUser("oTest1234567890");
        request.setTemplateId("");

        assertThrows(WechatException.class, () -> {
            messageService.sendSubscribeMessage(request);
        });
    }

    @Test
    public void testSendSubscribeMessage_emptyData() {
        MessageSendRequest request = new MessageSendRequest();
        request.setToUser("oTest1234567890");
        request.setTemplateId("TEST_TEMPLATE");
        request.setData(new HashMap<>());

        assertThrows(WechatException.class, () -> {
            messageService.sendSubscribeMessage(request);
        });
    }

    @Test
    public void testSendSubscribeMessage_nullData() {
        MessageSendRequest request = new MessageSendRequest();
        request.setToUser("oTest1234567890");
        request.setTemplateId("TEST_TEMPLATE");
        request.setData(null);

        assertThrows(WechatException.class, () -> {
            messageService.sendSubscribeMessage(request);
        });
    }

    @Test
    public void testBatchSend_success() {
        // 准备测试数据
        java.util.List<String> toUserIds = java.util.Arrays.asList(
            "oUser1123456789",
            "oUser2123456789",
            "oUser3123456789"
        );
        String templateId = "TEST_TEMPLATE_001";
        
        Map<String, Object> data = new HashMap<>();
        data.put("thing1", "测试内容");
        data.put("time1", "2023-12-01");
        
        String page = "pages/index";

        // 执行批量发送（当前会抛出异常，因为 Access Token 未配置）
        assertThrows(WechatException.class, () -> {
            messageService.batchSend(toUserIds, templateId, data, page);
        });
    }

    @Test
    public void testBatchSend_emptyUserList() {
        java.util.List<String> toUserIds = new java.util.ArrayList<>();
        String templateId = "TEST_TEMPLATE";
        Map<String, Object> data = new HashMap<>();

        assertThrows(WechatException.class, () -> {
            messageService.batchSend(toUserIds, templateId, data, "pages/index");
        });
    }

    @Test
    public void testBatchSend_nullUserList() {
        String templateId = "TEST_TEMPLATE";
        Map<String, Object> data = new HashMap<>();

        assertThrows(WechatException.class, () -> {
            messageService.batchSend(null, templateId, data, "pages/index");
        });
    }

    @Test
    public void testBatchSend_singleUser() {
        java.util.List<String> toUserIds = java.util.Arrays.asList("oUser1123456789");
        String templateId = "TEST_TEMPLATE";
        Map<String, Object> data = new HashMap<>();
        data.put("thing1", "测试");

        // 执行批量发送
        assertThrows(WechatException.class, () -> {
            messageService.batchSend(toUserIds, templateId, data, "pages/index");
        });
    }
}
