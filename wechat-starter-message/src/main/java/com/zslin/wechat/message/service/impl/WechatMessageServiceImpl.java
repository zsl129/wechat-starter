package com.zslin.wechat.message.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zslin.wechat.core.config.WechatProperties;
import com.zslin.wechat.core.constant.ApiUrlConstants;
import com.zslin.wechat.core.exception.WechatException;
import com.zslin.wechat.core.exception.WechatExceptionCodes;
import com.zslin.wechat.core.util.JsonUtils;
import com.zslin.wechat.core.util.StringUtils;
import com.zslin.wechat.message.dto.request.MessageSendRequest;
import com.zslin.wechat.message.dto.response.MessageSendResult;
import com.zslin.wechat.message.service.WechatMessageService;
import com.zslin.wechat.message.template.MessageTemplateConfig;
import com.zslin.wechat.message.template.MessageTemplateManager;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 微信消息服务实现类
 * <p>
 * 实现订阅消息发送功能，支持灵活的模板管理
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Service
public class WechatMessageServiceImpl implements WechatMessageService {

    private static final Logger log = LoggerFactory.getLogger(WechatMessageServiceImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WechatProperties properties;

    @Autowired
    private MessageTemplateManager templateManager;

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    public MessageSendResult sendSubscribeMessage(MessageSendRequest request) {
        log.info("开始发送订阅消息：toUser={}, templateId={}", 
                request.getToUser(), request.getTemplateId());

        // 参数校验
        validateRequest(request);

        // 检查模板是否存在
        MessageTemplateConfig templateConfig = templateManager.getTemplate(request.getTemplateId());
        if (templateConfig == null) {
            log.warn("模板未配置，但仍可尝试发送：templateId={}", request.getTemplateId());
        } else if (!templateConfig.isEnabled()) {
            throw new WechatException(WechatExceptionCodes.MESSAGE_SEND_FAILED, 
                String.format("模板已禁用：{}", request.getTemplateId()));
        }

        // 验证消息数据
        if (request.getData() != null && templateConfig != null) {
            Map<String, Object> simpleData = convertToSimpleData(request.getData());
            if (!templateManager.validateData(request.getTemplateId(), simpleData)) {
                throw new WechatException(WechatExceptionCodes.MESSAGE_PARAM_ERROR,
                    String.format("消息数据不符合模板要求：{}", request.getTemplateId()));
            }
        }

        try {
            // 构建请求参数
            Map<String, Object> requestBody = buildRequestParams(request);
            String jsonBody = JsonUtils.toJson(requestBody);

            log.debug("消息请求参数：{}", maskSensitiveInfo(jsonBody));

            // 调用微信订阅消息 API
            MessageSendResult result = callWechatMessageApi(request);
            
            log.info("发送订阅消息成功：toUser={}, templateId={}, msgId={}", 
                request.getToUser(), request.getTemplateId(), result.getMsgId());
            
            return result;

        } catch (WechatException e) {
            log.error("发送消息失败（业务异常）：{} - {}", 
                request.getTemplateId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("发送消息失败（系统异常）：{}", request.getTemplateId(), e);
            throw new WechatException(WechatExceptionCodes.MESSAGE_SEND_FAILED, 
                "发送消息失败：" + e.getMessage(), e);
        }
    }

    @Override
    public List<MessageSendResult> batchSend(
            List<String> toUserIds, 
            String templateId, 
            Map<String, Object> data, 
            String page) {
        
        log.info("批量发送消息：count={}, templateId={}", toUserIds.size(), templateId);

        if (toUserIds == null || toUserIds.isEmpty()) {
            throw new WechatException(WechatExceptionCodes.MESSAGE_PARAM_ERROR, "用户 ID 列表不能为空");
        }

        List<MessageSendResult> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (String toUser : toUserIds) {
            try {
                MessageSendRequest request = new MessageSendRequest();
                request.setToUser(toUser);
                request.setTemplateId(templateId);
                request.setPage(page);
                request.setData(convertDataToMessageData(data));
                
                MessageSendResult result = sendSubscribeMessage(request);
                results.add(result);
                
                if (result.isSuccess()) {
                    successCount++;
                } else {
                    failCount++;
                }

            } catch (Exception e) {
                log.error("批量发送消息失败：toUser={}", toUser, e);
                
                MessageSendResult errorResult = new MessageSendResult();
                errorResult.setSuccess(false);
                errorResult.setErrCode(500);
                errorResult.setErrMsg("发送失败：" + e.getMessage());
                errorResult.setToUser(toUser);
                errorResult.setTemplateId(templateId);
                results.add(errorResult);
                failCount++;
            }
        }

        log.info("批量发送完成：total={}, success={}, fail={}", 
            toUserIds.size(), successCount, failCount);
        
        return results;
    }

    // ==================== 私有方法 ====================

    /**
     * 校验请求参数
     */
    private void validateRequest(MessageSendRequest request) {
        if (request == null) {
            throw new WechatException(WechatExceptionCodes.MESSAGE_PARAM_ERROR, "消息请求不能为空");
        }

        if (StringUtils.isBlank(request.getToUser())) {
            throw new WechatException(WechatExceptionCodes.MESSAGE_PARAM_ERROR, "用户 OpenID 不能为空");
        }

        if (StringUtils.isBlank(request.getTemplateId())) {
            throw new WechatException(WechatExceptionCodes.MESSAGE_TEMPLATE_NOT_FOUND, "模板 ID 不能为空");
        }

        if (request.getData() == null || request.getData().isEmpty()) {
            throw new WechatException(WechatExceptionCodes.MESSAGE_PARAM_ERROR, "消息数据不能为空");
        }
    }

    /**
     * 构建请求参数
     */
    private Map<String, Object> buildRequestParams(MessageSendRequest request) {
        Map<String, Object> params = new HashMap<>();
        
        params.put("touser", request.getToUser());
        params.put("template_id", request.getTemplateId());
        
        if (StringUtils.isNotBlank(request.getPage())) {
            params.put("page", request.getPage());
        }
        
        params.put("data", convertDataToApiFormat(request.getData()));
        
        return params;
    }

    /**
     * 转换数据格式为 API 格式
     */
    private Map<String, Object> convertDataToApiFormat(Map<String, MessageSendRequest.MessageData> data) {
        Map<String, Object> result = new HashMap<>();
        
        for (Map.Entry<String, MessageSendRequest.MessageData> entry : data.entrySet()) {
            Map<String, Object> field = new HashMap<>();
            field.put("value", entry.getValue().getValue());
            field.put("color", entry.getValue().getColor());
            result.put(entry.getKey(), field);
        }
        
        return result;
    }

    /**
     * 转换简单数据到 MessageData 格式
     */
    private Map<String, MessageSendRequest.MessageData> convertDataToMessageData(Map<String, Object> data) {
        Map<String, MessageSendRequest.MessageData> result = new HashMap<>();
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            MessageSendRequest.MessageData messageData = new MessageSendRequest.MessageData();
            messageData.setValue(String.valueOf(entry.getValue()));
            messageData.setColor("#173177");
            result.put(entry.getKey(), messageData);
        }
        
        return result;
    }

    /**
     * 转换为简单数据（用于验证）
     */
    private Map<String, Object> convertToSimpleData(Map<String, MessageSendRequest.MessageData> data) {
        Map<String, Object> result = new HashMap<>();
        
        for (Map.Entry<String, MessageSendRequest.MessageData> entry : data.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getValue());
        }
        
        return result;
    }

    /**
     * 创建模拟发送结果
     */
    private MessageSendResult createMockSendResult(MessageSendRequest request) {
        MessageSendResult result = new MessageSendResult();
        result.setSuccess(true);
        result.setErrCode(0);
        result.setErrMsg("ok");
        result.setTimestamp(System.currentTimeMillis());
        result.setToUser(request.getToUser());
        result.setTemplateId(request.getTemplateId());
        result.setMsgId("mock_msg_" + System.currentTimeMillis());
        
        return result;
    }

    /**
     * 脱敏敏感信息（用于日志）
     */
    private String maskSensitiveInfo(String json) {
        if (json == null) {
            return "";
        }
        return json.replaceFirst("\"touser\":\"[^\"]+\"", "\"touser\":\"***\"");
    }

    // ==================== 微信 API 调用 ====================

    /**
     * 调用微信订阅消息 API
     */
    private MessageSendResult callWechatMessageApi(MessageSendRequest request) {
        // 获取 Access Token
        String accessToken = getAccessToken();
        
        String apiUrl = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + accessToken;
        
        try {
            String jsonBody = buildRequestJson(request);
            log.debug("消息请求 JSON: {}", maskSensitiveInfo(jsonBody));
            
            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
            Request httpRequest = new Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .build();
            
            try (Response response = httpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    log.error("调用微信消息 API 失败：status={}", response.code());
                    throw new WechatException(WechatExceptionCodes.MESSAGE_SEND_FAILED, 
                        "HTTP 错误：" + response.code());
                }
                
                String responseBody = response.body().string();
                log.debug("消息响应：{}", responseBody);
                
                return parseMessageResponse(responseBody);
            }
            
        } catch (WechatException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用微信消息 API 异常", e);
            throw new WechatException(WechatExceptionCodes.MESSAGE_SEND_FAILED, 
                "API 调用异常：" + e.getMessage(), e);
        }
    }

    /**
     * 获取 Access Token
     */
    private String getAccessToken() {
        // 这里应该从 AccessTokenService 获取，但由于模块依赖关系，暂时简化处理
        // TODO: 注入 AccessTokenService
        throw new WechatException(WechatExceptionCodes.MESSAGE_SEND_FAILED, 
            "Access Token 未配置，请配置 AccessTokenService");
    }

    /**
     * 构建请求 JSON
     */
    private String buildRequestJson(MessageSendRequest request) {
        Map<String, Object> params = buildRequestParams(request);
        return JsonUtils.toJson(params);
    }

    /**
     * 解析消息响应
     */
    private MessageSendResult parseMessageResponse(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            
            MessageSendResult result = new MessageSendResult();
            result.setErrCode(root.path("errcode").asInt(-1));
            result.setErrMsg(root.path("errmsg").asText("未知错误"));
            result.setMsgId(root.path("msgid").asText());
            result.setSuccess(result.getErrCode() == 0);
            result.setTimestamp(System.currentTimeMillis());
            
            return result;
            
        } catch (Exception e) {
            log.error("解析消息响应失败", e);
            throw new WechatException(WechatExceptionCodes.MESSAGE_SEND_FAILED, 
                "解析响应失败：" + e.getMessage(), e);
        }
    }
}
