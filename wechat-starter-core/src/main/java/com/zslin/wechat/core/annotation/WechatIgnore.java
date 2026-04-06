package com.zslin.wechat.core.annotation;

import java.lang.annotation.*;

/**
 * 微信忽略注解
 * <p>
 * 用于标记在特定场景下需要忽略的字段或方法
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WechatIgnore {

    /**
     * 忽略的原因
     *
     * @return 原因描述
     */
    String value() default "";
}
