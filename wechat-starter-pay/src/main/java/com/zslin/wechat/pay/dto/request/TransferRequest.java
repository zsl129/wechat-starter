package com.zslin.wechat.pay.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商家转账请求 DTO
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    /**
     * 收款用户 openid
     */
    private String userOpenid;

    /**
     * 付款企业商户号
     */
    private String payeeMchId;

    /**
     * 商户订单号
     */
    private String outRedPackNo;

    /**
     * 转账金额，单位：分
     */
    private Integer amount;

    /**
     * 商户备注
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 单次转账金额，单位：分
     */
    private Integer singleAmount;
}
