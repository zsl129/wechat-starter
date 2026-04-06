# WeChat Starter - 微信开发 Starter 框架

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17-green.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.6-green.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.8.1+-blue.svg)](https://maven.apache.org/)

## 📖 项目简介

WeChat Starter 是一个完整的微信开发 Starter 框架，提供微信支付、认证、消息推送、IoT 设备等核心功能的快速集成。

### ✨ 核心特性

- **微信支付** - 支持 JSAPI、Native、APP 支付、退款、分账、商家转账、红包等功能
- **微信认证** - 支持小程序、公众号、开放平台认证
- **消息推送** - 支持模板消息、订阅消息推送
- **IoT 设备** - 支持微信 IoT 设备管理
- **安全加密** - 内置签名、加密、解密等安全工具
- **易于集成** - 提供完整示例和详细文档

## 📦 项目结构

```
wechat-starter/
├── wechat-starter-core/     # 核心模块（配置、工具、加密）
├── wechat-starter-auth/     # 认证模块
├── wechat-starter-pay/      # 支付模块
├── wechat-starter-message/  # 消息模块
├── wechat-starter-iot/      # IoT 设备模块
└── README.md
```

## 🚀 快速开始

### 1. 添加依赖

```xml
<dependencies>
    <!-- 核心模块 -->
    <dependency>
        <groupId>com.zslin</groupId>
        <artifactId>wechat-starter-core</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    
    <!-- 支付模块 -->
    <dependency>
        <groupId>com.zslin</groupId>
        <artifactId>wechat-starter-pay</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### 2. 配置参数

```yaml
wechat:
  pay:
    mch-id: 你的商户号
    api-v3-key: 你的 APIv3 密钥
    serial-no: 你的证书序列号
    private-key-pem: |
      -----BEGIN PRIVATE KEY-----
      你的私钥内容
      -----END PRIVATE KEY-----
    notify-url: 你的回调地址
```

### 3. 使用示例

```java
@Autowired
private PayService payService;

public void createOrder(PayRequest request) {
    PayResult result = payService.jsapiPay(request);
    // 返回给前端
}
```

## 📚 文档

- [API 文档](wechat-starter-pay/docs/API.md)
- [使用示例](wechat-starter-pay/docs/示例代码.md)
- [常见问题](wechat-starter-pay/docs/常见问题.md)
- [开发指南](wechat-starter-pay/docs/开发指南.md)

## 📊 功能列表

### 支付功能
- ✅ JSAPI 支付（公众号支付）
- ✅ Native 支付（扫码支付）
- ✅ APP 支付
- ✅ 订单查询
- ✅ 退款服务
- ✅ 分账功能
- ✅ 商家转账
- ✅ 红包发放

### 认证功能
- ✅ 小程序登录
- ✅ 公众号授权
- ✅ 手机号解密

### 工具类
- ✅ 签名工具（RSA、HMAC）
- ✅ 加密工具（AES、AES-GCM）
- ✅ HTTP 工具
- ✅ JSON 工具
- ✅ 时间工具

## 🧪 测试

所有核心功能均已通过 54 个测试用例，100% 通过率。

```bash
mvn test
```

## 📝 版本历史

- v1.0.0-SNAPSHOT (2026-04-06)
  - 初始版本
  - 完成支付核心功能
  - 完成认证功能
  - 完成工具类

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 开源协议

MIT License

## 👤 作者

- **子墨** - 项目开发与维护

## 📧 联系方式

如有问题，请提 Issue 或联系作者。

---

**开发时间**: 2026-04-04 ~ 2026-04-06
**代码行数**: 10,300+ 行
**测试用例**: 54 个
**文档数量**: 7 份
