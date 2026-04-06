package com.zslin.wechat.auth.service;

import com.zslin.wechat.auth.dto.request.PhoneDecryptRequest;
import com.zslin.wechat.auth.dto.response.PhoneDecryptResult;

/**
 * 手机号解密服务
 */
public interface PhoneDecryptService {
    
    /**
     * 解密手机号
     * 
     * @param request 解密请求（包含 encryptedData、sessionKey、iv）
     * @return 手机号解密结果
     */
    PhoneDecryptResult decrypt(PhoneDecryptRequest request);
    
    /**
     * 解密手机号（简化版）
     * 
     * @param encryptedData 用户数据
     * @param sessionKey 会话密钥
     * @param iv 初始化向量
     * @return 手机号解密结果
     */
    PhoneDecryptResult decrypt(String encryptedData, String sessionKey, String iv);
}
