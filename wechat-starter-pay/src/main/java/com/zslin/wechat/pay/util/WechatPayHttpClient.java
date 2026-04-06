package com.zslin.wechat.pay.util;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 微信支付 HTTP 工具类
 * <p>
 * 封装微信支付 V3 API 的 HTTP 调用
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Component
public class WechatPayHttpClient {

    private static final Logger log = LoggerFactory.getLogger(WechatPayHttpClient.class);

    private static final String WECHAT_PAY_BASE_URL = "https://api.mch.weixin.qq.com";

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    /**
     * 发送 GET 请求
     *
     * @param url 请求 URL
     * @param headers 请求头
     * @return 响应体
     */
    public String get(String url, okhttp3.Headers headers) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .headers(headers)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP 请求失败：" + response.code());
            }
            return response.body().string();
        }
    }

    /**
     * 发送 POST 请求
     *
     * @param url 请求 URL
     * @param body 请求体
     * @param headers 请求头
     * @return 响应体
     */
    public String post(String url, String body, okhttp3.Headers headers) throws IOException {
        RequestBody requestBody = RequestBody.create(body, MediaType.get("application/json"));
        
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .headers(headers)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP 请求失败：" + response.code());
            }
            return response.body().string();
        }
    }

    /**
     * 发送 PUT 请求
     *
     * @param url 请求 URL
     * @param body 请求体
     * @param headers 请求头
     * @return 响应体
     */
    public String put(String url, String body, okhttp3.Headers headers) throws IOException {
        RequestBody requestBody = RequestBody.create(body, MediaType.get("application/json"));
        
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .headers(headers)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP 请求失败：" + response.code());
            }
            return response.body().string();
        }
    }
}
