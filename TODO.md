# 待办事项 - 微信支付 SDK

## 编译错误修复（优先级：紧急）

### 1. 缺少的方法
- [ ] `WechatProperties.Pay.getSerialNo()` - 需要添加 getter 方法
- [ ] `WechatProperties.Pay.getWechatPublicKeyPem()` - 需要添加 getter 方法
- [ ] `WechatProperties.Pay.getApiUrl()` - 需要添加 getter 方法
- [ ] `WechatProperties.Pay.getPrivateKeyPem()` - 需要添加 getter 方法

### 2. 缺少的工具方法
- [ ] `SignUtils.loadPublicKeyFromPem(String)` - 加载公钥
- [ ] `SignUtils.verifyV3(String, PublicKey, String)` - V3 验签
- [ ] `SignUtils.signV3(String, PrivateKey)` - V3 签名

### 3. 缺少的错误码
- [ ] `WechatExceptionCodes.PAY_PARSE_ERROR` - 需要添加

### 4. 测试修复
- [ ] `ProfitSharingServiceTest` - 3 个失败用例
- [ ] `TransferServiceTest` - 4 个失败用例

## 建议修复步骤

1. **第一步**: 添加 WechatProperties.Pay 的缺失 getter 方法
2. **第二步**: 添加 SignUtils 的缺失方法
3. **第三步**: 添加 WechatExceptionCodes 的缺失枚举值
4. **第四步**: 重新编译并修复测试

## 预计修复时间
- 编译错误：30 分钟
- 测试修复：30 分钟
- 总计：1 小时
