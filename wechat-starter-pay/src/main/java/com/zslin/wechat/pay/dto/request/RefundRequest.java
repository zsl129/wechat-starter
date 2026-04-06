package com.zslin.wechat.pay.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 退款请求 DTO
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequest {

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 商户退款单号
     */
    private String outRefundNo;

    /**
     * 退款金额（分）
     */
    private Integer refund;

    /**
     * 原订单金额（分）
     */
    private Integer total;

    /**
     * 退款描述
     */
    private String reason;
}
