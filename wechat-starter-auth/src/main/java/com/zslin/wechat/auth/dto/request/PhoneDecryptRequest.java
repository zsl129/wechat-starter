package com.zslin.wechat.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 手机号解密请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneDecryptRequest {
    
    /**
     * 用户数据（前端传来的 encryptedData）
     */
    private String encryptedData;
    
    /**
     * 会话密钥（sessionKey）
     */
    private String sessionKey;
    
    /**
     * 初始化向量（iv）
     */
    private String iv;
}
