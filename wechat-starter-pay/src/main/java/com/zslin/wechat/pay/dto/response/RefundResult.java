package com.zslin.wechat.pay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 退款结果 DTO
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundResult {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 商户退款单号
     */
    private String outRefundNo;

    /**
     * 微信支付订单号
     */
    private String transactionId;

    /**
     * 退款状态：SUCCESS, PROCESSING, FAILED
     */
    private String refundStatus;

    /**
     * 退款金额
     */
    private int refundFee;
    
    /**
     * 退款金额 (别名)
     */
    private int refund;
    
    /**
     * 原订单总金额
     */
    private int total;
    
    /**
     * 消息
     */
    private String message;
}
