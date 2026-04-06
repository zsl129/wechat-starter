package com.zslin.wechat.pay.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分账请求 DTO
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfitSharingRequest {

    /**
     * 订单号（支付时的商户订单号）
     */
    private String outOrderNo;

    /**
     * 收款方列表
     */
    private List<Receptor> receptorList;

    /**
     * 资金用途
     */
    private String profitSharingType = "SHARING";

    /**
     * 分账描述
     */
    private String description;

    /**
     * 商户自定义打款备注
     */
    private String remark;

    /**
     * 分账接收方
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Receptor {
        /**
         * 分账接收方的微信支付商户号
         */
        private String receptorId;

        /**
         * 分账接收方类型：MerchantId-商户号、WechatId- openid
         */
        private String receptorType = "WechatId";

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
