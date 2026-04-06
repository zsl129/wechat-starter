package com.zslin.wechat.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录请求 DTO
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * 微信登录凭证（小程序登录时获取的 code）
     */
    private String code;

    /**
     * 用户自定义数据（可选，用于扩展业务需求）
     */
    private String extraData;
}
