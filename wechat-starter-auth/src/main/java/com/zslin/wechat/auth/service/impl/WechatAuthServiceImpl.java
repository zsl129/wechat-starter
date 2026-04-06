package com.zslin.wechat.auth.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zslin.wechat.auth.dto.response.AuthResult;
import com.zslin.wechat.auth.dto.response.UserInfo;
import com.zslin.wechat.auth.service.WechatAuthService;
import com.zslin.wechat.core.config.WechatProperties;
import com.zslin.wechat.core.constant.ApiUrlConstants;
import com.zslin.wechat.core.exception.WechatException;
import com.zslin.wechat.core.exception.WechatExceptionCodes;
import com.zslin.wechat.core.util.JsonUtils;
import com.zslin.wechat.core.util.StringUtils;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 微信授权服务实现类
 * <p>
 * 实现小程序登录、用户信息获取等功能
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Service
public class WechatAuthServiceImpl implements WechatAuthService {

    private static final Logger log = LoggerFactory.getLogger(WechatAuthServiceImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WechatProperties properties;

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    public AuthResult login(String code) {
        log.debug("开始处理登录，code={}", code);

        // 参数校验
        if (StringUtils.isBlank(code)) {
            throw new WechatException(WechatExceptionCodes.CODE_EMPTY);
        }

        // 获取配置
        String appId = properties.getMiniapp().getAppId();
        String appSecret = properties.getMiniapp().getAppSecret();

        if (StringUtils.isBlank(appId) || StringUtils.isBlank(appSecret)) {
            throw new WechatException(WechatExceptionCodes.INVALID_CONFIG);
        }

        // 构建请求 URL
        String url = String.format(
                "%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                ApiUrlConstants.MINIAPP_JS_CODE2SESSION,
                appId, appSecret, code
        );

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("微信登录接口返回非成功状态：code={}, status={}", code, response.code());
                    throw new WechatException(WechatExceptionCodes.WECHAT_LOGIN_FAILED,
                            new RuntimeException("HTTP " + response.code()));
                }

                String responseBody = response.body().string();
                log.debug("微信登录接口返回：{}", responseBody);

                // 解析响应
                LoginResponse loginResponse = parseLoginResponse(responseBody);

                // 验证响应
                if (loginResponse.getErrcode() != null && loginResponse.getErrcode() > 0) {
                    log.error("微信登录失败：errcode={}, errmsg={}",
                            loginResponse.getErrcode(), loginResponse.getErrmsg());
                    throw new WechatException(WechatExceptionCodes.WECHAT_LOGIN_FAILED,
                            new RuntimeException(loginResponse.getErrmsg()));
                }

                if (StringUtils.isBlank(loginResponse.getOpenid())) {
                    log.error("微信登录返回的 openid 为空");
                    throw new WechatException(WechatExceptionCodes.WECHAT_LOGIN_FAILED,
                            new RuntimeException("openid is empty"));
                }

                log.info("登录成功，openid={}", loginResponse.getOpenid());

                // 返回登录结果
                return new AuthResult(
                        loginResponse.getOpenid(),
                        loginResponse.getSession_key(),
                        null,  // 用户信息需要额外获取
                        null,  // Access Token 需要后续生成
                        null   // 过期时间需要后续设置
                );
            }
        } catch (IOException e) {
            log.error("调用微信登录接口失败，code={}", code, e);
            throw new WechatException(WechatExceptionCodes.WECHAT_LOGIN_FAILED, e);
        } catch (Exception e) {
            log.error("登录处理异常，code={}", code, e);
            throw new WechatException(WechatExceptionCodes.WECHAT_LOGIN_FAILED, e);
        }
    }

    /**
     * 解析登录响应
     */
    private LoginResponse parseLoginResponse(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            
            LoginResponse response = new LoginResponse();
            response.setOpenid(node.has("openid") ? node.get("openid").asText() : null);
            response.setSession_key(node.has("session_key") ? node.get("session_key").asText() : null);
            response.setErrcode(node.has("errcode") ? node.get("errcode").asInt() : null);
            response.setErrmsg(node.has("errmsg") ? node.get("errmsg").asText() : null);
            
            return response;
        } catch (Exception e) {
            log.error("解析登录响应失败：{}", json, e);
            throw new WechatException(WechatExceptionCodes.WECHAT_LOGIN_FAILED, e);
        }
    }

    @Override
    public UserInfo getUserInfo(String openId) {
        log.debug("获取用户信息，openId={}", openId);
        
        if (StringUtils.isBlank(openId)) {
            throw new WechatException(WechatExceptionCodes.USER_NOT_FOUND);
        }
        
        // TODO: 实现从 Redis 或数据库获取用户信息
        // 目前返回空对象，实际使用时需要结合业务实现
        return new UserInfo();
    }

    @Override
    public AuthResult refreshToken(String accessToken) {
        log.debug("刷新 Token，accessToken={}", accessToken);
        
        if (StringUtils.isBlank(accessToken)) {
            throw new WechatException(WechatExceptionCodes.TOKEN_INVALID);
        }
        
        // TODO: 实现 Token 刷新逻辑
        // 目前返回空对象，实际使用时需要结合业务实现
        return new AuthResult();
    }

    /**
     * 登录响应内部类
     */
    private static class LoginResponse {
        private String openid;
        private String session_key;
        private Integer errcode;
        private String errmsg;

        public String getOpenid() { return openid; }
        public void setOpenid(String openid) { this.openid = openid; }
        public String getSession_key() { return session_key; }
        public void setSession_key(String session_key) { this.session_key = session_key; }
        public Integer getErrcode() { return errcode; }
        public void setErrcode(Integer errcode) { this.errcode = errcode; }
        public String getErrmsg() { return errmsg; }
        public void setErrmsg(String errmsg) { this.errmsg = errmsg; }
    }
}
