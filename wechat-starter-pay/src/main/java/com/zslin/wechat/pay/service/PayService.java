package com.zslin.wechat.pay.service;

import com.zslin.wechat.pay.dto.request.PayRequest;
import com.zslin.wechat.pay.dto.response.PayResult;
import com.zslin.wechat.pay.dto.response.PayQueryResult;
import com.zslin.wechat.pay.dto.response.RefundResult;

/**
 * 微信支付服务接口
 * <p>
 * 提供微信支付相关功能：下单、查询、退款、分账等
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
public interface PayService {

    /**
     * 小程序 JSAPI 下单
     *
     * @param request 支付请求
     * @return 支付结果（包含前端调用的参数）
     */
    PayResult jsapiPay(PayRequest request);

    /**
     * 查询订单
     * <p>
     * 支持微信支付订单号 (transactionId) 和商户订单号 (outTradeNo) 两种方式
     * </p>
     *
     * @param transactionId 微信支付订单号（可选）
     * @param outTradeNo 商户订单号（可选）
     * @return 订单查询结果
     */
    PayQueryResult queryOrder(String transactionId, String outTradeNo);

    /**
     * 申请退款
     *
     * @param outTradeNo 商户订单号
     * @param outRefundNo 商户退款单号
     * @param refundAmount 退款金额（分）
     * @param totalAmount 原订单金额（分）
     * @return 退款结果
     */
    RefundResult refund(String outTradeNo, String outRefundNo, int refundAmount, int totalAmount);

    // TODO: 后续补充其他方法
}
