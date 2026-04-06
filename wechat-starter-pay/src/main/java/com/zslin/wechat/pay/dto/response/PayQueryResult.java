package com.zslin.wechat.pay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单查询结果 DTO
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayQueryResult {

    /**
     * 微信支付订单号
     */
    private String transactionId;

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 商户退款单号
     */
    private String outRefundNo;

    /**
     * 交易类型：JSAPI, NATIVE, APP
     */
    private String tradeType;

    /**
     * 交易状态：SUCCESS, NOTPAY, CLOSED
     */
    private String tradeState;
    
    /**
     * 交易状态描述
     */
    private String tradeStateDesc;

    /**
     * 订单金额，单位：分
     */
    private int cashFee;
    
    /**
     * 原订单总金额
     */
    private Integer total;
    
    /**
     * 退款金额
     */
    private Integer refund;
    
    /**
     * 退款状态
     */
    private String refundStatus;

    /**
     * 支付完成时间
     */
    private String successTime;

    /**
     * 附加数据
     */
    private String attach;
    
    /**
     * 账单下载链接
     */
    private String billDownloadUrl;
    
    /**
     * 订单总金额 (别名)
     */
    private Integer totalAmount;
}
