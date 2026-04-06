package com.zslin.wechat.pay.controller;

import com.zslin.wechat.core.constant.ErrorConstants;
import com.zslin.wechat.pay.dto.request.PayRequest;
import com.zslin.wechat.pay.dto.request.RefundRequest;
import com.zslin.wechat.pay.dto.response.PayQueryResult;
import com.zslin.wechat.pay.dto.response.PayResult;
import com.zslin.wechat.pay.service.WechatPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付控制层
 * <p>
 * 提供支付相关的 REST API 接口
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/pay")
public class PayController {

    private static final Logger log = LoggerFactory.getLogger(PayController.class);

    @Autowired
    private WechatPayService wechatPayService;

    /**
     * 创建支付订单
     *
     * @param request 支付请求
     * @return 支付结果
     */
    @PostMapping("/order")
    public Map<String, Object> createOrder(@RequestBody PayRequest request) {
        log.info("收到支付请求：outTradeNo={}", request.getOutTradeNo());

        try {
            PayResult result = wechatPayService.unifiedOrder(request);

            Map<String, Object> response = new HashMap<>();
            response.put("code", ErrorConstants.SUCCESS_CODE);
            response.put("message", ErrorConstants.SUCCESS_MSG);
            response.put("data", result);

            log.info("创建支付订单成功：outTradeNo={}", request.getOutTradeNo());
            return response;

        } catch (Exception e) {
            log.error("创建支付订单失败：outTradeNo={}", request.getOutTradeNo(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("code", ErrorConstants.ERROR_CODE);
            error.put("message", e.getMessage());
            return error;
        }
    }

    /**
     * 根据商户订单号查询订单
     *
     * @param outTradeNo 商户订单号
     * @return 查询结果
     */
    @GetMapping("/query/by-out-trade-no/{outTradeNo}")
    public Map<String, Object> queryOrderByOutTradeNo(@PathVariable String outTradeNo) {
        log.info("查询订单请求（商户订单号）：outTradeNo={}", outTradeNo);

        try {
            PayQueryResult result = wechatPayService.queryOrderByOutTradeNo(outTradeNo);

            Map<String, Object> response = new HashMap<>();
            response.put("code", ErrorConstants.SUCCESS_CODE);
            response.put("message", ErrorConstants.SUCCESS_MSG);
            response.put("data", result);

            return response;

        } catch (Exception e) {
            log.error("查询订单失败：outTradeNo={}", outTradeNo, e);
            Map<String, Object> error = new HashMap<>();
            error.put("code", ErrorConstants.ERROR_CODE);
            error.put("message", e.getMessage());
            return error;
        }
    }

    /**
     * 根据微信订单号查询订单
     *
     * @param transactionId 微信订单号
     * @return 查询结果
     */
    @GetMapping("/query/by-transaction-id/{transactionId}")
    public Map<String, Object> queryOrderByTransactionId(@PathVariable String transactionId) {
        log.info("查询订单请求（微信订单号）：transactionId={}", transactionId);

        try {
            PayQueryResult result = wechatPayService.queryOrderByTransactionId(transactionId);

            Map<String, Object> response = new HashMap<>();
            response.put("code", ErrorConstants.SUCCESS_CODE);
            response.put("message", ErrorConstants.SUCCESS_MSG);
            response.put("data", result);

            return response;

        } catch (Exception e) {
            log.error("查询订单失败：transactionId={}", transactionId, e);
            Map<String, Object> error = new HashMap<>();
            error.put("code", ErrorConstants.ERROR_CODE);
            error.put("message", e.getMessage());
            return error;
        }
    }

    /**
     * 关闭订单
     *
     * @param outTradeNo 商户订单号
     * @return 关闭结果
     */
    @PostMapping("/close/{outTradeNo}")
    public Map<String, Object> closeOrder(@PathVariable String outTradeNo) {
        log.info("关闭订单请求：outTradeNo={}", outTradeNo);

        try {
            boolean success = wechatPayService.closeOrder(outTradeNo);

            Map<String, Object> response = new HashMap<>();
            response.put("code", ErrorConstants.SUCCESS_CODE);
            response.put("message", success ? "订单已关闭" : "订单关闭失败");
            response.put("data", success);

            return response;

        } catch (Exception e) {
            log.error("关闭订单失败：outTradeNo={}", outTradeNo, e);
            Map<String, Object> error = new HashMap<>();
            error.put("code", ErrorConstants.ERROR_CODE);
            error.put("message", e.getMessage());
            return error;
        }
    }

    /**
     * 申请退款
     *
     * @param request 退款请求
     * @return 退款结果
     */
    @PostMapping("/refund")
    public Map<String, Object> refund(@RequestBody RefundRequest request) {
        log.info("申请退款请求：outTradeNo={}", request.getOutTradeNo());

        try {
            boolean success = wechatPayService.refund(request);

            Map<String, Object> response = new HashMap<>();
            response.put("code", ErrorConstants.SUCCESS_CODE);
            response.put("message", success ? "退款申请成功" : "退款申请失败");
            response.put("data", success);

            log.info("申请退款成功：outTradeNo={}", request.getOutTradeNo());
            return response;

        } catch (Exception e) {
            log.error("申请退款失败：outTradeNo={}", request.getOutTradeNo(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("code", ErrorConstants.ERROR_CODE);
            error.put("message", e.getMessage());
            return error;
        }
    }

    /**
     * 查询退款
     *
     * @param outTradeNo 商户订单号
     * @param outRefundNo 商户退款单号
     * @return 退款结果
     */
    @GetMapping("/refund/query")
    public Map<String, Object> queryRefund(
            @RequestParam String outTradeNo,
            @RequestParam String outRefundNo) {
        log.info("查询退款请求：outTradeNo={}, outRefundNo={}", outTradeNo, outRefundNo);

        try {
            PayQueryResult result = wechatPayService.queryRefund(outTradeNo, outRefundNo);

            Map<String, Object> response = new HashMap<>();
            response.put("code", ErrorConstants.SUCCESS_CODE);
            response.put("message", ErrorConstants.SUCCESS_MSG);
            response.put("data", result);

            return response;

        } catch (Exception e) {
            log.error("查询退款失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("code", ErrorConstants.ERROR_CODE);
            error.put("message", e.getMessage());
            return error;
        }
    }
}
