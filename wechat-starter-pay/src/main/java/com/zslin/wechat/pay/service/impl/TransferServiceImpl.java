package com.zslin.wechat.pay.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zslin.wechat.core.config.WechatProperties;
import com.zslin.wechat.core.exception.WechatException;
import com.zslin.wechat.core.exception.WechatExceptionCodes;
import com.zslin.wechat.core.util.JsonUtils;
import com.zslin.wechat.core.util.SignUtils;
import com.zslin.wechat.pay.dto.request.RedPacketRequest;
import com.zslin.wechat.pay.dto.request.TransferRequest;
import com.zslin.wechat.pay.dto.response.RedPacketResult;
import com.zslin.wechat.pay.dto.response.TransferResult;
import com.zslin.wechat.pay.service.TransferService;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 商家转账服务实现
 *
 * @author 子墨
 * @since 1.0.0
 */
@Service
public class TransferServiceImpl implements TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferServiceImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WechatProperties properties;

    private PrivateKey privateKey;

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    /**
     * 初始化
     */
    private void init() {
        if (privateKey == null) {
            try {
                String privateKeyPem = properties.getPay().getPrivateKeyPem();
                if (privateKeyPem != null && !privateKeyPem.isEmpty()) {
                    this.privateKey = SignUtils.loadPrivateKeyFromPem(privateKeyPem);
                    log.info("转账服务私钥加载成功");
                } else {
                    log.warn("转账服务私钥未配置，使用模拟模式");
                }
            } catch (Exception e) {
                log.error("转账服务初始化失败", e);
            }
        }
    }

    @Override
    public TransferResult transfer(TransferRequest request) {
        // 参数校验（先检查 null）
        validateTransferRequest(request);
        
        log.info("开始商家转账：outRedPackNo={}, amount={}", 
            request.getOutRedPackNo(), request.getAmount());

        init();

        try {
            // 构建请求参数
            Map<String, Object> requestBody = buildTransferRequestBody(request);
            String jsonBody = JsonUtils.toJson(requestBody);

            log.debug("转账请求参数：{}", maskSensitiveInfo(jsonBody));

            // 调用微信转账 API
            TransferResult result = callWechatTransferApi(jsonBody);

            log.info("商家转账成功：outRedPackNo={}, transferId={}", 
                request.getOutRedPackNo(), result.getTransferId());

            return result;

        } catch (WechatException e) {
            log.error("商家转账失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("商家转账异常", e);
            throw new WechatException(WechatExceptionCodes.PAY_TRANSFER_FAILED, 
                "商家转账异常：" + e.getMessage(), e);
        }
    }

    @Override
    public TransferResult query(String outRedPackNo) {
        log.info("查询转账结果：outRedPackNo={}", outRedPackNo);

        init();

        if (outRedPackNo == null || outRedPackNo.trim().isEmpty()) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "商户订单号不能为空");
        }

        try {
            // 调用微信查询转账 API
            String apiUrl = properties.getPay().getApiUrl() + 
                "/v3/transfer/bills/" + outRedPackNo;

            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String nonceStr = generateNonceStr();
            String signData = "\n" + timestamp + "\n" + nonceStr + "\n";
            String signature = generateSignatureV3(signData);

            Request httpRequest = new Request.Builder()
                    .url(apiUrl)
                    .get()
                    .addHeader("Authorization", buildAuthorizationHeader(timestamp, nonceStr, signature))
                    .build();

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    log.error("查询转账失败：status={}", response.code());
                    throw new WechatException(WechatExceptionCodes.PAY_API_ERROR, 
                        "查询转账失败：" + response.code());
                }

                String responseBody = response.body().string();
                log.debug("查询转账响应：{}", responseBody);

                return parseTransferResponse(responseBody);
            }

        } catch (WechatException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询转账异常", e);
            throw new WechatException(WechatExceptionCodes.PAY_API_ERROR, 
                "查询转账异常：" + e.getMessage(), e);
        }
    }

    @Override
    public RedPacketResult sendRedPacket(RedPacketRequest request) {
        // 参数校验（先检查 null）
        validateRedPacketRequest(request);
        
        log.info("开始发送红包：outDetailNo={}, total_amount={}", 
            request.getOutDetailNo(), request.getTotal_amount());

        init();

        try {
            // 构建请求参数
            Map<String, Object> requestBody = buildRedPacketRequestBody(request);
            String jsonBody = JsonUtils.toJson(requestBody);

            log.debug("红包请求参数：{}", maskSensitiveInfo(jsonBody));

            // 调用微信红包 API
            RedPacketResult result = callWechatRedPacketApi(jsonBody);

            log.info("发送红包成功：outDetailNo={}, detail_id={}", 
                request.getOutDetailNo(), result.getDetail_id());

            return result;

        } catch (WechatException e) {
            log.error("发送红包失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("发送红包异常", e);
            throw new WechatException(WechatExceptionCodes.PAY_RED_PACKET_FAILED, 
                "发送红包异常：" + e.getMessage(), e);
        }
    }

    @Override
    public RedPacketResult queryRedPacket(String outDetailNo) {
        log.info("查询红包结果：outDetailNo={}", outDetailNo);

        init();

        if (outDetailNo == null || outDetailNo.trim().isEmpty()) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "订单号不能为空");
        }

        try {
            // 调用微信查询红包 API
            String apiUrl = properties.getPay().getApiUrl() + 
                "/v3/marketing/packets/" + outDetailNo;

            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String nonceStr = generateNonceStr();
            String signData = "\n" + timestamp + "\n" + nonceStr + "\n";
            String signature = generateSignatureV3(signData);

            Request httpRequest = new Request.Builder()
                    .url(apiUrl)
                    .get()
                    .addHeader("Authorization", buildAuthorizationHeader(timestamp, nonceStr, signature))
                    .build();

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    log.error("查询红包失败：status={}", response.code());
                    throw new WechatException(WechatExceptionCodes.PAY_API_ERROR, 
                        "查询红包失败：" + response.code());
                }

                String responseBody = response.body().string();
                log.debug("查询红包响应：{}", responseBody);

                return parseRedPacketResponse(responseBody);
            }

        } catch (WechatException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询红包异常", e);
            throw new WechatException(WechatExceptionCodes.PAY_API_ERROR, 
                "查询红包异常：" + e.getMessage(), e);
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 验证转账请求
     */
    private void validateTransferRequest(TransferRequest request) {
        if (request == null) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "转账请求不能为空");
        }

        if (request.getUserOpenid() == null || request.getUserOpenid().trim().isEmpty()) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "收款用户 openid 不能为空");
        }

        if (request.getOutRedPackNo() == null || request.getOutRedPackNo().trim().isEmpty()) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "商户订单号不能为空");
        }

        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "转账金额必须大于 0");
        }

        if (request.getAmount() > 1000000) { // 单笔不超过 1 万元
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "单笔转账金额不能超过 1 万元");
        }
    }

    /**
     * 验证红包请求
     */
    private void validateRedPacketRequest(RedPacketRequest request) {
        if (request == null) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "红包请求不能为空");
        }

        if (request.getReceiverOpenid() == null || request.getReceiverOpenid().trim().isEmpty()) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "收款用户 openid 不能为空");
        }

        if (request.getOutDetailNo() == null || request.getOutDetailNo().trim().isEmpty()) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "订单号不能为空");
        }

        if (request.getTotal_amount() == null || request.getTotal_amount() <= 0) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "红包金额必须大于 0");
        }

        if (request.getTotal_amount() < 100) { // 红包最低 1 元
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "红包金额不能低于 1 元");
        }

        if (request.getTotal_amount() > 200000) { // 红包最高 2000 元
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "红包金额不能超过 2000 元");
        }
    }

    /**
     * 构建转账请求体
     */
    private Map<String, Object> buildTransferRequestBody(TransferRequest request) {
        Map<String, Object> body = new HashMap<>();
        
        body.put("transfer_detail_no", request.getOutRedPackNo());
        body.put("amount", request.getAmount());
        body.put("user_name", request.getName());
        body.put("out_payer_mch_id", request.getPayeeMchId());
        body.put("payer_username", "Payer_Username"); // TODO: 从配置获取
        body.put("scene_id", "Scene_Id"); // TODO: 从请求获取
        body.put("description", request.getRemark());
        
        Map<String, Object> transfer_detail = new HashMap<>();
        transfer_detail.put("transfer_scene", "Transfer_Scene");
        transfer_detail.put("transfer_remark", request.getRemark());
        
        Map<String, Object> payee_info = new HashMap<>();
        payee_info.put("account_type", "Openid");
        payee_info.put("account_id", request.getUserOpenid());
        transfer_detail.put("payee_info", payee_info);
        
        body.put("transfer_detail", transfer_detail);

        return body;
    }

    /**
     * 构建红包请求体
     */
    private Map<String, Object> buildRedPacketRequestBody(RedPacketRequest request) {
        Map<String, Object> body = new HashMap<>();
        
        body.put("out_detail_no", request.getOutDetailNo());
        body.put("send_name", "发送者名称");
        body.put("total_num", 1);
        body.put("total_amount", request.getTotal_amount());
        body.put("remark", request.getRemark());
        body.put("lucky_type", request.getLucky_type());
        body.put("send_addr", "发送地址");
        
        if (request.getActivity_id() != null) {
            body.put("activity_id", request.getActivity_id());
        }
        
        if (request.getCover_imgid() != null) {
            body.put("cover_imgid", request.getCover_imgid());
        }
        
        Map<String, Object> receiver = new HashMap<>();
        receiver.put("receiver_id", request.getReceiverOpenid());
        receiver.put("account_type", "Openid");
        body.put("receiver", receiver);

        return body;
    }

    /**
     * 调用微信转账 API
     */
    private TransferResult callWechatTransferApi(String jsonBody) {
        String apiUrl = properties.getPay().getApiUrl() + "/v3/transfer/bills";

        try {
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String nonceStr = generateNonceStr();
            String signData = jsonBody + "\n" + timestamp + "\n" + nonceStr + "\n";
            String signature = generateSignatureV3(signData);

            Request httpRequest = new Request.Builder()
                    .url(apiUrl)
                    .post(RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8")))
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Authorization", buildAuthorizationHeader(timestamp, nonceStr, signature))
                    .addHeader("Wechatpay-Mchid", properties.getPay().getMchId())
                    .build();

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    log.error("转账 API 失败：status={}", response.code());
                    throw new WechatException(WechatExceptionCodes.PAY_TRANSFER_FAILED, 
                        "转账失败：" + response.code());
                }

                String responseBody = response.body().string();
                log.debug("转账响应：{}", responseBody);

                return parseTransferResponse(responseBody);
            }

        } catch (WechatException e) {
            throw e;
        } catch (Exception e) {
            log.error("转账 API 调用异常", e);
            throw new WechatException(WechatExceptionCodes.PAY_TRANSFER_FAILED, 
                "转账 API 调用异常：" + e.getMessage(), e);
        }
    }

    /**
     * 调用微信红包 API
     */
    private RedPacketResult callWechatRedPacketApi(String jsonBody) {
        String apiUrl = properties.getPay().getApiUrl() + "/v3/marketing/packets";

        try {
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String nonceStr = generateNonceStr();
            String signData = jsonBody + "\n" + timestamp + "\n" + nonceStr + "\n";
            String signature = generateSignatureV3(signData);

            Request httpRequest = new Request.Builder()
                    .url(apiUrl)
                    .post(RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8")))
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Authorization", buildAuthorizationHeader(timestamp, nonceStr, signature))
                    .addHeader("Wechatpay-Mchid", properties.getPay().getMchId())
                    .build();

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    log.error("红包 API 失败：status={}", response.code());
                    throw new WechatException(WechatExceptionCodes.PAY_RED_PACKET_FAILED, 
                        "红包发放失败：" + response.code());
                }

                String responseBody = response.body().string();
                log.debug("红包响应：{}", responseBody);

                return parseRedPacketResponse(responseBody);
            }

        } catch (WechatException e) {
            throw e;
        } catch (Exception e) {
            log.error("红包 API 调用异常", e);
            throw new WechatException(WechatExceptionCodes.PAY_RED_PACKET_FAILED, 
                "红包 API 调用异常：" + e.getMessage(), e);
        }
    }

    /**
     * 解析转账响应
     */
    private TransferResult parseTransferResponse(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);

            TransferResult result = new TransferResult();
            result.setSuccess(true);
            result.setTransferId(root.path("transfer_id").asText());
            result.setOutRedPackNo(root.path("transfer_detail_no").asText());
            result.setUserOpenid(root.path("payee_user_name").asText());
            result.setAmount(root.path("amount").asInt());
            result.setTransferTime(root.path("transfer_time").asText());
            result.setStatus("SUCCESS");

            return result;

        } catch (Exception e) {
            log.error("解析转账响应失败", e);
            throw new WechatException(WechatExceptionCodes.PAY_PARSE_ERROR, 
                "解析转账响应失败：" + e.getMessage(), e);
        }
    }

    /**
     * 解析红包响应
     */
    private RedPacketResult parseRedPacketResponse(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);

            RedPacketResult result = new RedPacketResult();
            result.setSuccess(true);
            result.setReturn_code(root.path("return_code").asText());
            result.setReturn_msg(root.path("return_msg").asText());
            result.setOut_detail_no(root.path("out_detail_no").asText());
            result.setDetail_id(root.path("detail_id").asText());
            result.setTotal_amount(root.path("total_amount").asInt());
            result.setTotal_num(root.path("total_num").asInt());
            result.setSend_time(root.path("send_time").asText());

            return result;

        } catch (Exception e) {
            log.error("解析红包响应失败", e);
            throw new WechatException(WechatExceptionCodes.PAY_PARSE_ERROR, 
                "解析红包响应失败：" + e.getMessage(), e);
        }
    }

    /**
     * 生成随机串
     */
    private String generateNonceStr() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成签名
     */
    private String generateSignatureV3(String signData) {
        if (privateKey == null) {
            log.warn("私钥未配置，使用模拟签名");
            return "mock_signature_" + System.currentTimeMillis();
        }
        return SignUtils.signV3(signData, privateKey);
    }

    /**
     * 构建授权头
     */
    private String buildAuthorizationHeader(String timestamp, String nonceStr, String signature) {
        String mchId = properties.getPay().getMchId();
        String serialNo = properties.getPay().getSerialNo();

        return "WECHATPAY2-SHA256-RSA2048 mchid=" + mchId + 
               ",nonce_str=" + nonceStr + 
               ",timestamp=" + timestamp + 
               ",serial_number=" + serialNo + 
               ",signature=" + signature;
    }

    /**
     * 脱敏敏感信息
     */
    private String maskSensitiveInfo(String json) {
        if (json == null) {
            return "";
        }
        return json.replaceFirst("\"user_openid\":\"[^\"]+\"", "\"user_openid\":\"***\"")
                   .replaceFirst("\"out_red_pack_no\":\"[^\"]+\"", "\"out_red_pack_no\":\"***\"");
    }
}
