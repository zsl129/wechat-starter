package com.zslin.wechat.core.service.impl;

import com.zslin.wechat.core.config.WechatProperties;
import com.zslin.wechat.core.exception.WechatException;
import com.zslin.wechat.core.exception.WechatExceptionCodes;
import com.zslin.wechat.core.service.AccessTokenService;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Access Token 服务实现
 * <p>
 * 管理微信接口调用凭据，支持自动刷新
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    private static final Logger log = LoggerFactory.getLogger(AccessTokenServiceImpl.class);

    private static final String GET_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";

    @Autowired
    private WechatProperties properties;

    private final AtomicReference<String> tokenRef = new AtomicReference<>();
    private final AtomicReference<Long> expireTimeRef = new AtomicReference<>();

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    public String getAccessToken() {
        // 检查是否需要刷新
        if (!isValid()) {
            log.debug("Access Token 已过期或不存在，开始刷新");
            return refreshAccessToken();
        }
        return tokenRef.get();
    }

    @Override
    public String refreshAccessToken() {
        String appId = properties.getMiniapp().getAppId();
        String appSecret = properties.getMiniapp().getAppSecret();

        if (appId == null || appId.trim().isEmpty() || appSecret == null || appSecret.trim().isEmpty()) {
            throw new WechatException(WechatExceptionCodes.INVALID_CONFIG,
                    new RuntimeException("小程序 appId 或 appSecret 未配置"));
        }

        // 构建请求 URL
        String url = String.format(
                "%s?grant_type=client_credential&appid=%s&secret=%s",
                GET_TOKEN_URL, appId, appSecret
        );

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("获取 Access Token 失败，HTTP 状态码：{}", response.code());
                    throw new WechatException(WechatExceptionCodes.WECHAT_LOGIN_FAILED,
                            new RuntimeException("HTTP " + response.code()));
                }

                String responseBody = response.body().string();
                log.debug("获取 Access Token 响应：{}", responseBody);

                // 解析响应
                com.fasterxml.jackson.databind.JsonNode root = 
                        new com.fasterxml.jackson.databind.ObjectMapper()
                                .readTree(responseBody);

                int errcode = root.path("errcode").asInt(0);
                if (errcode != 0) {
                    String errmsg = root.path("errmsg").asText("未知错误");
                    log.error("获取 Access Token 失败：errcode={}, errmsg={}", errcode, errmsg);
                    throw new WechatException(WechatExceptionCodes.WECHAT_LOGIN_FAILED,
                            new RuntimeException(String.format("微信 API 错误：%s - %s", errcode, errmsg)));
                }

                String accessToken = root.path("access_token").asText();
                int expiresIn = root.path("expires_in").asInt(7200);

                // 更新缓存（减去 5 分钟缓冲）
                tokenRef.set(accessToken);
                expireTimeRef.set(System.currentTimeMillis() + (expiresIn - 300) * 1000);

                log.info("Access Token 刷新成功，有效期：{} 秒", expiresIn);

                return accessToken;
            }
        } catch (IOException e) {
            log.error("获取 Access Token 网络请求失败", e);
            throw new WechatException(WechatExceptionCodes.WECHAT_LOGIN_FAILED, e);
        }
    }

    @Override
    public boolean isValid() {
        Long expireTime = expireTimeRef.get();
        return expireTime != null && System.currentTimeMillis() < expireTime;
    }
}
