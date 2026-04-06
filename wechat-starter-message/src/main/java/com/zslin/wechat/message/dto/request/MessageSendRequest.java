package com.zslin.wechat.message.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 消息发送请求 DTO
 * <p>
 * 支持灵活的订阅消息发送，字段动态配置
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageSendRequest {

    /**
     * 用户 openid
     */
    private String toUser;

    /**
     * 模板 ID
     */
    private String templateId;

    /**
     * 页面路径（用户点击消息跳转）
     */
    private String page;

    /**
     * 消息字段（动态，支持不同模板的不同字段）
     * Key: 字段名（如 thing1, thing2, time1 等）
     * Value: 字段值
     */
    private Map<String, Object> miniprogramState;

    /**
     * 消息内容字段
     * Key: 字段名（对应模板中的字段名）
     * Value: 字段值对象（包含 value 和 color）
     */
    private Map<String, MessageData> data;

    /**
     * 消息数据（单个字段）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageData {
        /**
         * 字段值
         */
        private String value;

        /**
         * 字体颜色（16 进制，默认为黑色）
         */
        private String color = "#173177";
    }
}
