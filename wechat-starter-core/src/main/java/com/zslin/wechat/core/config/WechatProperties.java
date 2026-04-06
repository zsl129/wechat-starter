package com.zslin.wechat.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信配置属性
 * <p>
 * 用于从 application.yml 中读取微信相关配置
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WechatProperties {

    /**
     * 小程序配置
     */
    private MiniApp miniapp = new MiniApp();

    /**
     * 支付配置
     */
    private Pay pay = new Pay();

    /**
     * Redis 配置（可选）
     */
    private Redis redis = new Redis();

    /**
     * 小程序配置项
     */
    @Data
    public static class MiniApp {
        /**
         * 小程序 AppID
         */
        private String appId;

        /**
         * 小程序 AppSecret
         */
        private String appSecret;

        /**
         * 接口令牌（用于服务器端请求校验）
         */
        private String token;

        /**
         * 消息加解密密钥
         */
        private String aesKey;

        /**
         * 授权回调路径
         */
        private String tokenPath = "/wechat/callback";
    }

    /**
     * 支付配置项
     */
    @Data
    public static class Pay {
        /**
         * 商户号
         */
        private String mchId;

        /**
         * API 密钥 v2
         */
        private String apiKey;

        /**
         * API 密钥 v3
         */
        private String apiV3Key;

        /**
         * 商户证书路径
         */
        private String certPath;

        /**
         * 商户证书密码
         */
        private String certPassword = "123456"; // 默认密码

        /**
         * 回调通知地址
         */
        private String notifyUrl;

        /**
         * 是否使用沙箱环境
         */
        private boolean sandbox = false;
        
        /**
         * 微信支付 API 基础 URL
         */
        private String apiUrl = "https://api.mch.weixin.qq.com";
        
        /**
         * 商户证书序列号
         */
        private String serialNo;
        
        /**
         * 商户私钥 PEM 格式（base64）
         */
        private String privateKeyPem;
        
        /**
         * 微信支付公钥证书 PEM 格式（base64）
         */
        private String wechatPublicKeyPem;
    }

    /**
     * Redis 配置项
     */
    @Data
    public static class Redis {
        /**
         * 是否启用 Redis
         */
        private boolean enabled = false;

        /**
         * Redis 主机地址
         */
        private String host = "localhost";

        /**
         * Redis 端口
         */
        private int port = 6379;

        /**
         * Redis 密码
         */
        private String password;

        /**
         * Redis 数据库编号
         */
        private int database = 0;

        /**
         * 默认过期时间（秒）
         */
        private long expireSeconds = 7200;

        /**
         * 连接超时时间（毫秒）
         */
        private int timeout = 3000;
    }
}
