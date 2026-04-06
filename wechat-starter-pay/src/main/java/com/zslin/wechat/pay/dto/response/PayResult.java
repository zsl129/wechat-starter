package com.zslin.wechat.pay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付结果 DTO
 * <p>
 * 包含前端调用支付所需的所有参数
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayResult {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 时间戳
     */
    private String timeStamp;

    /**
     * 随机串
     */
    private String nonceStr;

    /**
     * 订单详情扩展字符串
     */
    private String packageValue;

    /**
     * 签名方式
     */
    private String signType = "RSA";

    /**
     * 签名
     */
    private String paySign;

    /**
     * 商户订单号
     */
    private String outTradeNo;
    
    /**
     * 微信支付订单号
     */
    private String transactionId;
    
    /**
     * 交易状态
     */
    private String tradeState;
    
    /**
     * 交易状态描述
     */
    private String tradeStateDesc;
    
    /**
     * 支付类型
     */
    private String payType;
    
    /**
     * 现金支付金额（分）
     */
    private Integer cashFee;
    
    /**
     * 订单过期时间
     */
    private String timeExpire;
}
