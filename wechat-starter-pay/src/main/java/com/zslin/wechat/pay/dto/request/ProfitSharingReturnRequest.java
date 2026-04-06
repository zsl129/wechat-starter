package com.zslin.wechat.pay.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分账回款请求 DTO
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfitSharingReturnRequest {

    /**
     * 分账请求单号
     */
    private String outRequestNo;

    /**
     * 退回金额，单位为分
     */
    private Integer returnAmount;

    /**
     * 分账接收方的微信支付商户号或 openid
     */
    private String returnReceptorId;

    /**
     * 分账接收方类型：MerchantId-商户号、WechatId- openid
     */
    private String returnReceptorType = "WechatId";

    /**
     * 申请原因
     */
    private String reason;

    /**
     * 备注
     */
    private String remark;
}
