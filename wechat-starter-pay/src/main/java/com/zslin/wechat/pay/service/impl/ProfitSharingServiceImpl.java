package com.zslin.wechat.pay.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zslin.wechat.core.config.WechatProperties;
import com.zslin.wechat.core.exception.WechatException;
import com.zslin.wechat.core.exception.WechatExceptionCodes;
import com.zslin.wechat.core.util.JsonUtils;
import com.zslin.wechat.core.util.SignUtils;
import com.zslin.wechat.pay.dto.request.ProfitSharingRequest;
import com.zslin.wechat.pay.dto.request.ProfitSharingReturnRequest;
import com.zslin.wechat.pay.dto.response.ProfitSharingResult;
import com.zslin.wechat.pay.dto.response.ProfitSharingReturnResult;
import com.zslin.wechat.pay.service.ProfitSharingService;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 分账服务实现
 *
 * @author 子墨
 * @since 1.0.0
 */
@Service
public class ProfitSharingServiceImpl implements ProfitSharingService {

    private static final Logger log = LoggerFactory.getLogger(ProfitSharingServiceImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WechatProperties properties;

    private PrivateKey privateKey;
    private PublicKey wechatPublicKey;

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    /**
     * 分账请求单号存储（用于幂等性检查）
     */
    private final Set<String> sharingRequestNos = new HashSet<>();

    /**
     * 初始化（延迟加载）
     */
    private void init() {
        if (initialized.compareAndSet(false, true)) {
            try {
                String privateKeyPem = properties.getPay().getPrivateKeyPem();
                if (privateKeyPem != null && !privateKeyPem.isEmpty()) {
                    this.privateKey = SignUtils.loadPrivateKeyFromPem(privateKeyPem);
                    log.info("分账服务私钥加载成功");
                } else {
                    log.warn("分账服务私钥未配置，使用模拟模式");
                }

                String wechatPublicKeyPem = properties.getPay().getWechatPublicKeyPem();
                if (wechatPublicKeyPem != null && !wechatPublicKeyPem.isEmpty()) {
                    this.wechatPublicKey = SignUtils.loadPublicKeyFromPem(wechatPublicKeyPem);
                    log.info("分账服务微信公钥加载成功");
                } else {
                    log.warn("分账服务微信公钥未配置");
                }
            } catch (Exception e) {
                log.error("分账服务初始化失败", e);
            }
        }
    }

    @Override
    public ProfitSharingResult share(ProfitSharingRequest request) {
        // 参数校验（先检查 null）
        validateShareRequest(request);
        
        log.info("开始分账：outOrderNo={}", request.getOutOrderNo());

        init();

        // 幂等性检查
        checkSharingIdempotency(request.getOutOrderNo());

        try {
            // 构建请求参数
            Map<String, Object> requestBody = buildShareRequestBody(request);
            String jsonBody = JsonUtils.toJson(requestBody);

            log.debug("分账请求参数：{}", maskSensitiveInfo(jsonBody));

            // 调用微信分账 API
            ProfitSharingResult result = callWechatShareApi(jsonBody);

            // 记录分账请求单号
            if (result.isSuccess()) {
                recordSharingRequestNo(request.getOutOrderNo());
            }

            log.info("分账成功：outOrderNo={}, transactionId={}", 
                request.getOutOrderNo(), result.getTransactionId());

            return result;

        } catch (WechatException e) {
            log.error("分账失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("分账异常", e);
            throw new WechatException(WechatExceptionCodes.PAY_API_ERROR, 
                "分账异常：" + e.getMessage(), e);
        }
    }

    @Override
    public ProfitSharingResult query(String outOrderNo) {
        log.info("查询分账结果：outOrderNo={}", outOrderNo);

        init();

        if (outOrderNo == null || outOrderNo.trim().isEmpty()) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "订单号不能为空");
        }

        try {
            // 调用微信查询分账 API
            String apiUrl = properties.getPay().getApiUrl() + 
                "/v3/profitsharing/orders/" + outOrderNo;

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
                    log.error("查询分账失败：status={}", response.code());
                    throw new WechatException(WechatExceptionCodes.PAY_API_ERROR, 
                        "查询分账失败：" + response.code());
                }

                String responseBody = response.body().string();
                log.debug("查询分账响应：{}", responseBody);

                return parseShareQueryResponse(responseBody);
            }

        } catch (WechatException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询分账异常", e);
            throw new WechatException(WechatExceptionCodes.PAY_API_ERROR, 
                "查询分账异常：" + e.getMessage(), e);
        }
    }

    @Override
    public ProfitSharingReturnResult returnFund(ProfitSharingReturnRequest request) {
        // 参数校验（先检查 null）
        validateReturnRequest(request);
        
        log.info("申请分账回款：outRequestNo={}", request.getOutRequestNo());

        init();

        try {
            // 构建请求参数
            Map<String, Object> requestBody = buildReturnRequestBody(request);
            String jsonBody = JsonUtils.toJson(requestBody);

            log.debug("分账回款请求参数：{}", jsonBody);

            // 调用微信分账回款 API
            ProfitSharingReturnResult result = callWechatReturnApi(jsonBody);

            log.info("分账回款申请成功：outRequestNo={}", request.getOutRequestNo());

            return result;

        } catch (WechatException e) {
            log.error("分账回款失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("分账回款异常", e);
            throw new WechatException(WechatExceptionCodes.PAY_API_ERROR, 
                "分账回款异常：" + e.getMessage(), e);
        }
    }

    @Override
    public ProfitSharingReturnResult queryReturn(String outRequestNo) {
        // 参数校验（先检查 null）
        if (outRequestNo == null || outRequestNo.trim().isEmpty()) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "分账请求单号不能为空");
        }
        
        log.info("查询分账回款结果：outRequestNo={}", outRequestNo);

        init();

        try {
            // TODO: 实现查询分账回款 API
            // 这里返回模拟结果
            ProfitSharingReturnResult result = new ProfitSharingReturnResult();
            result.setSuccess(true);
            result.setOutRequestNo(outRequestNo);
            result.setStatus("SUCCESS");
            
            return result;

        } catch (Exception e) {
            log.error("查询分账回款异常", e);
            throw new WechatException(WechatExceptionCodes.PAY_API_ERROR, 
                "查询分账回款异常：" + e.getMessage(), e);
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 验证分账请求
     */
    private void validateShareRequest(ProfitSharingRequest request) {
        if (request == null) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "分账请求不能为空");
        }

        if (request.getOutOrderNo() == null || request.getOutOrderNo().trim().isEmpty()) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "订单号不能为空");
        }

        if (request.getReceptorList() == null || request.getReceptorList().isEmpty()) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "收款方列表不能为空");
        }

        // 检查分账金额总和不超过订单金额（这里简化处理）
        int totalAmount = 0;
        for (ProfitSharingRequest.Receptor receptor : request.getReceptorList()) {
            if (receptor.getAmount() == null || receptor.getAmount() <= 0) {
                throw new WechatException(WechatExceptionCodes.PARAM_ERROR, 
                    "分账金额必须大于 0");
            }
            totalAmount += receptor.getAmount();
        }

        log.debug("分账总金额：{} 分", totalAmount);
    }

    /**
     * 验证分账回款请求
     */
    private void validateReturnRequest(ProfitSharingReturnRequest request) {
        if (request == null) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "回款请求不能为空");
        }

        if (request.getOutRequestNo() == null || request.getOutRequestNo().trim().isEmpty()) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "分账请求单号不能为空");
        }

        if (request.getReturnAmount() == null || request.getReturnAmount() <= 0) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "回款金额必须大于 0");
        }

        if (request.getReturnReceptorId() == null || request.getReturnReceptorId().trim().isEmpty()) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "回款接收方不能为空");
        }
    }

    /**
     * 检查分账幂等性
     */
    private void checkSharingIdempotency(String outOrderNo) {
        if (sharingRequestNos.contains(outOrderNo)) {
            log.warn("分账幂等性检查：订单已分账 outOrderNo={}", outOrderNo);
            // 这里可以选择抛出异常或者静默处理
            // throw new WechatException(WechatExceptionCodes.PARAM_ERROR, "订单已分账");
        }
    }

    /**
     * 记录分账请求单号
     */
    private void recordSharingRequestNo(String outOrderNo) {
        sharingRequestNos.add(outOrderNo);
    }

    /**
     * 构建分账请求体
     */
    private Map<String, Object> buildShareRequestBody(ProfitSharingRequest request) {
        Map<String, Object> body = new HashMap<>();
        
        body.put("out_order_no", request.getOutOrderNo());
        body.put("profit_sharing_type", request.getProfitSharingType());
        body.put("description", request.getDescription());
        body.put("remark", request.getRemark());

        List<Map<String, Object>> receptorList = new ArrayList<>();
        for (ProfitSharingRequest.Receptor receptor : request.getReceptorList()) {
            Map<String, Object> receptorMap = new HashMap<>();
            receptorMap.put("receptor_id", receptor.getReceptorId());
            receptorMap.put("receptor_type", receptor.getReceptorType());
            receptorMap.put("amount", receptor.getAmount());
            receptorMap.put("description", receptor.getDescription());
            receptorList.add(receptorMap);
        }
        body.put("receptor_list", receptorList);

        return body;
    }

    /**
     * 构建分账回款请求体
     */
    private Map<String, Object> buildReturnRequestBody(ProfitSharingReturnRequest request) {
        Map<String, Object> body = new HashMap<>();
        
        body.put("out_request_no", request.getOutRequestNo());
        body.put("return_amount", request.getReturnAmount());
        body.put("return_receptor_id", request.getReturnReceptorId());
        body.put("return_receptor_type", request.getReturnReceptorType());
        body.put("reason", request.getReason());
        body.put("remark", request.getRemark());

        return body;
    }

    /**
     * 调用微信分账 API
     */
    private ProfitSharingResult callWechatShareApi(String jsonBody) {
        String apiUrl = properties.getPay().getApiUrl() + "/v3/profitsharing/orders";

        try {
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String nonceStr = generateNonceStr();
            
            // 微信支付 v3 签名：先对请求体进行 AES 加密，然后对加密后的数据进行签名
            // 这里简化处理，直接使用请求体
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
                    log.error("分账 API 失败：status={}", response.code());
                    throw new WechatException(WechatExceptionCodes.PAY_API_ERROR, 
                        "分账失败：" + response.code());
                }

                String responseBody = response.body().string();
                log.debug("分账响应：{}", responseBody);

                return parseShareResponse(responseBody);
            }

        } catch (WechatException e) {
            throw e;
        } catch (Exception e) {
            log.error("分账 API 调用异常", e);
            throw new WechatException(WechatExceptionCodes.PAY_API_ERROR, 
                "分账 API 调用异常：" + e.getMessage(), e);
        }
    }

    /**
     * 调用微信分账回款 API
     */
    private ProfitSharingReturnResult callWechatReturnApi(String jsonBody) {
        String apiUrl = properties.getPay().getApiUrl() + "/v3/profitsharing/returns";

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
                    log.error("分账回款 API 失败：status={}", response.code());
                    throw new WechatException(WechatExceptionCodes.PAY_API_ERROR, 
                        "分账回款失败：" + response.code());
                }

                String responseBody = response.body().string();
                log.debug("分账回款响应：{}", responseBody);

                // TODO: 解析响应
                ProfitSharingReturnResult result = new ProfitSharingReturnResult();
                result.setSuccess(true);
                
                return result;
            }

        } catch (WechatException e) {
            throw e;
        } catch (Exception e) {
            log.error("分账回款 API 调用异常", e);
            throw new WechatException(WechatExceptionCodes.PAY_API_ERROR, 
                "分账回款 API 调用异常：" + e.getMessage(), e);
        }
    }

    /**
     * 解析分账响应
     */
    private ProfitSharingResult parseShareResponse(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);

            ProfitSharingResult result = new ProfitSharingResult();
            result.setSuccess(true);
            result.setTransactionId(root.path("transaction_id").asText());
            result.setOutOrderNo(root.path("out_order_no").asText());

            // 解析收款方详情
            JsonNode receptorList = root.path("receptor_detail_list");
            if (receptorList.isArray()) {
                List<ProfitSharingResult.ReceptorDetail> details = new ArrayList<>();
                for (JsonNode receptor : receptorList) {
                    ProfitSharingResult.ReceptorDetail detail = new ProfitSharingResult.ReceptorDetail();
                    detail.setReceptorId(receptor.path("receptor_id").asText());
                    detail.setReceptorType(receptor.path("receptor_type").asText());
                    detail.setAmount(receptor.path("amount").asInt());
                    detail.setDescription(receptor.path("description").asText());
                    details.add(detail);
                }
                result.setReceptorDetailList(details);
            }

            return result;

        } catch (Exception e) {
            log.error("解析分账响应失败", e);
            throw new WechatException(WechatExceptionCodes.PAY_PARSE_ERROR, 
                "解析分账响应失败：" + e.getMessage(), e);
        }
    }

    /**
     * 解析分账查询响应
     */
    private ProfitSharingResult parseShareQueryResponse(String json) {
        return parseShareResponse(json);
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
        return json.replaceFirst("\"out_order_no\":\"[^\"]+\"", "\"out_order_no\":\"***\"");
    }
}
