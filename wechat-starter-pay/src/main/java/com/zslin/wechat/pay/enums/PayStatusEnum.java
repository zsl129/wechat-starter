package com.zslin.wechat.pay.enums;

/**
 * 支付状态枚举
 *
 * @author 子墨
 * @since 1.0.0
 */
public enum PayStatusEnum {

    /**
     * 支付成功
     */
    SUCCESS("SUCCESS", "支付成功"),

    /**
     * 未支付
     */
    NOTPAY("NOTPAY", "未支付"),

    /**
     * 已关闭
     */
    CLOSED("CLOSED", "已关闭"),

    /**
     * 转入退款
     */
    REFUND("REFUND", "转入退款"),

    /**
     * 用户正在输入密码
     */
    USERPAYING("USERPAYING", "用户正在输入密码"),

    /**
     * 支付失败
     */
    PAYERROR("PAYERROR", "支付失败");

    private final String code;
    private final String desc;

    PayStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据状态码获取枚举
     *
     * @param code 状态码
     * @return 对应的枚举
     */
    public static PayStatusEnum fromCode(String code) {
        for (PayStatusEnum status : PayStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
