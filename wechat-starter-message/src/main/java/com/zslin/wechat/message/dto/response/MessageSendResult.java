package com.zslin.wechat.message.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息发送结果 DTO
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageSendResult {

    /**
     * 是否发送成功
     */
    private boolean success;

    /**
     * 错误码（0 表示成功）
     */
    private Integer errCode;

    /**
     * 错误信息
     */
    private String errMsg;

    /**
     * 发送时间戳
     */
    private Long timestamp;

    /**
     * 用户 openid
     */
    private String toUser;

    /**
     * 模板 ID
     */
    private String templateId;

    /**
     * 消息 ID（微信返回）
     */
    private String msgId;

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return success && (errCode == null || errCode == 0);
    }
}
