package com.zslin.wechat.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 授权结果 DTO
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResult {

    /**
     * 用户唯一标识（OpenID）
     */
    private String openid;

    /**
     * 会话密钥（Session Key）
     */
    private String sessionKey;

    /**
     * 用户信息
     */
    private UserInfo userInfo;

    /**
     * 访问令牌（Access Token）
     */
    private String accessToken;

    /**
     * 过期时间（时间戳，秒）
     */
    private Long expireTime;
}
