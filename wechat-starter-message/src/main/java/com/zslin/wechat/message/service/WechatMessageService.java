package com.zslin.wechat.message.service;

import com.zslin.wechat.message.dto.request.MessageSendRequest;
import com.zslin.wechat.message.dto.response.MessageSendResult;

/**
 * 微信消息服务接口
 * <p>
 * 提供微信订阅消息发送功能
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
public interface WechatMessageService {

    /**
     * 发送订阅消息
     *
     * @param request 消息请求
     * @return 发送结果
     */
    MessageSendResult sendSubscribeMessage(MessageSendRequest request);

    /**
     * 批量发送消息
     *
     * @param toUserIds    用户 ID 列表
     * @param templateId   模板 ID
     * @param data         消息数据
     * @param page         页面路径
     * @return 发送结果列表
     */
    java.util.List<MessageSendResult> batchSend(
            java.util.List<String> toUserIds,
            String templateId,
            java.util.Map<String, Object> data,
            String page);
}
