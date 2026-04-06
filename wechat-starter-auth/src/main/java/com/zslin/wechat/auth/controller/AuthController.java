package com.zslin.wechat.auth.controller;

import com.zslin.wechat.auth.dto.request.LoginRequest;
import com.zslin.wechat.auth.dto.response.AuthResult;
import com.zslin.wechat.auth.service.WechatAuthService;
import com.zslin.wechat.core.constant.ErrorConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 授权控制层
 * <p>
 * 提供登录相关的 REST API 接口
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private WechatAuthService wechatAuthService;

    /**
     * 小程序登录
     * <p>
     * 接受小程序传来的 code，换取用户的 OpenID 和 SessionKey
     * </p>
     *
     * @param request 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {
        log.info("收到登录请求：code={}", request.getCode());

        try {
            AuthResult result = wechatAuthService.login(request.getCode());

            Map<String, Object> response = new HashMap<>();
            response.put("code", ErrorConstants.SUCCESS_CODE);
            response.put("message", ErrorConstants.SUCCESS_MSG);
            response.put("data", result);

            log.info("登录成功：openid={}", result.getOpenid());
            return response;

        } catch (Exception e) {
            log.error("登录失败：code={}", request.getCode(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("code", ErrorConstants.ERROR_CODE);
            error.put("message", e.getMessage());
            return error;
        }
    }

    /**
     * 获取用户信息
     * <p>
     * 根据 OpenID 获取用户详细信息
     * </p>
     *
     * @param openId 用户 OpenID
     * @return 用户信息
     */
    @GetMapping("/user/{openId}")
    public Map<String, Object> getUserInfo(@PathVariable String openId) {
        log.info("获取用户信息请求：openId={}", openId);

        try {
            // TODO: 实现获取用户信息逻辑
            Map<String, Object> response = new HashMap<>();
            response.put("code", ErrorConstants.SUCCESS_CODE);
            response.put("message", ErrorConstants.SUCCESS_MSG);
            response.put("data", wechatAuthService.getUserInfo(openId));
            return response;

        } catch (Exception e) {
            log.error("获取用户信息失败：openId={}", openId, e);
            Map<String, Object> error = new HashMap<>();
            error.put("code", ErrorConstants.ERROR_CODE);
            error.put("message", e.getMessage());
            return error;
        }
    }

    /**
     * 刷新 Token
     * <p>
     * 刷新 Access Token
     * </p>
     *
     * @param accessToken 旧的 Access Token
     * @return 新的登录结果
     */
    @PostMapping("/refresh")
    public Map<String, Object> refreshToken(@RequestBody Map<String, String> request) {
        String accessToken = request.get("accessToken");
        log.info("刷新 Token 请求");

        try {
            AuthResult result = wechatAuthService.refreshToken(accessToken);

            Map<String, Object> response = new HashMap<>();
            response.put("code", ErrorConstants.SUCCESS_CODE);
            response.put("message", ErrorConstants.SUCCESS_MSG);
            response.put("data", result);

            return response;

        } catch (Exception e) {
            log.error("刷新 Token 失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("code", ErrorConstants.ERROR_CODE);
            error.put("message", e.getMessage());
            return error;
        }
    }
}
