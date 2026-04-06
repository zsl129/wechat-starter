package com.zslin.wechat.pay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分账回款结果 DTO
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfitSharingReturnResult {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 微信支付订单号
     */
    private String transactionId;

    /**
     * 商户订单号
     */
    private String outOrderNo;

    /**
     * 分账请求单号
     */
    private String outRequestNo;

    /**
     * 申请单号
     */
    private String applicationNo;

    /**
     * 退回金额，单位为分
     */
    private Integer returnAmount;

    /**
     * 分账接收方
     */
    private String returnReceptorId;

    /**
     * 申请原因
     */
    private String reason;

    /**
     * 申请状态：PROCESSING-申请处理中、SUCCESS-申请成功、REJECTED-申请拒绝
     */
    private String status;
}
