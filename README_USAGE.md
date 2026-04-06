# Wechat Starter 使用指南

> **版本**: v1.0.0-SNAPSHOT  
> **作者**: 钟述林 & 子墨  
> **最后更新**: 2026-04-01  
> **项目地址**: `D:\work\projects\wechat-starter`

---

## 📖 目录

1. [快速开始](#1-快速开始)
2. [模块说明](#2-模块说明)
3. [配置说明](#3-配置说明)
4. [使用示例](#4-使用示例)
5. [常见问题](#5-常见问题)
6. [API 文档](#6-api-文档)

---

## 1. 快速开始

### 1.1 环境要求

- **JDK**: 1.8+
- **Maven**: 3.6+
- **Spring Boot**: 2.7.18 或 3.2.x

### 1.2 克隆项目

```bash
cd D:\work\projects\wechat-starter
```

### 1.3 编译安装

```bash
# 方式一：使用 IDEA
# 直接打开项目，IDEA 会自动识别 Maven 项目

# 方式二：命令行
mvn clean install
```

### 1.4 引入依赖

在你的项目中引入需要的模块：

```xml
<!-- 核心模块（必须） -->
<dependency>
    <groupId>com.zslin</groupId>
    <artifactId>wechat-starter-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- 登录授权模块 -->
<dependency>
    <groupId>com.zslin</groupId>
    <artifactId>wechat-starter-auth</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- 支付模块 -->
<dependency>
    <groupId>com.zslin</groupId>
    <artifactId>wechat-starter-pay</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- 消息推送模块 -->
<dependency>
    <groupId>com.zslin</groupId>
    <artifactId>wechat-starter-message</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- IoT 设备管理模块 -->
<dependency>
    <groupId>com.zslin</groupId>
    <artifactId>wechat-starter-iot</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 1.5 配置文件

在 `application.yml` 中添加配置：

```yaml
server:
  port: 8080

spring:
  application:
    name: my-wechat-app

# 微信小程序配置
wechat:
  miniapp:
    appId: wx1234567890abcdef
    appSecret: 1234567890abcdef1234567890abcdef
    token: your-token
    aesKey: your-aes-key
  
  # 支付配置（可选）
  pay:
    mchId: 1234567890
    apiKey: your-api-key
    apiV3Key: your-api-v3-key
    notifyUrl: https://yourdomain.com/api/pay/notify
  
  # Redis 配置（可选，用于缓存）
  redis:
    enabled: false
    host: localhost
    port: 6379
```

### 1.6 立即使用

**登录授权：**

```java
@RestController
@RequestMapping("/api")
public class MyController {
    
    @Autowired
    private WechatAuthService authService;
    
    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest request) {
        // 一行代码搞定登录
        AuthResult result = authService.login(request.getCode());
        return Result.success(result);
    }
}
```

**支付功能：**

```java
@RestController
@RequestMapping("/api/pay")
public class PayController {
    
    @Autowired
    private WechatPayService payService;
    
    @PostMapping("/order")
    public Result createOrder(@RequestBody PayRequest request) {
        // 一行代码创建支付订单
        PayResult result = payService.unifiedOrder(request);
        return Result.success(result);
    }
}
```

---

## 2. 模块说明

### 2.1 wechat-starter-core（核心模块）

**功能**：
- 自动配置机制
- 统一异常处理
- 工具类封装（JSON、签名、日期、字符串）
- 常量定义

**核心类**：
- `WechatProperties` - 配置属性
- `WechatAutoConfiguration` - 自动配置
- `WechatException` - 统一异常
- `SignUtils` - 签名工具
- `JsonUtils` - JSON 工具

### 2.2 wechat-starter-auth（登录授权）

**功能**：
- 小程序登录
- 获取用户信息
- Token 管理

**API 接口**：
```
POST /api/auth/login        # 登录
GET  /api/auth/user/{id}    # 获取用户信息
POST /api/auth/refresh      # 刷新 Token
```

**使用示例**：
```java
@Autowired
private WechatAuthService authService;

// 小程序登录
AuthResult loginResult = authService.login(code);
String openid = loginResult.getOpenid();
String sessionKey = loginResult.getSessionKey();
```

### 2.3 wechat-starter-pay（支付模块）

**功能**：
- 统一下单
- 订单查询
- 关闭订单
- 申请退款
- 退款查询
- 支付回调

**安全特性**：
- ✅ 参数严格校验
- ✅ 幂等性保护（防重复支付）
- ✅ 签名验证
- ✅ 金额双重校验
- ✅ 完整日志记录

**API 接口**：
```
POST /api/pay/order          # 创建订单
GET  /api/pay/query/{orderNo} # 查询订单
POST /api/pay/close/{orderNo} # 关闭订单
POST /api/pay/refund         # 申请退款
GET  /api/pay/refund/query   # 查询退款
POST /api/pay/notify         # 支付回调
```

**使用示例**：
```java
@Autowired
private WechatPayService payService;

// 创建支付订单
PayRequest request = new PayRequest();
request.setOutTradeNo("ORDER_20260401_001");
request.setDescription("测试商品");
request.setAmount(new PayRequest.Amount(100, "CNY")); // 1 元
request.setAppId("wx123456");
request.setState("user_openid");

PayResult result = payService.unifiedOrder(request);

// 查询订单
PayQueryResult queryResult = payService.queryOrder("ORDER_20260401_001");

// 申请退款
RefundRequest refund = new RefundRequest();
refund.setOutTradeNo("ORDER_20260401_001");
refund.setOutRefundNo("REFUND_001");
refund.setRefund(100);
refund.setTotal(100);
refund.setReason("用户申请退款");

boolean success = payService.refund(refund);
```

### 2.4 wechat-starter-message（消息推送）

**功能**：
- 订阅消息发送
- 批量发送
- 模板管理（灵活配置）

**特色**：
- ✅ 支持 4 种预置模板（订单、支付、物流、预约）
- ✅ 动态字段支持
- ✅ 模板验证机制
- ✅ 批量发送

**使用示例**：
```java
@Autowired
private WechatMessageService messageService;

// 发送订单通知
MessageSendRequest request = new MessageSendRequest();
request.setToUser("user_openid");
request.setTemplateId("ORDER_NOTIFY");
request.setPage("/pages/order/detail?id=123");

Map<String, MessageSendRequest.MessageData> data = new HashMap<>();
data.put("thing1", new MessageSendRequest.MessageData("ORDER001", "#173177"));
data.put("thing2", new MessageSendRequest.MessageData("已发货", "#173177"));
data.put("time1", new MessageSendRequest.MessageData("2026-04-01 10:30", "#173177"));

request.setData(data);

MessageSendResult result = messageService.sendSubscribeMessage(request);

// 批量发送
List<String> users = Arrays.asList("openid1", "openid2", "openid3");
List<MessageSendResult> results = messageService.batchSend(
    users, 
    "ORDER_NOTIFY",
    data,
    "/pages/order/list"
);
```

### 2.5 wechat-starter-iot（IoT 设备管理）

**功能**：
- 设备注册
- 设备查询
- 设备状态管理
- 设备解绑
- 传感器数据处理

### 2.6 wechat-starter-qrcode（小程序码）

**功能**：
- 临时小程序码（支持无限场景值）
- 永久小程序码（10 万场景值）
- 批量生成
- 自定义样式（颜色、大小、Logo）

### 2.7 wechat-starter-customer-service（客服消息）

**功能**：
- 发送文本消息
- 发送图片/语音/视频
- 发送小程序卡片
- 消息频率限制检查

### 2.8 wechat-starter-user-auth（用户授权）

**功能**：
- 获取用户基本信息（昵称、头像）
- 获取用户手机号
- OpenGID 跨小程序识别
- 用户信息缓存

**使用示例**：
```java
@Autowired
private DeviceService deviceService;

// 注册设备
Device device = new Device();
device.setName("智能温控器");
device.setType("sensor");
device.setDeviceIdentify("MAC_00:11:22:33:44:55");
device.setOwnerOpenId("user_openid");

Device registered = deviceService.register(device);
String deviceId = registered.getDeviceId();

// 查询设备
Device found = deviceService.findById(deviceId);

// 更新状态
deviceService.updateStatus(deviceId, "online");

// 查询用户所有设备
List<Device> userDevices = deviceService.findByUser("user_openid");

// 传感器数据
@Autowired
private SensorDataHandler sensorHandler;

SensorData data = sensorHandler.receiveData(
    deviceId,
    "temperature",
    25.5,
    "℃"
);
```

---

## 3. 配置说明

### 3.1 小程序配置

| 配置项 | 说明 | 必填 | 示例 |
|--------|------|------|------|
| `wechat.miniapp.appId` | 小程序 AppID | 是 | `wx1234567890abcdef` |
| `wechat.miniapp.appSecret` | 小程序密钥 | 是 | `1234567890abcdef...` |
| `wechat.miniapp.token` | 接口令牌 | 否 | `your-token` |
| `wechat.miniapp.aesKey` | 加解密密钥 | 否 | `your-aes-key` |

### 3.2 支付配置

| 配置项 | 说明 | 必填 | 示例 |
|--------|------|------|------|
| `wechat.pay.mchId` | 商户号 | 是（支付模块） | `1234567890` |
| `wechat.pay.apiKey` | API 密钥 v2 | 是（v2 支付） | `your-api-key` |
| `wechat.pay.apiV3Key` | API 密钥 v3 | 是（v3 支付） | `your-api-v3-key` |
| `wechat.pay.notifyUrl` | 回调地址 | 是 | `https://.../api/pay/notify` |
| `wechat.pay.sandbox` | 沙箱模式 | 否 | `false` |

### 3.3 Redis 配置（可选）

| 配置项 | 说明 | 默认值 | 示例 |
|--------|------|--------|------|
| `wechat.redis.enabled` | 是否启用 | `false` | `true` |
| `wechat.redis.host` | 主机地址 | `localhost` | `redis.example.com` |
| `wechat.redis.port` | 端口 | `6379` | `6379` |
| `wechat.redis.password` | 密码 | `null` | `your-password` |
| `wechat.redis.database` | 数据库编号 | `0` | `1` |
| `wechat.redis.expireSeconds` | 缓存过期时间 | `7200` | `3600` |

---

## 4. 使用示例

### 4.1 完整登录流程

```java
// 1. 小程序端获取 code
String code = WXApi.login();

// 2. 发送登录请求到后端
LoginRequest request = new LoginRequest();
request.setCode(code);

// 3. 后端处理
AuthResult result = authService.login(request.getCode());

// 4. 生成自己的 Token
String accessToken = Jwts.builder()
    .setSubject(result.getOpenid())
    .setExpiration(new Date(System.currentTimeMillis() + 7200000))
    .signWith(SignatureAlgorithm.HS256, "your-secret")
    .compact();

// 5. 返回给前端
return new LoginResponse(accessToken, result.getOpenid());
```

### 4.2 完整支付流程

```java
// 1. 创建订单
PayRequest request = new PayRequest();
request.setOutTradeNo("ORDER_" + System.currentTimeMillis());
request.setDescription("商品名称");
request.setAmount(new PayRequest.Amount(100, "CNY"));
request.setAppId(wxAppId);
request.setState(userOpenid);
request.setNotifyUrl(notifyUrl);

PayResult orderResult = payService.unifiedOrder(request);

// 2. 返回支付参数给前端（需要调起支付）

// 3. 微信回调通知（异步）
@PostMapping("/api/pay/notify")
public String handleNotify(HttpServletRequest request) {
    // 验签、处理业务逻辑
    // 返回成功响应
    return "<result>success</result>";
}

// 4. 主动查询订单状态（可选）
PayQueryResult queryResult = payService.queryOrder(orderNo);
if ("SUCCESS".equals(queryResult.getTradeState())) {
    // 更新订单状态
}
```

### 4.3 发送订阅消息

```java
// 订单支付成功后发送通知
MessageSendRequest request = new MessageSendRequest();
request.setToUser(userOpenid);
request.setTemplateId("PAYMENT_SUCCESS");
request.setPage("/pages/order/success?id=" + orderNo);

Map<String, MessageSendRequest.MessageData> data = new HashMap<>();
data.put("thing1", new MessageSendRequest.MessageData(orderName, null));
data.put("price1", new MessageSendRequest.MessageData("¥" + price, null));
data.put("time1", new MessageSendRequest.MessageData(orderTime, null));

request.setData(data);

MessageSendResult result = messageService.sendSubscribeMessage(request);
```

---

## 5. 常见问题

### Q1: 登录失败，提示"登录凭证已过期"

**原因**：`code` 只能使用一次，且 5 分钟内有效。

**解决**：
- 确保 `code` 是刚获取的
- 不要重复使用同一个 `code`
- 检查小程序端是否正确获取 `code`

### Q2: 支付回调收不到

**原因**：
1. 回调地址不是 HTTPS
2. 回调地址内网访问
3. 服务器没有公网 IP

**解决**：
- 使用 HTTPS 地址
- 使用内网穿透工具（如 ngrok）
- 配置域名和 SSL 证书

### Q3: 如何调试支付问题？

**建议**：
1. 开启沙箱模式：`wechat.pay.sandbox=true`
2. 查看日志：`logs/pay.log`
3. 使用微信支付的官方调试工具

### Q4: 消息发送失败

**可能原因**：
1. 用户未授权订阅
2. 模板 ID 错误
3. 字段不匹配

**解决**：
- 确认用户已点击"订阅"按钮
- 检查模板 ID 是否与微信后台一致
- 确保字段名和数量与模板定义一致

### Q5: 如何扩展新的消息模板？

**步骤**：
```java
@Autowired
private MessageTemplateManager templateManager;

// 定义新模板
MessageTemplateConfig config = new MessageTemplateConfig();
config.setTemplateId("NEW_TEMPLATE");
config.setName("新模板");
config.setFields(...);

// 添加到管理器
templateManager.addTemplate(config);
```

---

## 6. API 文档

### 6.1 登录授权接口

#### POST /api/auth/login

**请求参数**：
```json
{
  "code": "071xxx08",
  "extraData": "可选的额外数据"
}
```

**响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "openid": "oXXXX",
    "sessionKey": "session_key",
    "accessToken": "your_token"
  }
}
```

#### GET /api/auth/user/{openId}

**响应**：
```json
{
  "code": 200,
  "data": {
    "nickName": "用户昵称",
    "avatarUrl": "头像 URL"
  }
}
```

### 6.2 支付接口

详见 `wechat-starter-pay` 模块的 Controller 注释。

---

## 📚 其他资源

- **GitHub 项目**: https://github.com/zslin/wechat-starter
- **开发手册**: `D:/work/plan/wechat-starter-dev-guide.md`
- **Agent 配置**: `D:/work/plan/wechat-starter-agent-config.md`

---

## 🆘 获取帮助

遇到问题？可以通过以下方式：

1. 查看项目日志
2. 查看 GitHub Issues
3. 联系作者：钟述林

---

**祝你开发顺利！** 🦞

*文档版本：1.0 | 最后更新：2026-04-01*
