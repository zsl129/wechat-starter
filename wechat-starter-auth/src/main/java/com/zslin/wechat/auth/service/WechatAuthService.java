package com.zslin.wechat.auth.service;

import com.zslin.wechat.auth.dto.response.AuthResult;
import com.zslin.wechat.auth.dto.response.UserInfo;

/**
 * 微信授权服务接口
 * <p>
 * 提供小程序登录、用户信息管理等功能
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
public interface WechatAuthService {

    /**
     * 小程序登录
     * <p>
     * 通过微信提供的 code 换取用户的 openid 和 sessionKey
     * </p>
     *
     * @param code 微信登录凭证
     * @return 登录结果
     */
    AuthResult login(String code);

    /**
     * 获取用户信息
     * <p>
     * 根据 OpenID 获取用户详细信息
     * </p>
     *
     * @param openId 用户 OpenID
     * @return 用户信息
     */
    UserInfo getUserInfo(String openId);

    /**
     * 刷新 Token
     * <p>
     * 刷新 Access Token
     * </p>
     *
     * @param accessToken 旧的 Access Token
     * @return 新的登录结果
     */
    AuthResult refreshToken(String accessToken);
}
