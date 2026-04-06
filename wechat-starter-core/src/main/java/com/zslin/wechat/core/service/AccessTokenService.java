package com.zslin.wechat.core.service;

/**
 * Access Token 服务接口
 * <p>
 * 提供微信接口调用凭据的管理功能
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
public interface AccessTokenService {

    /**
     * 获取 Access Token
     * <p>
     * 自动判断是否过期，过期则重新获取
     * </p>
     *
     * @return Access Token
     */
    String getAccessToken();

    /**
     * 强制刷新 Access Token
     * <p>
     * 忽略缓存，直接向微信服务器请求新的 Access Token
     * </p>
     *
     * @return 新的 Access Token
     */
    String refreshAccessToken();

    /**
     * 检查 Access Token 是否有效
     *
     * @return true 如果有效，false 如果过期或无效
     */
    boolean isValid();
}
