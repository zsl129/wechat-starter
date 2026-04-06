package com.zslin.wechat.pay.callback;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zslin.wechat.core.config.WechatProperties;
import com.zslin.wechat.core.util.JsonUtils;
import com.zslin.wechat.core.util.SignUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

/**
 * 支付回调处理类
 * <p>
 * 处理微信支付的结果通知
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/pay")
public class PayCallbackHandler {

    private static final Logger log = LoggerFactory.getLogger(PayCallbackHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WechatProperties properties;

    /**
     * 支付结果回调
     * <p>
     * 微信支付的异步通知地址
     * </p>
     *
     * @param request HTTP 请求
     * @return 响应结果
     */
    @PostMapping("/notify")
    public String handleNotify(HttpServletRequest request) {
        log.info("收到支付回调通知");

        try {
            // 读取请求体
            String requestBody = readRequestBody(request);
            log.debug("回调数据：{}", requestBody);

            // 获取签名相关头部
            String timestamp = request.getHeader("Wechatpay-Timestamp");
            String nonce = request.getHeader("Wechatpay-Nonce");
            String sign = request.getHeader("Wechatpay-Signature");
            String serial = request.getHeader("Wechatpay-Serial");

            // 验签
            if (!verifySignature(requestBody, timestamp, nonce, sign)) {
                log.error("支付回调验签失败");
                return buildErrorResponse(40003, "签名验证失败");
            }

            // 解析回调数据
            PayNotifyData notifyData = parseNotifyData(requestBody);
            
            // 处理支付成功
            if ("SUCCESS".equals(notifyData.getEvent())) {
                handlePaySuccess(notifyData);
            }

            log.info("支付回调处理成功：orderId={}", notifyData.getOrderId());
            return buildSuccessResponse();

        } catch (Exception e) {
            log.error("处理支付回调失败", e);
            return buildErrorResponse(500, "处理失败：" + e.getMessage());
        }
    }

    /**
     * 验证签名
     */
    private boolean verifySignature(String payload, String timestamp, String nonce, String sign) {
        try {
            String signData = payload + "\n" + timestamp + "\n" + nonce + "\n";
            
            // 获取微信公钥
            String wechatPublicKeyPem = properties.getPay().getWechatPublicKeyPem();
            if (wechatPublicKeyPem == null || wechatPublicKeyPem.isEmpty()) {
                log.warn("微信公钥未配置，跳过验签");
                return true; // 开发环境临时跳过
            }
            
            PublicKey publicKey = SignUtils.loadPublicKeyFromPem(wechatPublicKeyPem);
            return SignUtils.verifyV3(signData, publicKey, sign);

        } catch (Exception e) {
            log.error("验签失败", e);
            return false;
        }
    }

    /**
     * 解析回调数据
     */
    private PayNotifyData parseNotifyData(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            
            PayNotifyData data = new PayNotifyData();
            data.setEvent(root.get("event").asText());
            
            JsonResource resource = root.get("resource").isObject() 
                ? new JsonResource(root.get("resource").get("resource_type").asText(), 
                                 root.get("resource").get("id").asText(),
                                 root.get("resource").get("out_id").asText())
                : null;
            data.setResource(resource);
            
            return data;
            
        } catch (Exception e) {
            log.error("解析回调数据失败", e);
            throw new RuntimeException("解析回调数据失败", e);
        }
    }

    /**
     * 处理支付成功
     */
    private void handlePaySuccess(PayNotifyData notifyData) {
        log.info("处理支付成功：orderId={}", notifyData.getOrderId());
        
        // TODO: 实现业务逻辑
        // 1. 更新订单状态
        // 2. 发送消息通知用户
        // 3. 记录日志
    }

    /**
     * 读取请求体
     */
    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    /**
     * 构建成功响应
     */
    private String buildSuccessResponse() {
        return "{\"code\":\"SUCCESS\",\"message\":\"成功\"}";
    }

    /**
     * 构建错误响应
     */
    private String buildErrorResponse(int code, String message) {
        return "{\"code\":" + code + ",\"message\":\"" + message + "\"}";
    }

    /**
     * 支付通知数据内部类
     */
    private static class PayNotifyData {
        private String event;
        private JsonResource resource;

        public String getEvent() { return event; }
        public void setEvent(String event) { this.event = event; }
        public JsonResource getResource() { return resource; }
        public void setResource(JsonResource resource) { this.resource = resource; }
        
        public String getOrderId() {
            return resource != null ? resource.getOutId() : null;
        }
    }

    /**
     * JSON 资源内部类
     */
    private static class JsonResource {
        private String resourceType;
        private String id;
        private String outId;

        public JsonResource(String resourceType, String id, String outId) {
            this.resourceType = resourceType;
            this.id = id;
            this.outId = outId;
        }

        public String getResourceType() { return resourceType; }
        public String getId() { return id; }
        public String getOutId() { return outId; }
    }
}
