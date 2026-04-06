package com.zslin.wechat.auth.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zslin.wechat.auth.dto.request.PhoneDecryptRequest;
import com.zslin.wechat.auth.dto.response.PhoneDecryptResult;
import com.zslin.wechat.auth.service.PhoneDecryptService;
import com.zslin.wechat.core.exception.WechatException;
import com.zslin.wechat.core.exception.WechatExceptionCodes;
import com.zslin.wechat.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 手机号解密服务实现
 * <p>
 * 解密小程序用户手机号
 * </p>
 * 
 * @author 子墨
 * @since 1.0.0
 * @see <a href="https://developers.weixin.qq.com/miniprogram/dev/api/open-api/phone/wx.getPhoneNumber.html">微信手机号解密</a>
 */
@Service
public class PhoneDecryptServiceImpl implements PhoneDecryptService {

    private static final Logger log = LoggerFactory.getLogger(PhoneDecryptServiceImpl.class);
    
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PhoneDecryptResult decrypt(PhoneDecryptRequest request) {
        log.debug("开始解密手机号（Request 对象）");
        
        if (request == null) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR);
        }
        
        return decrypt(request.getEncryptedData(), request.getSessionKey(), request.getIv());
    }

    @Override
    public PhoneDecryptResult decrypt(String encryptedData, String sessionKey, String iv) {
        log.debug("开始解密手机号");
        
        // 参数校验
        if (StringUtils.isBlank(encryptedData)) {
            throw new WechatException(WechatExceptionCodes.ENCRYPTED_DATA_EMPTY);
        }
        if (StringUtils.isBlank(sessionKey)) {
            throw new WechatException(WechatExceptionCodes.SESSION_KEY_EMPTY);
        }
        if (StringUtils.isBlank(iv)) {
            throw new WechatException(WechatExceptionCodes.IV_EMPTY);
        }

        try {
            // 1. 对 sessionKey 进行 Base64 解码
            byte[] sessionKeyBytes = Base64.getDecoder().decode(sessionKey);
            
            // 2. 对 sessionKey 进行 MD5 哈希（微信的要求）
            byte[] aesKey = md5(sessionKeyBytes);
            
            // 3. 初始化 AES 解密
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            javax.crypto.spec.IvParameterSpec ivSpec = new javax.crypto.spec.IvParameterSpec(
                Base64.getDecoder().decode(iv)
            );
            javax.crypto.spec.SecretKeySpec keySpec = new javax.crypto.spec.SecretKeySpec(aesKey, "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            
            // 4. 解密 encryptedData
            byte[] encryptedDataBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(encryptedDataBytes);
            String decryptedJson = new String(decryptedBytes, StandardCharsets.UTF_8);
            
            log.debug("解密后的 JSON: {}", decryptedJson);
            
            // 5. 解析 JSON
            JsonNode rootNode = objectMapper.readTree(decryptedJson);
            String purePhone = rootNode.path("purePhoneNumber").asText();
            String country = rootNode.path("phoneNumberCountryCode").asText();
            String waterMarkedPhone = rootNode.path("watermarkdedPhoneNumber").asText();
            
            // 6. 返回结果
            PhoneDecryptResult result = new PhoneDecryptResult();
            result.setPurePhoneNumber(purePhone);
            result.setPhoneNumberCountryCode(country);
            result.setWaterMarkedPhoneNumber(waterMarkedPhone);
            
            log.info("手机号解密成功：{}", purePhone);
            return result;
            
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5 算法不存在", e);
            throw new WechatException(WechatExceptionCodes.PHONE_DECRYPT_FAILED, e);
        } catch (Exception e) {
            log.error("手机号解密失败", e);
            throw new WechatException(WechatExceptionCodes.PHONE_DECRYPT_FAILED, e);
        }
    }
    
    /**
     * 计算 MD5 哈希
     * 
     * @param data 待哈希数据
     * @return MD5 哈希值
     * @throws NoSuchAlgorithmException MD5 算法不存在
     */
    private byte[] md5(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return md.digest(data);
    }
}
