package com.zslin.wechat.auth.service;

import com.zslin.wechat.auth.dto.request.PhoneDecryptRequest;
import com.zslin.wechat.auth.dto.response.PhoneDecryptResult;
import com.zslin.wechat.core.exception.WechatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 手机号解密服务测试
 */
@ExtendWith(MockitoExtension.class)
public class PhoneDecryptServiceImplTest {

    @InjectMocks
    private PhoneDecryptServiceImpl phoneDecryptService;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
    }

    @Test
    void testDecryptWithEmptyEncryptedData() {
        PhoneDecryptRequest request = new PhoneDecryptRequest("", "sessionKey", "iv");
        assertThrows(WechatException.class, () -> {
            phoneDecryptService.decrypt(request);
        });
    }

    @Test
    void testDecryptWithEmptySessionKey() {
        PhoneDecryptRequest request = new PhoneDecryptRequest("encryptedData", "", "iv");
        assertThrows(WechatException.class, () -> {
            phoneDecryptService.decrypt(request);
        });
    }

    @Test
    void testDecryptWithEmptyIv() {
        PhoneDecryptRequest request = new PhoneDecryptRequest("encryptedData", "sessionKey", "");
        assertThrows(WechatException.class, () -> {
            phoneDecryptService.decrypt(request);
        });
    }

    @Test
    void testDecryptWithNullRequest() {
        assertThrows(WechatException.class, () -> {
            phoneDecryptService.decrypt(null);
        });
    }

    @Test
    void testDecryptWithInvalidData() {
        // 使用无效的加密数据进行测试（应该会抛出异常）
        PhoneDecryptRequest request = new PhoneDecryptRequest(
            "invalid_data", 
            "AQICbgn5rGAzOeL6aQ3WuU8fVd0fH7a5fH7a5fH7a5f=", 
            "invalid_iv"
        );
        assertThrows(WechatException.class, () -> {
            phoneDecryptService.decrypt(request);
        });
    }
}
