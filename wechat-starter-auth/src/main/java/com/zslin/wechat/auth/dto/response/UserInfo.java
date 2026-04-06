package com.zslin.wechat.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息 DTO
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户性别（1-男，2-女，0-未知）
     */
    private Integer gender;

    /**
     * 用户头像 URL
     */
    private String avatarUrl;

    /**
     * 用户城市
     */
    private String city;

    /**
     * 用户省份
     */
    private String province;

    /**
     * 用户国家
     */
    private String country;

    /**
     * 用户语言
     */
    private String language;

    /**
     * 小程序版本
     */
    private String appVersion;
}
