# WeChat-Starter API 文档

> **版本**: v1.0.0-SNAPSHOT  
> **最后更新**: 2026-04-06  
> **开发者**: 子墨 🦞

---

## 📖 目录

1. [概述](#概述)
2. [认证授权模块](#认证授权模块)
3. [支付模块](#支付模块)
4. [消息推送模块](#消息推送模块)
5. [IoT 设备管理模块](#iot 设备管理模块)
6. [分账模块](#分账模块)
7. [转账与红包模块](#转账与红包模块)
8. [小程序码模块](#小程序码模块)
9. [错误码说明](#错误码说明)

---

## 概述

### 基础信息

- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **字符编码**: UTF-8

### 认证方式

目前 API 不需要额外的认证，但在生产环境中建议添加 JWT 或其他认证机制。

### 响应格式

所有接口统一返回格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 配置示例

```yaml
server:
  port: 8080

wechat:
  miniapp:
    appId: wx1234567890abcdef
    appSecret: 1234567890abcdef1234567890abcdef
  
  pay:
    mchId: 1234567890
    apiKey: your-api-key
    apiV3Key: your-api-v3-key
    notifyUrl: https://yourdomain.com/api/pay/notify
```

---

## 认证授权模块

### 1. 小程序登录

**接口**: `POST /api/auth/login`

**描述**: 使用小程序 code 换取用户的 OpenID 和 SessionKey

**请求参数**:

```json
{
  "code": "071xxx08",
  "extraData": "可选的额外数据"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| code | String | 是 | 小程序登录凭证 |
| extraData | String | 否 | 额外数据，可选 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "openid": "oXXXX",
    "sessionKey": "session_key",
    "unionid": "unionid_if_available"
  }
}
```

---

### 2. 获取用户信息

**接口**: `GET /api/auth/user/{openid}`

**描述**: 根据 OpenID 获取用户基本信息

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| openid | String | 是 | 用户 OpenID |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "openid": "oXXXX",
    "nickName": "用户昵称",
    "avatarUrl": "头像 URL",
    "gender": 1,
    "city": "城市",
    "province": "省份",
    "country": "国家"
  }
}
```

---

### 3. 获取手机号

**接口**: `POST /api/auth/phone/decrypt`

**描述**: 解密用户手机号加密数据

**请求参数**:

```json
{
  "encryptedData": "加密后的手机号数据",
  "iv": "加密算法的初始向量",
  "sessionKey": "会话密钥"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| encryptedData | String | 是 | 加密数据 |
| iv | String | 是 | 初始向量 |
| sessionKey | String | 是 | 会话密钥 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "phoneNumber": "13800138000",
    "purePhoneNumber": "13800138000",
    "countryCode": "86"
  }
}
```

---

## 支付模块

### 1. 创建支付订单

**接口**: `POST /api/pay/order`

**描述**: 创建 JSAPI 支付订单

**请求参数**:

```json
{
  "outTradeNo": "ORDER_20260406_001",
  "description": "商品描述",
  "amount": 100,
  "openid": "user_openid",
  "appId": "wx1234567890",
  "notifyUrl": "https://yourdomain.com/api/pay/notify",
  "attach": "附加数据"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| outTradeNo | String | 是 | 商户订单号，最大 32 字符 |
| description | String | 是 | 订单描述 |
| amount | Integer | 是 | 订单金额，单位：分 |
| openid | String | 是 | 用户 OpenID |
| appId | String | 是 | 小程序 AppID |
| notifyUrl | String | 否 | 回调地址，默认使用配置值 |
| attach | String | 否 | 附加数据 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "transactionId": "4200001234567890",
    "outTradeNo": "ORDER_20260406_001",
    "tradeState": "SUCCESS",
    "tradeType": "JSAPI",
    "amount": 100,
    "successTime": "2026-04-06 11:00:00"
  }
}
```

---

### 2. 查询订单

**接口**: `GET /api/pay/query`

**描述**: 根据商户订单号或微信订单号查询订单状态

**查询参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| transactionId | String | 否 | 微信订单号 |
| outTradeNo | String | 否 | 商户订单号 |

**说明**: `transactionId` 和 `outTradeNo` 至少提供一个

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "transactionId": "4200001234567890",
    "outTradeNo": "ORDER_20260406_001",
    "tradeType": "JSAPI",
    "tradeState": "SUCCESS",
    "tradeStateDesc": "支付成功",
    "cashFee": 100,
    "total": 100,
    "successTime": "2026-04-06 11:00:00",
    "billDownloadUrl": "https://api.mch.weixin.qq.com/bill/v3/download/xxxxx"
  }
}
```

---

### 3. 申请退款

**接口**: `POST /api/pay/refund`

**描述**: 申请订单退款

**请求参数**:

```json
{
  "outTradeNo": "ORDER_20260406_001",
  "outRefundNo": "REFUND_20260406_001",
  "refund": 100,
  "total": 100,
  "reason": "用户申请退款"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| outTradeNo | String | 是 | 商户订单号 |
| outRefundNo | String | 是 | 商户退款单号 |
| refund | Integer | 是 | 退款金额，单位：分 |
| total | Integer | 是 | 原订单金额，单位：分 |
| reason | String | 否 | 退款原因 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "success": true,
    "outTradeNo": "ORDER_20260406_001",
    "outRefundNo": "REFUND_20260406_001",
    "refund": 100,
    "total": 100,
    "refundStatus": "SUCCESS",
    "message": "退款成功"
  }
}
```

---

### 4. 支付回调通知

**接口**: `POST /api/pay/notify`

**描述**: 微信支付结果异步通知

**说明**: 
- 微信服务器会多次发送回调通知
- 必须返回特定格式才能确认收到通知
- 需要验签确保安全性

**响应格式**:

**成功响应**:
```xml
<result>success</result>
```

**失败响应**:
```xml
<result>fail</result>
```

**回调数据示例**:

```json
{
  "id": "eventId_123456",
  "create_time": "2026-04-06T11:00:00+08:00",
  "resource_type": "encrypt-config",
  "resource": {
    "algorithm": "AES256_GCM",
    "ciphertext": "加密后的数据",
    "nonce": "随机串",
    "associated_data": "订单号"
  },
  "summary": "支付结果通知",
  "event_payload": {
    "transaction_id": "4200001234567890",
    "out_trade_no": "ORDER_20260406_001",
    "trade_state": "SUCCESS"
  }
}
```

---

## 消息推送模块

### 1. 发送订阅消息

**接口**: `POST /api/message/send`

**描述**: 发送单条订阅消息

**请求参数**:

```json
{
  "toUser": "user_openid",
  "templateId": "ORDER_NOTIFY",
  "page": "/pages/order/detail?id=123",
  "data": {
    "thing1": {
      "value": "测试订单",
      "color": "#173177"
    },
    "time1": {
      "value": "2026-04-06 11:00",
      "color": "#173177"
    }
  }
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| toUser | String | 是 | 用户 OpenID |
| templateId | String | 是 | 模板 ID |
| page | String | 否 | 点击消息跳转页面 |
| data | Object | 是 | 消息字段内容 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "errCode": 0,
    "errMsg": "ok",
    "msgId": "1234567890"
  }
}
```

---

### 2. 批量发送消息

**接口**: `POST /api/message/batch-send`

**描述**: 批量发送订阅消息

**请求参数**:

```json
{
  "toUsers": ["openid1", "openid2", "openid3"],
  "templateId": "ORDER_NOTIFY",
  "data": {
    "thing1": {
      "value": "测试订单",
      "color": "#173177"
    }
  },
  "page": "/pages/order/list"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| toUsers | Array | 是 | 用户 OpenID 列表 |
| templateId | String | 是 | 模板 ID |
| data | Object | 是 | 消息字段内容 |
| page | String | 否 | 跳转页面 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalCount": 3,
    "successCount": 3,
    "failCount": 0,
    "results": [
      {"toUser": "openid1", "success": true, "msgId": "123"},
      {"toUser": "openid2", "success": true, "msgId": "124"},
      {"toUser": "openid3", "success": true, "msgId": "125"}
    ]
  }
}
```

---

### 3. 获取消息模板

**接口**: `GET /api/message/template/{templateId}`

**描述**: 根据模板 ID 获取模板信息

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| templateId | String | 是 | 模板 ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "templateId": "ORDER_NOTIFY",
    "name": "订单通知",
    "fields": [
      {"name": "thing1", "type": "THING"},
      {"name": "time1", "type": "TIME"}
    ]
  }
}
```

---

### 4. 添加消息模板

**接口**: `POST /api/message/template/add`

**描述**: 添加自定义消息模板

**请求参数**:

```json
{
  "templateId": "CUSTOM_TEMPLATE",
  "name": "自定义模板",
  "fields": [
    {"name": "field1", "type": "TEXT"},
    {"name": "field2", "type": "NUMBER"}
  ]
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

## IoT 设备管理模块

### 1. 注册设备

**接口**: `POST /api/iot/device/register`

**描述**: 注册新的 IoT 设备

**请求参数**:

```json
{
  "name": "智能温控器",
  "type": "sensor",
  "deviceIdentify": "MAC_00:11:22:33:44:55",
  "ownerOpenId": "user_openid",
  "status": "online"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 设备名称 |
| type | String | 是 | 设备类型 |
| deviceIdentify | String | 是 | 设备唯一标识 |
| ownerOpenId | String | 是 | 所有者 OpenID |
| status | String | 否 | 设备状态，默认为 online |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "deviceId": "device_001",
    "name": "智能温控器",
    "type": "sensor",
    "deviceIdentify": "MAC_00:11:22:33:44:55",
    "ownerOpenId": "user_openid",
    "status": "online",
    "createTime": "2026-04-06 11:00:00"
  }
}
```

---

### 2. 查询设备

**接口**: `GET /api/iot/device/{deviceId}`

**描述**: 根据设备 ID 查询设备信息

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| deviceId | String | 是 | 设备 ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "deviceId": "device_001",
    "name": "智能温控器",
    "type": "sensor",
    "status": "online"
  }
}
```

---

### 3. 查询用户所有设备

**接口**: `GET /api/iot/device/user/{openId}`

**描述**: 查询用户拥有的所有设备

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| openId | String | 是 | 用户 OpenID |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "deviceId": "device_001",
      "name": "智能温控器",
      "status": "online"
    },
    {
      "deviceId": "device_002",
      "name": "智能插座",
      "status": "offline"
    }
  ]
}
```

---

### 4. 更新设备状态

**接口**: `PUT /api/iot/device/{deviceId}/status`

**描述**: 更新设备状态

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| deviceId | String | 是 | 设备 ID |

**请求参数**:

```json
{
  "status": "offline"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

### 5. 解绑设备

**接口**: `DELETE /api/iot/device/{deviceId}/unbind`

**描述**: 解绑设备

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| deviceId | String | 是 | 设备 ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

### 6. 删除设备

**接口**: `DELETE /api/iot/device/{deviceId}`

**描述**: 删除设备

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| deviceId | String | 是 | 设备 ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

### 7. 接收传感器数据

**接口**: `POST /api/iot/sensor/data`

**描述**: 接收设备传感器上报的数据

**请求参数**:

```json
{
  "deviceId": "device_001",
  "sensorType": "temperature",
  "value": 25.5,
  "unit": "℃",
  "timestamp": 1680788400000
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| deviceId | String | 是 | 设备 ID |
| sensorType | String | 是 | 传感器类型 |
| value | Double | 是 | 传感器值 |
| unit | String | 否 | 单位 |
| timestamp | Long | 否 | 时间戳 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "success": true,
    "receiveTime": "2026-04-06 11:00:00"
  }
}
```

---

### 8. 批量接收传感器数据

**接口**: `POST /api/iot/sensor/data/batch`

**描述**: 批量接收传感器数据

**请求参数**:

```json
[
  {
    "deviceId": "device_001",
    "sensorType": "temperature",
    "value": 25.5
  },
  {
    "deviceId": "device_001",
    "sensorType": "humidity",
    "value": 60.0
  }
]
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalCount": 2,
    "successCount": 2
  }
}
```

---

## 分账模块

### 1. 分账下单

**接口**: `POST /api/pay/profit-sharing/order`

**描述**: 创建分账订单

**请求参数**:

```json
{
  "transactionId": "4200001234567890",
  "outRequestNo": "SHARE_REQ_001",
  "receiver": "wx1234567890",
  "amount": 100,
  "operator": "operator_openid"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| transactionId | String | 是 | 微信支付订单号 |
| outRequestNo | String | 是 | 商户分账请求单号 |
| receiver | String | 是 | 接收者 AppID |
| amount | Integer | 是 | 分账金额，单位：分 |
| operator | String | 否 | 操作者 OpenID |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "success": true,
    "transactionId": "4200001234567890",
    "outRequestNo": "SHARE_REQ_001"
  }
}
```

---

### 2. 查询分账结果

**接口**: `GET /api/pay/profit-sharing/query`

**描述**: 查询分账结果

**查询参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| transactionId | String | 否 | 微信支付订单号 |
| outRequestNo | String | 否 | 商户分账请求单号 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "transactionId": "4200001234567890",
    "outRequestNo": "SHARE_REQ_001",
    "status": "SUCCESS",
    "receivers": [...]
  }
}
```

---

### 3. 分账回款

**接口**: `POST /api/pay/profit-sharing/return`

**描述**: 申请分账回款

**请求参数**:

```json
{
  "transactionId": "4200001234567890",
  "outReturnNo": "RETURN_REQ_001",
  "receiver": "wx1234567890",
  "amount": 50,
  "returnType": "BENEFICIARY"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "success": true,
    "outReturnNo": "RETURN_REQ_001"
  }
}
```

---

### 4. 查询分账回款结果

**接口**: `GET /api/pay/profit-sharing/return/query`

**描述**: 查询分账回款结果

**查询参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| outReturnNo | String | 是 | 商户回款请求单号 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "outReturnNo": "RETURN_REQ_001",
    "status": "SUCCESS",
    "amount": 50
  }
}
```

---

## 转账与红包模块

### 1. 商家转账

**接口**: `POST /api/pay/transfer`

**描述**: 商家转账到零钱

**请求参数**:

```json
{
  "userOpenid": "user_openid",
  "amount": 100,
  "remark": "转账说明",
  "outCmdNo": "TRANSFER_001"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userOpenid | String | 是 | 用户 OpenID |
| amount | Integer | 是 | 转账金额，单位：分 |
| remark | String | 否 | 转账说明 |
| outCmdNo | String | 否 | 商户订单号 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "success": true,
    "outCmdNo": "TRANSFER_001",
    "transactionId": "4200001234567890"
  }
}
```

---

### 2. 查询转账

**接口**: `GET /api/pay/transfer/query`

**描述**: 查询转账结果

**查询参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| outCmdNo | String | 否 | 商户订单号 |
| transactionId | String | 否 | 微信支付订单号 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "outCmdNo": "TRANSFER_001",
    "status": "SUCCESS",
    "amount": 100
  }
}
```

---

### 3. 发放红包

**接口**: `POST /api/pay/red-packet`

**描述**: 发放普通红包

**请求参数**:

```json
{
  "userOpenid": "user_openid",
  "amount": 100,
  "title": "红包标题",
  "remark": "红包备注",
  "outCmdNo": "REDPACKET_001"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userOpenid | String | 是 | 用户 OpenID |
| amount | Integer | 是 | 红包金额，1-200 元 |
| title | String | 否 | 红包标题 |
| remark | String | 否 | 红包备注 |
| outCmdNo | String | 否 | 商户订单号 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "success": true,
    "outCmdNo": "REDPACKET_001",
    "transactionId": "4200001234567890"
  }
}
```

---

### 4. 查询红包

**接口**: `GET /api/pay/red-packet/query`

**描述**: 查询红包结果

**查询参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| outCmdNo | String | 是 | 商户订单号 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "outCmdNo": "REDPACKET_001",
    "status": "SUCCESS",
    "totalAmount": 100
  }
}
```

---

## 小程序码模块

### 1. 生成小程序码

**接口**: `POST /api/qrcode/create`

**描述**: 生成小程序临时码或永久码

**请求参数**:

```json
{
  "scene": "scene_123",
  "autoColor": false,
  "lineColor": {
    "r": 0,
    "g": 0,
    "b": 0
  },
  "isHyaline": false,
  "width": 430,
  "envVersion": "release"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| scene | String | 是 | 场景值 |
| autoColor | Boolean | 否 | 自动颜色，默认 false |
| lineColor | Object | 否 | 线条颜色 |
| isHyaline | Boolean | 否 | 透明背景，默认 false |
| width | Integer | 否 | 二维码尺寸，默认 430 |
| envVersion | String | 否 | 环境版本，release/trial/dev |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "url": "https://wx.qlogo.cn/mmqrcode/xxx",
    "scene": "scene_123",
    "expireSeconds": 0
  }
}
```

---

## 错误码说明

### 通用错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 微信业务错误码

| 错误码 | 说明 |
|--------|------|
| 60001 | 配置错误 |
| 60002 | 登录失败 |
| 60003 | 获取用户信息失败 |
| 61001 | 支付初始化失败 |
| 61002 | 支付 API 错误 |
| 61003 | 订单已支付 |
| 61004 | 退款不允许 |
| 61005 | 退款失败 |
| 62001 | 消息发送失败 |
| 63001 | 设备注册失败 |
| 63002 | 设备不存在 |
| 64001 | 分账失败 |
| 65001 | 转账失败 |
| 66001 | 小程序码生成失败 |

---

## 📚 附录

### A. 测试环境配置

```yaml
wechat:
  pay:
    sandbox: true  # 开启沙箱模式
```

### B. 常见问题

**Q1: 支付回调收不到怎么办？**
A: 确保回调地址是 HTTPS，且服务器有公网 IP 或使用内网穿透工具。

**Q2: 消息发送失败？**
A: 检查用户是否已授权订阅，模板 ID 是否正确。

**Q3: 如何调试支付问题？**
A: 开启沙箱模式，查看详细日志。

### C. 性能建议

- 登录接口响应时间：< 500ms
- 支付订单创建：< 1s
- 消息批量发送：建议使用异步处理

---

**API 文档版本：1.0 | 最后更新：2026-04-06**
**开发团队：子墨 🦞 & 钟述林**
