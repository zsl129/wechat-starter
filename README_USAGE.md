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

**后端环境：**
- **JDK**: 1.8+
- **Maven**: 3.6+
- **Spring Boot**: 2.7.18 或 3.2.x

**前端环境：**
- **微信开发者工具**
- **小程序基础库**: 2.20.0+

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

## 3. 完整集成示例（重要！）

### 3.1 场景一：小程序登录 + 支付完整流程

这是一个**完整的示例**，展示从用户打开小程序到完成支付的整个过程。

#### 第一步：后端配置

**1. 创建 Controller**

```java
package com.example.demo.controller;

import com.zslin.wechat.auth.service.WechatAuthService;
import com.zslin.wechat.pay.service.WechatPayService;
import com.zslin.wechat.pay.request.PayRequest;
import com.zslin.wechat.pay.result.PayResult;
import com.zslin.wechat.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
public class OrderController {
    
    @Autowired
    private WechatAuthService authService;
    
    @Autowired
    private WechatPayService payService;
    
    /**
     * 小程序登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest request) {
        try {
            // 1. 使用 SDK 登录
            var authResult = authService.login(request.getCode());
            
            // 2. 生成自己的 Token（可选）
            String token = generateToken(authResult.getOpenid());
            
            // 3. 返回结果
            return Result.success(new LoginResponse(token, authResult.getOpenid()));
        } catch (Exception e) {
            return Result.fail("登录失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建订单并返回支付参数
     */
    @PostMapping("/order/create")
    public Result createOrder(@RequestBody CreateOrderRequest request) {
        try {
            // 1. 校验用户身份
            String openid = getUserOpenid(request.getToken());
            if (openid == null) {
                return Result.fail("用户未登录");
            }
            
            // 2. 创建业务订单（保存到数据库）
            String orderNo = "ORDER_" + System.currentTimeMillis();
            saveBusinessOrder(orderNo, request.getProductId(), request.getAmount(), openid);
            
            // 3. 调用微信支付 SDK 创建订单
            var payRequest = new PayRequest();
            payRequest.setOutTradeNo(orderNo);
            payRequest.setDescription(request.getProductName());
            payRequest.setAmount(new PayRequest.Amount(
                request.getAmount().multiply(new BigDecimal("100")).intValue(), // 转换分为元
                "CNY"
            ));
            payRequest.setAppId(""); // SDK 自动从配置读取
            payRequest.setState(openid);
            
            var result = payService.unifiedOrder(payRequest);
            
            // 4. 返回支付参数给前端
            return Result.success(new PayResponse(
                result.getTimestamp(),
                result.getNonceStr(),
                result.getPackageValue(),
                result.getSignType(),
                result.getPaySign()
            ));
        } catch (Exception e) {
            return Result.fail("创建订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 支付回调
     */
    @PostMapping("/pay/notify")
    public String handleNotify(@RequestBody Object requestBody) {
        try {
            // 1. SDK 会自动验签
            // 2. 处理业务逻辑（更新订单状态等）
            // 3. 返回成功响应
            return "<result>success</result>";
        } catch (Exception e) {
            return "<result>fail</result>";
        }
    }
    
    // 辅助方法...
    private String generateToken(String openid) { /* JWT 生成逻辑 */ }
    private String getUserOpenid(String token) { /* 从 JWT 解析 openid */ }
    private void saveBusinessOrder(String orderNo, String productId, BigDecimal amount, String openid) { /* 保存到数据库 */ }
}

// ============================================
// 完整的 DTO 类示例
// ============================================

// 登录请求
class LoginRequest {
    private String code;
    // getter/setter
}

// 登录响应
class LoginResponse {
    private String token;
    private String openid;
    // getter/setter
}

// 创建订单请求
class CreateOrderRequest {
    private String productId;
    private String productName;
    private BigDecimal amount;  // 单位：元
    private String token;
    // getter/setter
}

// 支付响应（返回给小程序的支付参数）
class PayResponse {
    private String timestamp;
    private String nonceStr;
    private String packageValue;
    private String signType;
    private String paySign;
    // getter/setter
}
```

#### 第二步：小程序端代码

**WXML 页面 (pages/order/order.wxml):**
```xml
<view class="container">
  <view class="product-info">
    <text>{{productName}}</text>
    <text class="price">¥{{price}}</text>
  </view>
  
  <button 
    wx:if="{{!hasPaid}}"
    bindtap="handlePay"
    class="pay-btn"
  >
    立即支付 {{price}} 元
  </button>
  
  <view wx:if="{{hasPaid}}" class="paid">
    支付成功 ✓
  </view>
  
  <view wx:if="{{loading}}" class="loading">
    支付中...
  </view>
</view>
```

**JS 逻辑 (pages/order/order.js):**
```javascript
const api = require('../../utils/api.js');

Page({
  data: {
    productId: 'product_123',
    productName: '测试商品',
    price: 99.00,
    hasPaid: false,
    loading: false,
    userOpenid: null
  },

  onLoad() {
    this.checkLogin();
  },

  // 检查登录状态
  async checkLogin() {
    try {
      // 1. 获取 code
      const codeRes = await wx.login();
      const code = codeRes.code;
      
      // 2. 调用后端登录接口
      const loginRes = await api.post('/api/login', { code });
      
      if (loginRes.code === 200) {
        // 3. 保存 token 和 openid
        wx.setStorageSync('token', loginRes.data.token);
        this.setData({ userOpenid: loginRes.data.openid });
      }
    } catch (error) {
      console.error('登录失败', error);
      wx.showToast({ title: '登录失败', icon: 'none' });
    }
  },

  // 发起支付
  async handlePay() {
    this.setData({ loading: true });
    
    try {
      // 1. 调用后端创建订单
      const createRes = await api.post('/api/order/create', {
        productId: this.data.productId,
        productName: this.data.productName,
        amount: this.data.price,
        token: wx.getStorageSync('token')
      });
      
      if (createRes.code !== 200) {
        throw new Error(createRes.message);
      }
      
      const { timestamp, nonceStr, package: pkg, signType, paySign } = createRes.data;
      
      // 2. 调起微信支付
      await wx.requestPayment({
        timeStamp: timestamp,
        nonceStr: nonceStr,
        package: pkg,
        signType: signType,
        paySign: paySign,
        success: () => {
          this.setData({ hasPaid: true, loading: false });
          wx.showToast({ title: '支付成功', icon: 'success' });
          this.checkOrderStatus(); // 主动查询订单状态
        },
        fail: (err) => {
          console.error('支付失败', err);
          wx.showToast({ title: '支付失败', icon: 'none' });
          this.setData({ loading: false });
        }
      });
      
    } catch (error) {
      console.error('支付错误', error);
      wx.showToast({ title: error.message || '支付失败', icon: 'none' });
      this.setData({ loading: false });
    }
  },

  // 主动查询订单状态（防止回调延迟）
  async checkOrderStatus() {
    // 延迟 2 秒后查询
    setTimeout(async () => {
      try {
        const res = await api.get('/api/order/status', {
          token: wx.getStorageSync('token')
        });
        if (res.data.status === 'PAID') {
          this.refreshPage();
        }
      } catch (e) {
        console.error('查询订单状态失败', e);
      }
    }, 2000);
  },

  refreshPage() {
    // 刷新页面或跳转到结果页
    wx.navigateTo({ url: '/pages/order/success' });
  }
});
```

**API 工具类 (utils/api.js):**
```javascript
const BASE_URL = 'https://your-domain.com'; // 替换为你的后端地址

export default {
  // GET 请求
  get(url, params = {}) {
    return new Promise((resolve, reject) => {
      wx.request({
        url: BASE_URL + url,
        method: 'GET',
        data: params,
        header: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + wx.getStorageSync('token')
        },
        success: res => resolve(res.data),
        fail: err => reject(err)
      });
    });
  },
  
  // POST 请求
  post(url, data = {}) {
    return new Promise((resolve, reject) => {
      wx.request({
        url: BASE_URL + url,
        method: 'POST',
        data: data,
        header: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + wx.getStorageSync('token')
        },
        success: res => resolve(res.data),
        fail: err => reject(err)
      });
    });
  }
};
```

#### 第三步：配置文件 (application.yml)

```yaml
server:
  port: 8080

spring:
  application:
    name: my-wechat-demo

# 微信支付配置
wechat:
  miniapp:
    appId: wx1234567890abcdef
    appSecret: your-app-secret
  
  pay:
    mchId: 1234567890
    apiKey: your-api-v2-key
    apiV3Key: your-api-v3-key
    notifyUrl: https://your-domain.com/api/pay/notify
    sandbox: false  # 生产环境设为 false

# 日志配置
logging:
  level:
    com.zslin.wechat: DEBUG  # 开发阶段开启 DEBUG
```

---

## 4. 使用示例

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
