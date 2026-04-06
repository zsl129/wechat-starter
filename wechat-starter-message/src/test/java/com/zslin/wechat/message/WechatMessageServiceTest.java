package com.zslin.wechat.message;

import com.zslin.wechat.message.dto.request.MessageSendRequest;
import com.zslin.wechat.message.service.impl.WechatMessageServiceImpl;
import com.zslin.wechat.core.exception.WechatException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 消息服务测试
 *
 * @author 子墨
 */
public class WechatMessageServiceTest {

    @Test
    void testSendMessageWithNullRequest() {
        WechatMessageServiceImpl service = new WechatMessageServiceImpl();
        assertThrows(NullPointerException.class, () -> {
            service.sendSubscribeMessage(null);
        });
    }

    @Test
    void testSendMessageWithEmptyToUser() {
        WechatMessageServiceImpl service = new WechatMessageServiceImpl();
        MessageSendRequest request = new MessageSendRequest();
        request.setToUser("");
        request.setTemplateId("TEST_TEMPLATE");
        request.setData(new java.util.HashMap<>());
        
        assertThrows(WechatException.class, () -> {
            service.sendSubscribeMessage(request);
        });
    }

    @Test
    void testSendMessageWithEmptyTemplateId() {
        WechatMessageServiceImpl service = new WechatMessageServiceImpl();
        MessageSendRequest request = new MessageSendRequest();
        request.setToUser("test_openid");
        request.setTemplateId("");
        request.setData(new java.util.HashMap<>());
        
        assertThrows(WechatException.class, () -> {
            service.sendSubscribeMessage(request);
        });
    }

    @Test
    void testSendMessageWithEmptyData() {
        WechatMessageServiceImpl service = new WechatMessageServiceImpl();
        MessageSendRequest request = new MessageSendRequest();
        request.setToUser("test_openid");
        request.setTemplateId("TEST_TEMPLATE");
        request.setData(new java.util.HashMap<>());
        
        assertThrows(WechatException.class, () -> {
            service.sendSubscribeMessage(request);
        });
    }
}
