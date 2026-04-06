package com.zslin.wechat.auth.service;

import com.zslin.wechat.auth.dto.response.PhoneInfo;

/**
 * 手机号服务接口
 * <p>
 * 提供小程序手机号获取和解密功能
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
public interface PhoneService {

    /**
     * 获取用户手机号
     * <p>
     * 通过手机号凭证 code 换取用户手机号信息
     * 每个 code 只能使用一次，有效期为 5 分钟
     * </p>
     *
     * @param code 手机号获取凭证
     * @return 手机号信息（包含完整手机号、纯手机号、区号）
     */
    PhoneInfo getPhoneNumber(String code);
}
