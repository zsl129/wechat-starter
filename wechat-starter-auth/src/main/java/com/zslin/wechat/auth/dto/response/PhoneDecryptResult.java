package com.zslin.wechat.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 手机号解密结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneDecryptResult {
    
    /**
     * 纯文本手机号
     */
    private String purePhoneNumber;
    
    /**
     * 手机号国家码（如 86）
     */
    private String phoneNumberCountryCode;
    
    /**
     * 完整手机号（包含国家码）
     */
    private String waterMarkedPhoneNumber;
}
