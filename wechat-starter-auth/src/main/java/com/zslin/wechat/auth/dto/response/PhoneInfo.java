package com.zslin.wechat.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 手机号信息
 * <p>
 * 包含用户绑定的手机号完整信息
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneInfo {

    /**
     * 用户绑定的手机号（国外手机号会有区号）
     */
    private String phoneNumber;

    /**
     * 没有区号的手机号
     */
    private String purePhoneNumber;

    /**
     * 区号
     */
    private String countryCode;

    /**
     * 获取手机号操作的时间戳
     */
    private Long timestamp;

    /**
     * 小程序 appid
     */
    private String appId;
}
