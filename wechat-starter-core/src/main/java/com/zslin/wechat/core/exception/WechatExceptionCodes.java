package com.zslin.wechat.core.exception;

import lombok.Getter;

/**
 * 微信异常错误码枚举
 * <p>
 * 定义所有微信业务相关的错误码，便于统一错误处理
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Getter
public enum WechatExceptionCodes {

    // ========== 通用错误 (10000-19999) ==========
    PARAM_ERROR(10000, "参数错误"),
    INVALID_CONFIG(10001, "配置无效"),
    UNKNOWN_ERROR(19999, "未知错误"),

    // ========== 登录授权错误 (20000-29999) ==========
    CODE_EMPTY(20001, "登录凭证不能为空"),
    CODE_EXPIRED(20002, "登录凭证已过期"),
    WECHAT_LOGIN_FAILED(20003, "微信登录失败"),
    TOKEN_EXPIRED(20004, "Token 已过期"),
    TOKEN_INVALID(20005, "Token 无效"),
    USER_NOT_FOUND(20006, "用户未找到"),
    ENCRYPTED_DATA_EMPTY(20007, "加密数据不能为空"),
    SESSION_KEY_EMPTY(20008, "会话密钥不能为空"),
    IV_EMPTY(20009, "初始化向量不能为空"),
    PHONE_CODE_EMPTY(20010, "手机号凭证不能为空"),
    PHONE_DECRYPT_FAILED(20011, "手机号解密失败"),

    // ========== 支付错误 (30000-39999) ==========
    PAY_INIT_FAILED(30001, "支付初始化失败"),
    PAY_SIGN_FAILED(30002, "支付签名失败"),
    PAY_API_ERROR(30003, "微信支付 API 调用失败"),
    PAY_ORDER_NOT_FOUND(30004, "支付订单未找到"),
    PAY_ORDER_ALREADY_PAID(30005, "订单已支付"),
    PAY_ORDER_CLOSED(30006, "订单已关闭"),
    PAY_REFUND_FAILED(30007, "退款失败"),
    PAY_REFUND_NOT_ALLOWED(30008, "不允许退款"),
    PAY_CERT_ERROR(30009, "支付证书错误"),
    PAY_PARSE_ERROR(30010, "支付响应解析失败"),
    PAY_SHARING_FAILED(30011, "分账失败"),
    PAY_SHARING_RETURN_FAILED(30012, "分账回款失败"),
    PAY_TRANSFER_FAILED(30013, "商家转账失败"),
    PAY_RED_PACKET_FAILED(30014, "红包发放失败"),

    // ========== 消息推送错误 (40000-49999) ==========
    MESSAGE_SEND_FAILED(40001, "消息发送失败"),
    MESSAGE_TEMPLATE_NOT_FOUND(40002, "消息模板未找到"),
    MESSAGE_PARAM_ERROR(40003, "消息参数错误"),

    // ========== IoT 设备错误 (50000-59999) ==========
    DEVICE_NOT_FOUND(50001, "设备未找到"),
    DEVICE_OFFLINE(50002, "设备离线"),
    DEVICE_AUTH_FAILED(50003, "设备认证失败"),
    DEVICE_REGISTER_FAILED(50004, "设备注册失败"),
    DEVICE_UNBIND_FAILED(50005, "设备解绑失败"),
    SENSOR_DATA_ERROR(50006, "传感器数据异常"),

    // ========== Redis 错误 (60000-69999) ==========
    REDIS_CONNECT_FAILED(60001, "Redis 连接失败"),
    REDIS_OPERATE_FAILED(60002, "Redis 操作失败");

    private final int code;
    private final String message;

    WechatExceptionCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据错误码获取枚举
     *
     * @param code 错误码
     * @return 对应的枚举，如果找不到返回 UNKNOWN_ERROR
     */
    public static WechatExceptionCodes fromCode(int code) {
        for (WechatExceptionCodes errorCode : WechatExceptionCodes.values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return UNKNOWN_ERROR;
    }
}
