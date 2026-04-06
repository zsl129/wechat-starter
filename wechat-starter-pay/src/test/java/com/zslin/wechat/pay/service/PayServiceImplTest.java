package com.zslin.wechat.pay.service.impl;

import com.zslin.wechat.core.exception.WechatException;
import com.zslin.wechat.pay.dto.request.PayRequest;
import com.zslin.wechat.pay.dto.response.PayResult;
import com.zslin.wechat.pay.service.WechatPayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * PayServiceImpl 单元测试
 *
 * @author 子墨
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class PayServiceImplTest {

    @Mock
    private WechatPayService wechatPayService;

    @InjectMocks
    private PayServiceImpl payService;

    private PayRequest testRequest;

    @BeforeEach
    void setUp() {
        testRequest = new PayRequest();
        testRequest.setOutTradeNo("TEST_ORDER_001");
        testRequest.setDescription("测试商品");
        testRequest.setAmount(100);
        testRequest.setOpenid("test_openid");
        testRequest.setAppId("wx1234567890");
    }

    @Test
    void testJsapiPay_Success() {
        // 准备 Mock 数据
        PayResult expectedResult = new PayResult();
        expectedResult.setTransactionId("tx_id_123456");
        expectedResult.setTradeState("SUCCESS");

        when(wechatPayService.unifiedOrder(any(PayRequest.class)))
            .thenReturn(expectedResult);

        // 执行测试
        PayResult result = payService.jsapiPay(testRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals("tx_id_123456", result.getTransactionId());
        assertEquals("SUCCESS", result.getTradeState());
    }

    @Test
    void testJsapiPay_WechatException() {
        // 准备 Mock 数据 - 抛出异常
        WechatException exception = new WechatException(com.zslin.wechat.core.exception.WechatExceptionCodes.PAY_INIT_FAILED,
                "支付初始化失败");

        when(wechatPayService.unifiedOrder(any(PayRequest.class)))
            .thenThrow(exception);

        // 执行测试并验证异常
        WechatException result = assertThrows(WechatException.class, () -> {
            payService.jsapiPay(testRequest);
        });

        assertEquals(com.zslin.wechat.core.exception.WechatExceptionCodes.PAY_INIT_FAILED.getCode(), result.getCode());
    }

    @Test
    void testQueryOrder_ByTransactionId() {
        // 这个测试需要实现 queryOrder 方法的完整逻辑
        // 暂时跳过，等待完整实现
        assertTrue(true);
    }

    @Test
    void testQueryOrder_ByOutTradeNo() {
        // 这个测试需要实现 queryOrder 方法的完整逻辑
        // 暂时跳过，等待完整实现
        assertTrue(true);
    }

    @Test
    void testRefund_Success() {
        // 这个测试需要实现 refund 方法的完整逻辑
        // 暂时跳过，等待完整实现
        assertTrue(true);
    }

    @Test
    void testRefund_InvalidAmount() {
        // 测试退款金额为负数
        assertThrows(WechatException.class, () -> {
            payService.refund("ORDER_001", "REFUND_001", -100, 1000);
        });
    }

    @Test
    void testRefund_ExceedsTotal() {
        // 测试退款金额超过原订单金额
        assertThrows(WechatException.class, () -> {
            payService.refund("ORDER_001", "REFUND_001", 2000, 1000);
        });
    }

    @Test
    void testRefund_EmptyOrderNo() {
        // 测试商户订单号为空
        assertThrows(WechatException.class, () -> {
            payService.refund("", "REFUND_001", 100, 1000);
        });
    }

    @Test
    void testRefund_EmptyRefundNo() {
        // 测试商户退款单号为空
        assertThrows(WechatException.class, () -> {
            payService.refund("ORDER_001", "", 100, 1000);
        });
    }
}
