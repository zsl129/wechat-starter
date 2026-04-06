# 变更日志 (Changelog)

格式：遵循 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)

## [Unreleased]

### 已添加
- 🎯 项目初始化完成
- 📦 wechat-starter-core 模块基础架构
  - 自动配置类 (WechatAutoConfiguration)
  - 配置属性类 (WechatProperties)
  - 异常处理机制 (WechatException, WechatExceptionCodes)
  - 工具类 (JsonUtils, SignUtils, StringUtils, DateUtils)
- 🔐 wechat-starter-auth 模块基础架构
  - 登录授权 Service (WechatAuthService)
  - 登录接口 (AuthController)
  - DTO 类 (LoginRequest, AuthResult, UserInfo)
- 📝 5 个模块的目录结构已创建
- 📖 README.md 文档

### 待开发
- wechat-starter-pay 模块（支付功能）
- wechat-starter-message 模块（消息推送）
- wechat-starter-iot 模块（IoT 设备管理）
- 单元测试补充
- 集成测试
- API 文档

---

## 版本规划

- **1.0.0-SNAPSHOT** (当前) - 开发中
- **1.0.0-RC1** - 发布候选（预计 2026-04-30）
- **1.0.0** - 正式版（预计 2026-05-01）

---

*最后更新：2026-04-01*
