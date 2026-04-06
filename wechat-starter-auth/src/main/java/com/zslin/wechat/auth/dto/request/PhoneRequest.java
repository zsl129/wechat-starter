package com.zslin.wechat.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 手机号获取请求
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneRequest {

    /**
     * 手机号获取凭证
     * <p>
     * 每个 code 只能使用一次，有效期为 5 分钟
     * </p>
     */
    private String code;
}
