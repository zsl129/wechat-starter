package com.zslin.wechat.core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信自动配置类
 * <p>
 * 自动配置微信相关的 Bean，实现即插即用
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(WechatProperties.class)
public class WechatAutoConfiguration {

    // 工具类都是静态方法，不需要注册为 Bean
    // 以后需要扩展的 Bean 可以在这里添加
}
