package com.zslin.wechat.auth.controller;

import com.zslin.wechat.auth.dto.request.PhoneRequest;
import com.zslin.wechat.auth.dto.response.PhoneInfo;
import com.zslin.wechat.auth.service.PhoneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 手机号控制器
 * <p>
 * 提供手机号获取和解密接口
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/phone")
public class PhoneController {

    private static final Logger log = LoggerFactory.getLogger(PhoneController.class);

    @Autowired
    private PhoneService phoneService;

    /**
     * 获取用户手机号
     *
     * @param request 手机号获取请求
     * @return 手机号信息
     */
    @PostMapping("/get")
    public Map<String, Object> getPhoneNumber(@RequestBody PhoneRequest request) {
        log.info("收到手机号获取请求：code={}", request.getCode());

        try {
            PhoneInfo phoneInfo = phoneService.getPhoneNumber(request.getCode());

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", phoneInfo);

            log.info("手机号获取成功：phoneNumber={}", phoneInfo.getPhoneNumber());
            return response;
        } catch (Exception e) {
            log.error("手机号获取失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", e.getMessage());
            return error;
        }
    }
}
