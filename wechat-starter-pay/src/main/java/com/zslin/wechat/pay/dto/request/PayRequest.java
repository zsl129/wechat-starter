package com.zslin.wechat.pay.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付请求 DTO
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayRequest {

    /**
     * 商户订单号（必填）
     */
    private String outTradeNo;

    /**
     * 订单标题（必填）
     */
    private String description;

    /**
     * 订单金额，单位：分（必填）
     */
    private int amount;

    /**
     * 用户 OpenID（JSAPI 支付必填）
     */
    private String openid;

    /**
     * 回调通知地址（可选，默认使用配置值）
     */
    private String notifyUrl;

    /**
     * 附加数据（可选）
     */
    private String attach;
    
    /**
     * 小程序 AppID
     */
    private String appId;
    
    /**
     * 用户状态
     */
    private String state;
    
    /**
     * 金额对象（v3 API 使用）
     */
    private Amount amountObj;
    
    /**
     * 金额对象类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Amount {
        /**
         * 订单总金额，单位：分
         */
        private int total;
        
        /**
         * 货币种类
         */
        private String currency;
    }
}
