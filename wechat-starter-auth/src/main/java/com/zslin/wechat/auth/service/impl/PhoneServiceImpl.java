package com.zslin.wechat.auth.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zslin.wechat.auth.dto.response.PhoneInfo;
import com.zslin.wechat.auth.service.PhoneService;
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

/**
 * 手机号服务实现
 * <p>
 * 提供微信小程序手机号解密功能
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Service
public class PhoneServiceImpl implements PhoneService {

    private static final Logger log = LoggerFactory.getLogger(PhoneServiceImpl.class);

    private static final String GET_PHONE_NUMBER_API = 
            "https://api.weixin.qq.com/wxa/business/getuserphonenumber";

    @Autowired
    private AccessTokenService accessTokenService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    public PhoneInfo getPhoneNumber(String code) {
        log.debug("开始获取手机号，code={}", code);

        if (code == null || code.trim().isEmpty()) {
            throw new WechatException(WechatExceptionCodes.PHONE_CODE_EMPTY);
        }

        // 获取 access_token
        String accessToken = accessTokenService.getAccessToken();

        // 构建请求 URL
        String url = String.format("%s?access_token=%s", GET_PHONE_NUMBER_API, accessToken);

        // 构建请求体
        String requestBody = String.format("{\"code\":\"%s\"}", code);

        try {
            // 创建请求
            RequestBody body = RequestBody.create(requestBody, MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            // 执行请求
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("获取手机号失败，HTTP 状态码：{}", response.code());
                    throw new WechatException(WechatExceptionCodes.PHONE_DECRYPT_FAILED);
                }

                String responseBody = response.body().string();
                log.debug("获取手机号响应：{}", responseBody);

                // 解析响应
                JsonNode root = objectMapper.readTree(responseBody);

                // 检查错误码
                int errcode = root.path("errcode").asInt(0);
                if (errcode != 0) {
                    String errmsg = root.path("errmsg").asText("未知错误");
                    log.error("获取手机号失败：errcode={}, errmsg={}", errcode, errmsg);
                    throw new WechatException(WechatExceptionCodes.PHONE_DECRYPT_FAILED, 
                            new RuntimeException(String.format("微信 API 错误：%s - %s", errcode, errmsg)));
                }

                // 解析手机号信息
                JsonNode phoneInfoNode = root.path("phone_info");
                PhoneInfo phoneInfo = new PhoneInfo();
                
                phoneInfo.setPhoneNumber(phoneInfoNode.path("phoneNumber").asText());
                phoneInfo.setPurePhoneNumber(phoneInfoNode.path("purePhoneNumber").asText());
                phoneInfo.setCountryCode(phoneInfoNode.path("countryCode").asText());
                phoneInfo.setTimestamp(phoneInfoNode.path("watermark").path("timestamp").asLong());
                phoneInfo.setAppId(phoneInfoNode.path("watermark").path("appid").asText());

                log.info("获取手机号成功：phoneNumber={}", phoneInfo.getPhoneNumber());

                return phoneInfo;
            }
        } catch (IOException e) {
            log.error("获取手机号网络请求失败", e);
            throw new WechatException(WechatExceptionCodes.PHONE_DECRYPT_FAILED, e);
        }
    }
}
