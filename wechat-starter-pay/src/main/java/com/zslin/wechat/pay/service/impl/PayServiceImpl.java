package com.zslin.wechat.pay.service.impl;

import com.zslin.wechat.core.exception.WechatException;
import com.zslin.wechat.core.exception.WechatExceptionCodes;
import com.zslin.wechat.pay.dto.request.PayRequest;
import com.zslin.wechat.pay.dto.request.RefundRequest;
import com.zslin.wechat.pay.dto.response.PayQueryResult;
import com.zslin.wechat.pay.dto.response.PayResult;
import com.zslin.wechat.pay.dto.response.RefundResult;
import com.zslin.wechat.pay.service.PayService;
import com.zslin.wechat.pay.service.WechatPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 微信支付服务实现
 * <p>
 * 实现支付下单、查询、退款等核心功能
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Service
public class PayServiceImpl implements PayService {

    private static final Logger log = LoggerFactory.getLogger(PayServiceImpl.class);
    
    @Autowired
    private WechatPayService wechatPayService;

    @Override
    public PayResult jsapiPay(PayRequest request) {
        log.info("开始 JSAPI 支付下单：outTradeNo={}", request.getOutTradeNo());
        
        try {
            // 调用微信支付服务完成下单
            return wechatPayService.unifiedOrder(request);
            
        } catch (WechatException e) {
            log.error("JSAPI 支付下单失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("JSAPI 支付下单异常", e);
            throw new WechatException(WechatExceptionCodes.PAY_INIT_FAILED, 
                    "支付下单异常：" + e.getMessage(), e);
        }
    }

    @Override
    public PayQueryResult queryOrder(String transactionId, String outTradeNo) {
        log.info("开始查询订单：transactionId={}, outTradeNo={}", transactionId, outTradeNo);
        
        if (transactionId == null || transactionId.trim().isEmpty()) {
            if (outTradeNo == null || outTradeNo.trim().isEmpty()) {
                throw new WechatException(WechatExceptionCodes.PARAM_ERROR,
                        new RuntimeException("transactionId 和 outTradeNo 不能同时为空"));
            }
            // 根据商户订单号查询
            return wechatPayService.queryOrderByOutTradeNo(outTradeNo);
        } else {
            // 根据微信订单号查询
            return wechatPayService.queryOrderByTransactionId(transactionId);
        }
    }

    @Override
    public RefundResult refund(String outTradeNo, String outRefundNo, int refundAmount, int totalAmount) {
        log.info("开始申请退款：outTradeNo={}, outRefundNo={}, refundAmount={}", 
                outTradeNo, outRefundNo, refundAmount);
        
        if (outTradeNo == null || outTradeNo.trim().isEmpty()) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR,
                    new RuntimeException("商户订单号不能为空"));
        }
        
        if (outRefundNo == null || outRefundNo.trim().isEmpty()) {
            throw new WechatException(WechatExceptionCodes.PARAM_ERROR,
                    new RuntimeException("商户退款单号不能为空"));
        }
        
        if (refundAmount <= 0 || refundAmount > totalAmount) {
            throw new WechatException(WechatExceptionCodes.PAY_REFUND_NOT_ALLOWED,
                    new RuntimeException("退款金额必须大于 0 且不超过原订单金额"));
        }
        
        try {
            // 构建退款请求
            RefundRequest refundRequest = new RefundRequest();
            refundRequest.setOutTradeNo(outTradeNo);
            refundRequest.setOutRefundNo(outRefundNo);
            refundRequest.setRefund(refundAmount);
            refundRequest.setTotal(totalAmount);
            refundRequest.setReason("用户申请退款");
            
            // 调用退款服务
            boolean result = wechatPayService.refund(refundRequest);
            
            // 构建退款结果
            RefundResult refundResult = new RefundResult();
            refundResult.setOutTradeNo(outTradeNo);
            refundResult.setOutRefundNo(outRefundNo);
            refundResult.setRefund(refundAmount);
            refundResult.setTotal(totalAmount);
            refundResult.setSuccess(result);
            refundResult.setMessage(result ? "退款成功" : "退款失败");
            
            return refundResult;
            
        } catch (WechatException e) {
            log.error("退款失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("退款异常", e);
            throw new WechatException(WechatExceptionCodes.PAY_REFUND_FAILED, 
                    "退款异常：" + e.getMessage(), e);
        }
    }
}
