package com.zslin.wechat.pay.service;

import com.zslin.wechat.pay.dto.request.PayRequest;
import com.zslin.wechat.pay.dto.request.RefundRequest;
import com.zslin.wechat.pay.dto.response.PayQueryResult;
import com.zslin.wechat.pay.dto.response.PayResult;

/**
 * 微信支付服务接口
 * <p>
 * 提供微信支付相关的核心功能
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
public interface WechatPayService {

    /**
     * 统一下单
     * <p>
     * 创建支付订单，返回支付参数
     * </p>
     *
     * @param request 支付请求
     * @return 支付结果（包含支付参数）
     */
    PayResult unifiedOrder(PayRequest request);

    /**
     * 根据商户订单号查询订单
     * <p>
     * 根据商户订单号查询支付状态
     * </p>
     *
     * @param outTradeNo 商户订单号
     * @return 查询结果
     */
    PayQueryResult queryOrderByOutTradeNo(String outTradeNo);

    /**
     * 根据微信订单号查询订单
     * <p>
     * 根据微信支付订单号查询支付状态
     * </p>
     *
     * @param transactionId 微信订单号
     * @return 查询结果
     */
    PayQueryResult queryOrderByTransactionId(String transactionId);

    /**
     * 关闭订单
     * <p>
     * 关闭未支付的订单
     * </p>
     *
     * @param outTradeNo 商户订单号
     * @return true-关闭成功，false-关闭失败
     */
    boolean closeOrder(String outTradeNo);

    /**
     * 申请退款
     * <p>
     * 提交退款申请
     * </p>
     *
     * @param request 退款请求
     * @return true-退款成功，false-退款失败
     */
    boolean refund(RefundRequest request);

    /**
     * 查询退款
     * <p>
     * 查询退款结果
     * </p>
     *
     * @param outTradeNo 商户订单号
     * @param outRefundNo 商户退款单号
     * @return 退款结果
     */
    PayQueryResult queryRefund(String outTradeNo, String outRefundNo);
}
