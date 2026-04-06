package com.zslin.wechat.pay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商家转账结果 DTO
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferResult {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 错误码
     */
    private Integer errCode;

    /**
     * 错误信息
     */
    private String errMsg;

    /**
     * 微信转账单号
     */
    private String transferId;

    /**
     * 商户订单号
     */
    private String outRedPackNo;

    /**
     * 收款用户 openid
     */
    private String userOpenid;

    /**
     * 转账金额，单位：分
     */
    private Integer amount;

    /**
     * 完成时间
     */
    private String transferTime;

    /**
     * 转账状态：SUCCESS-成功、PROCESSING-处理中、FAILED-失败
     */
    private String status;

    /**
     * 商户备注
     */
    private String remark;
}
