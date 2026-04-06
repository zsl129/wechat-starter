# WeChat Starter 详细开发文档

## 📖 目录

1. [项目概述](#1-项目概述)
2. [架构设计](#2-架构设计)
3. [核心模块详解](#3-核心模块详解)
4. [支付模块详解](#4-支付模块详解)
5. [认证模块详解](#5-认证模块详解)
6. [消息模块详解](#6-消息模块详解)
7. [IoT 模块详解](#7-iot-模块详解)
8. [最佳实践](#8-最佳实践)
9. [常见问题](#9-常见问题)
10. [性能优化](#10-性能优化)

---

## 1. 项目概述

### 1.1 什么是 WeChat Starter？

WeChat Starter 是一个完整的微信开发 Starter 框架，旨在简化微信支付、认证、消息推送等功能的集成过程。

**核心目标**：
- 一行代码完成支付对接
- 统一的异常处理
- 完善的文档和示例
- 开箱即用的体验

### 1.2 功能特性

| 模块 | 功能 | 状态 |
|------|------|------|
| 核心模块 | 配置管理、安全工具、数据工具、网络工具 | ✅ 完成 |
| 支付模块 | JSAPI/Native/APP 支付、退款、分账、转账、红包 | ✅ 完成 |
| 认证模块 | 小程序登录、公众号授权、手机号解密 | ✅ 完成 |
| 消息模块 | 模板消息、订阅消息 | ✅ 完成 |
| IoT 模块 | 设备管理、传感器数据 | ✅ 完成 |

### 1.3 技术栈

```
- Java: 17
- Spring Boot: 3.3.6
- Maven: 3.8.1+
- HTTP 客户端：OkHttp 4.12
- 加密库：BouncyCastle
- 测试框架：JUnit 5 + Mockito
```

---

## 2. 架构设计

### 2.1 项目结构

```
wechat-starter/
├── wechat-starter-core/          # 核心模块
│   ├── config/                   # 配置管理
│   ├── util/                     # 工具类
│   ├── exception/                # 异常处理
│   └── service/                  # 服务层
│
├── wechat-starter-pay/           # 支付模块
│   ├── controller/               # 控制层
│   ├── service/                  # 服务层
│   ├── dto/                      # 数据传输对象
│   ├── callback/                 # 回调处理
│   └── docs/                     # 文档
│
├── wechat-starter-auth/          # 认证模块
├── wechat-starter-message/       # 消息模块
├── wechat-starter-iot/           # IoT 模块
└── pom.xml                       # 父 POM
```

### 2.2 设计模式

#### 2.2.1 工厂模式
```java
// 支付服务工厂
public class PayServiceFactory {
    public static PayService createPayService(PayType type) {
        switch (type) {
            case JSAPI:
                return new JsapiPayService();
            case NATIVE:
                return new NativePayService();
            case APP:
                return new AppPayService();
            default:
                throw new IllegalArgumentException("Unknown pay type");
        }
    }
}
```

#### 2.2.2 策略模式
```java
// 签名策略
public interface SignStrategy {
    String sign(String data);
}

// RSA 签名实现
public class RsaSignStrategy implements SignStrategy {
    @Override
    public String sign(String data) {
        // RSA 签名实现
    }
}

// HMAC 签名实现
public class HmacSignStrategy implements SignStrategy {
    @Override
    public String sign(String data) {
        // HMAC 签名实现
    }
}
```

#### 2.2.3 单例模式
```java
// HTTP 客户端单例
@Service
public class WechatHttpClient {
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build();
    
    private WechatHttpClient() {}
    
    public static OkHttpClient getInstance() {
        return CLIENT;
    }
}
```

---

## 3. 核心模块详解

### 3.1 配置管理

#### 3.1.1 WechatProperties

```java
@Data
public class WechatProperties {
    /**
     * 小程序配置
     */
    private MiniApp miniApp;
    
    /**
     * 支付配置
     */
    private Pay pay;
    
    /**
     * Redis 配置
     */
    private Redis redis;
    
    /**
     * 公众号配置
     */
    private Mp mp;
    
    // 内部类定义
    @Data
    public static class Pay {
        private String mchId;          // 商户号
        private String apiKey;         // API 密钥 v2
        private String apiV3Key;       // API 密钥 v3
        private String certPath;       // 证书路径
        private String certPassword;   // 证书密码
        private String notifyUrl;      // 回调地址
        private boolean sandbox;       // 沙箱环境
        private String apiUrl;         // API 地址
        private String serialNo;       // 证书序列号
        private String privateKeyPem;  // 私钥
        private String wechatPublicKeyPem; // 微信支付公钥
    }
}
```

#### 3.1.2 配置文件示例

```yaml
wechat:
  pay:
    mch-id: 1234567890
    api-v3-key: your-api-v3-key
    serial-no: your-serial-no
    private-key-pem: |
      -----BEGIN PRIVATE KEY-----
      MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC7...
      -----END PRIVATE KEY-----
    notify-url: https://your-domain.com/notify
    sandbox: false
```

### 3.2 安全工具

#### 3.2.1 签名工具 (SignUtils)

**RSA 签名**
```java
public class SignUtils {
    /**
     * RSA 签名
     */
    public static String sign(String data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signature.sign());
    }
    
    /**
     * RSA 验签
     */
    public static boolean verify(String data, String signature, PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(data.getBytes(StandardCharsets.UTF_8));
        return sig.verify(Base64.getDecoder().decode(signature));
    }
}
```

**HMAC-SHA256 签名**
```java
/**
 * HMAC-SHA256 签名
 */
public static String hmacSha256(String data, String key) throws Exception {
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
    byte[] result = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    return bytesToHex(result);
}
```

#### 3.2.2 证书加载

```java
/**
 * 从 PEM 格式加载私钥
 */
public static PrivateKey loadPrivateKeyFromPem(String pem) throws Exception {
    String privateKeyPEM = pem
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .replaceAll("\\s", "");
    
    byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
    return keyFactory.generatePrivate(keySpec);
}

/**
 * 从 PEM 格式加载公钥
 */
public static PublicKey loadPublicKeyFromPem(String pem) throws Exception {
    String publicKeyPEM = pem
        .replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "")
        .replaceAll("\\s", "");
    
    byte[] keyBytes = Base64.getDecoder().decode(publicKeyPEM);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
    return keyFactory.generatePublic(keySpec);
}
```

### 3.3 数据加密

#### 3.3.1 AES 加密

```java
public class DataUtils {
    /**
     * AES 加密
     */
    public static String aesEncrypt(String plainText, String key) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();
        
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    /**
     * AES 解密
     */
    public static String aesDecrypt(String encryptedText, String key) throws Exception {
        byte[] encrypted = Base64.getDecoder().decode(encryptedText);
        
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"));
        
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
```

#### 3.3.2 敏感信息脱敏

```java
/**
 * 脱敏敏感信息
 */
public static String maskSensitiveInfo(String json) {
    if (json == null) {
        return "";
    }
    return json
        .replaceFirst("\"openid\":\"[^\"]+\"", "\"openid\":\"***\"")
        .replaceFirst("\"out_trade_no\":\"[^\"]+\"", "\"out_trade_no\":\"***\"")
        .replaceFirst("\"amount\":\"\\d+\"", "\"amount\":\"***\"");
}
```

---

## 4. 支付模块详解

### 4.1 JSAPI 支付

#### 4.1.1 完整流程

```
1. 创建订单 -> 2. 统一下单 -> 3. 生成预支付 ID
4. 生成调起参数 -> 5. 用户支付 -> 6. 微信回调
```

#### 4.1.2 代码实现

```java
@Service
public class PayServiceImpl implements PayService {
    
    @Autowired
    private WechatPayService wechatPayService;
    
    @Override
    public PayResult jsapiPay(PayRequest request) {
        log.info("开始 JSAPI 支付：orderId={}", request.getOrderId());
        
        try {
            // 1. 构建统一下单参数
            Map<String, String> params = new HashMap<>();
            params.put("appid", appId);
            params.put("mch_id", mchId);
            params.put("nonce_str", generateNonceStr());
            params.put("notify_url", notifyUrl);
            params.put("body", request.getBody());
            params.put("out_trade_no", request.getOutTradeNo());
            params.put("total_fee", String.valueOf(request.getAmount()));
            params.put("spbill_create_ip", request.getSpbillCreateIp());
            params.put("time_start", getTimeStart());
            params.put("time_expire", getTimeExpire());
            params.put("trade_type", "JSAPI");
            params.put("detail_request", "0");
            
            // 2. 添加优惠参数
            if (request.getCoupon() != null) {
                params.put("coupon_type", request.getCoupon().getType());
                params.put("coupon_id", request.getCoupon().getId());
            }
            
            // 3. 添加设备号
            if (request.getDeviceInfo() != null) {
                params.put("device_info", request.getDeviceInfo());
            }
            
            // 4. 添加标签
            if (request.getTagid() != null) {
                params.put("tagid", request.getTagid());
            }
            
            // 5. 生成签名
            params.put("sign", generateSign(params));
            
            // 6. 调用微信统一下单接口
            String response = wechatPayService.unifiedOrder(params);
            
            // 7. 解析响应
            PayResult result = parsePayResponse(response);
            
            // 8. 生成调起支付的参数
            if ("SUCCESS".equals(result.getResultCode())) {
                result.setPrepayId(result.getPrepayId());
                result.setPaySign(generatePaySign(result));
            }
            
            log.info("JSAPI 支付成功：orderId={}, prepayId={}", 
                request.getOrderId(), result.getPrepayId());
            
            return result;
            
        } catch (Exception e) {
            log.error("JSAPI 支付失败：orderId={}", request.getOrderId(), e);
            throw new WechatException(PAY_INIT_FAILED, "JSAPI 支付失败：" + e.getMessage(), e);
        }
    }
}
```

#### 4.1.3 注意事项

1. **订单号唯一性**
   - 商户订单号必须全局唯一
   - 建议使用 UUID 或雪花算法生成

2. **金额单位**
   - 微信接口使用**分**作为单位
   - 1 元 = 100 分

3. **回调地址**
   - 必须是 HTTPS
   - 必须可公网访问
   - 必须在微信商户平台配置

4. **签名参数**
   - 所有参数按字典序排序
   - 空值参数不参与签名
   - 签名结果大写

### 4.2 退款

#### 4.2.1 退款流程

```
1. 验证订单 -> 2. 构建退款请求 -> 3. 调用退款接口
4. 处理退款结果 -> 5. 记录退款日志 -> 6. 异步通知商户
```

#### 4.2.2 代码实现

```java
@Override
public RefundResult refund(String outTradeNo, int refundAmount, int totalAmount) {
    log.info("开始退款：outTradeNo={}, refundAmount={}, totalAmount={}", 
        outTradeNo, refundAmount, totalAmount);
    
    try {
        // 1. 验证订单
        PayQueryResult order = queryOrder(outTradeNo);
        if (order == null || !order.isPaid()) {
            throw new WechatException(PAY_ORDER_NOT_FOUND, "订单未找到或未支付");
        }
        
        // 2. 验证退款金额
        if (refundAmount <= 0) {
            throw new WechatException(PAY_REFUND_NOT_ALLOWED, "退款金额必须大于 0");
        }
        
        if (refundAmount > totalAmount) {
            throw new WechatException(PAY_REFUND_NOT_ALLOWED, "退款金额不能超过订单金额");
        }
        
        // 3. 构建退款请求
        Map<String, String> params = new HashMap<>();
        params.put("appid", appId);
        params.put("mch_id", mchId);
        params.put("nonce_str", generateNonceStr());
        params.put("out_trade_no", outTradeNo);
        params.put("out_refund_no", generateRefundNo());
        params.put("total_fee", String.valueOf(totalAmount));
        params.put("refund_fee", String.valueOf(refundAmount));
        params.put("refund_fee_type", "CNY");
        
        // 4. 生成签名
        params.put("sign", generateSign(params));
        
        // 5. 调用退款接口
        String response = wechatPayService.refund(params);
        
        // 6. 解析响应
        RefundResult result = parseRefundResponse(response);
        
        // 7. 记录退款日志
        recordRefundLog(outTradeNo, refundAmount, result);
        
        log.info("退款成功：outTradeNo={}, refundNo={}", outTradeNo, result.getOutRefundNo());
        
        return result;
        
    } catch (WechatException e) {
        throw e;
    } catch (Exception e) {
        log.error("退款失败：outTradeNo={}", outTradeNo, e);
        throw new WechatException(PAY_REFUND_FAILED, "退款失败：" + e.getMessage(), e);
    }
}
```

### 4.3 分账

#### 4.3.1 分账流程

```
1. 支付时标记分账 -> 2. 添加分账接收方 -> 3. 发起分账
4. 查询分账结果 -> 5. 分账回退（可选）
```

#### 4.3.2 代码实现

```java
@Override
public ProfitSharingResult share(ProfitSharingRequest request) {
    log.info("开始分账：outOrderNo={}", request.getOutOrderNo());
    
    try {
        // 1. 参数校验
        validateShareRequest(request);
        
        // 2. 幂等性检查
        checkSharingIdempotency(request.getOutOrderNo());
        
        // 3. 构建请求参数
        Map<String, Object> requestBody = buildShareRequestBody(request);
        String jsonBody = JsonUtils.toJson(requestBody);
        
        // 4. 签名
        String signature = generateSignature(jsonBody);
        
        // 5. 调用微信分账接口
        String response = callWechatShareApi(jsonBody, signature);
        
        // 6. 解析响应
        ProfitSharingResult result = parseShareResponse(response);
        
        // 7. 记录分账单号
        if (result.isSuccess()) {
            recordSharingRequestNo(request.getOutOrderNo());
        }
        
        log.info("分账成功：outOrderNo={}, transactionId={}", 
            request.getOutOrderNo(), result.getTransactionId());
        
        return result;
        
    } catch (Exception e) {
        log.error("分账失败：outOrderNo={}", request.getOutOrderNo(), e);
        throw new WechatException(PAY_SHARING_FAILED, "分账失败：" + e.getMessage(), e);
    }
}
```

---

## 5. 认证模块详解

### 5.1 小程序登录

#### 5.1.1 登录流程

```
1. 用户打开小程序 -> 2. 调用 wx.login() -> 3. 获取 code
4. 后端接收 code -> 5. 调用微信接口 -> 6. 获取 openid 和 session_key
7. 生成自定义 token -> 8. 返回给前端
```

#### 5.1.2 代码实现

```java
@Override
public AuthResult login(String code) {
    log.info("小程序登录：code={}", maskCode(code));
    
    try {
        // 1. 参数校验
        if (StringUtils.isBlank(code)) {
            throw new WechatException(CODE_EMPTY, "登录凭证不能为空");
        }
        
        // 2. 调用微信接口
        String apiUrl = String.format(
            "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
            miniApp.getAppId(),
            miniApp.getSecret(),
            code
        );
        
        String response = httpUtils.get(apiUrl);
        
        // 3. 解析响应
        JsonNode root = objectMapper.readTree(response);
        
        if (root.has("errcode")) {
            log.error("微信登录失败：errcode={}, errmsg={}", 
                root.get("errcode").asInt(), 
                root.get("errmsg").asText());
            throw new WechatException(WECHAT_LOGIN_FAILED, "微信登录失败：" + root.get("errmsg").asText());
        }
        
        // 4. 生成结果
        AuthResult result = new AuthResult();
        result.setOpenId(root.get("openid").asText());
        result.setSessionKey(root.get("session_key").asText());
        result.setSuccess(true);
        
        // 5. 生成自定义 token
        String token = generateToken(result.getOpenId());
        result.setToken(token);
        
        // 6. 缓存 token
        cacheToken(token, result.getOpenId());
        
        log.info("小程序登录成功：openid={}", maskOpenId(result.getOpenId()));
        
        return result;
        
    } catch (WechatException e) {
        throw e;
    } catch (Exception e) {
        log.error("小程序登录异常：code={}", maskCode(code), e);
        throw new WechatException(WECHAT_LOGIN_FAILED, "登录异常：" + e.getMessage(), e);
    }
}
```

---

## 6. 消息模块详解

### 6.1 模板消息

#### 6.1.1 发送模板消息

```java
@Override
public MessageSendResult sendTemplateMessage(MessageSendRequest request) {
    log.info("发送模板消息：templateId={}, toUser={}", 
        request.getTemplateId(), request.getToUser());
    
    try {
        // 1. 参数校验
        if (StringUtils.isBlank(request.getTemplateId())) {
            throw new WechatException(TEMPLATE_NOT_FOUND, "模板 ID 不能为空");
        }
        
        // 2. 构建请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("touser", request.getToUser());
        params.put("template_id", request.getTemplateId());
        params.put("url", request.getUrl());
        
        if (request.getData() != null) {
            Map<String, Object> data = new HashMap<>();
            request.getData().forEach((key, value) -> {
                Map<String, Object> item = new HashMap<>();
                item.put("value", value.getValue());
                if (value.getColor() != null) {
                    item.put("color", value.getColor());
                }
                data.put(key, item);
            });
            params.put("data", data);
        }
        
        // 3. 生成签名
        String token = getAccessToken();
        String apiUrl = String.format(
            "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s", 
            token
        );
        
        // 4. 发送请求
        String response = httpUtils.post(apiUrl, JsonUtils.toJson(params));
        
        // 5. 解析响应
        JsonNode root = objectMapper.readTree(response);
        
        if (root.has("errcode") && root.get("errcode").asInt() != 0) {
            throw new WechatException(SEND_FAILED, "发送失败：" + root.get("errmsg").asText());
        }
        
        MessageSendResult result = new MessageSendResult();
        result.setSuccess(true);
        result.setMsgId(root.get("msg_id").asText());
        
        log.info("模板消息发送成功：msgId={}", result.getMsgId());
        
        return result;
        
    } catch (WechatException e) {
        throw e;
    } catch (Exception e) {
        log.error("模板消息发送失败", e);
        throw new WechatException(SEND_FAILED, "发送失败：" + e.getMessage(), e);
    }
}
```

---

## 7. IoT 模块详解

### 7.1 设备管理

#### 7.1.1 设备注册

```java
@Override
public Device registerDevice(DeviceRegisterRequest request) {
    log.info("注册设备：deviceName={}, deviceType={}", 
        request.getDeviceName(), request.getDeviceType());
    
    try {
        // 1. 参数校验
        if (StringUtils.isBlank(request.getDeviceName())) {
            throw new WechatException(DEVICE_REGISTER_FAILED, "设备名称不能为空");
        }
        
        // 2. 生成设备 ID
        String deviceId = generateDeviceId();
        
        // 3. 创建设备对象
        Device device = new Device();
        device.setDeviceId(deviceId);
        device.setDeviceName(request.getDeviceName());
        device.setDeviceType(request.getDeviceType());
        device.setStatus("ONLINE");
        device.setCreateTime(System.currentTimeMillis());
        
        // 4. 保存设备
        deviceRepository.save(device);
        
        log.info("设备注册成功：deviceId={}, deviceName={}", deviceId, request.getDeviceName());
        
        return device;
        
    } catch (Exception e) {
        log.error("设备注册失败", e);
        throw new WechatException(DEVICE_REGISTER_FAILED, "设备注册失败：" + e.getMessage(), e);
    }
}
```

---

## 8. 最佳实践

### 8.1 错误处理

```java
// 统一异常处理
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(WechatException.class)
    public Result handleWechatException(WechatException e) {
        log.error("微信业务异常：code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(UNKNOWN_ERROR, "系统异常");
    }
}
```

### 8.2 日志记录

```java
// 结构化日志
log.info("支付成功：orderId={}, amount={}, userId={}", 
    orderId, amount, userId);

// 敏感信息脱敏
log.info("用户登录：openid={}, phone={}", 
    maskOpenId(openId), maskPhone(phone));
```

### 8.3 性能优化

```java
// 1. HTTP 连接池
private final OkHttpClient httpClient = new OkHttpClient.Builder()
    .connectionPool(new ConnectionPool(10, 5, TimeUnit.MINUTES))
    .build();

// 2. 访问令牌缓存
@Cacheable(value = "access_token", key = "#appId")
public String getAccessToken(String appId) {
    // 获取 token 逻辑
}

// 3. 签名缓存
private final Map<String, String> signCache = new ConcurrentHashMap<>();

public String sign(String data) {
    return signCache.computeIfAbsent(data, this::generateSign);
}
```

---

## 9. 常见问题

### Q1: 签名失败怎么办？
**A**: 检查以下几点：
1. 参数是否按字典序排序
2. 空值参数是否排除
3. 密钥是否正确
4. 签名算法是否一致

### Q2: 回调收不到怎么办？
**A**: 检查以下几点：
1. 回调地址是否 HTTPS
2. 回调地址是否可公网访问
3. 回调地址是否在商户平台配置
4. 服务器防火墙是否放行

### Q3: 退款失败怎么办？
**A**: 检查以下几点：
1. 订单是否已支付
2. 退款金额是否正确
3. 订单是否在退款有效期内
4. 商户号是否有退款权限

---

## 10. 性能优化

### 10.1 数据库优化
- 使用索引加速查询
- 合理使用缓存
- 定期清理历史数据

### 10.2 网络优化
- 使用连接池
- 合理设置超时时间
- 添加重试机制

### 10.3 代码优化
- 减少不必要的对象创建
- 使用批量操作
- 避免 N+1 查询问题

---

*文档版本：v1.0*
*最后更新：2026-04-06*
*作者：子墨 & 钟述林*
