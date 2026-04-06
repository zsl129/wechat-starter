package com.zslin.wechat.pay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分账结果 DTO
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfitSharingResult {

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
     * 分账结果详情列表
     */
    private List<ReceptorDetail> receptorDetailList;

    /**
     * 分账接收方详细信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceptorDetail {
        /**
         * 分账接收方的微信支付商户号
         */
        private String receptorId;

        /**
         * 分账接收方类型
         */
        private String receptorType;

        /**
         * 本次分账金额，单位为分
         */
        private Integer amount;

        /**
         * 备注
         */
        private String description;
    }
}
