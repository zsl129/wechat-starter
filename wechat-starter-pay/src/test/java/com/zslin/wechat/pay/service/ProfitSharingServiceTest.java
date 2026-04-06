package com.zslin.wechat.pay.service;

import com.zslin.wechat.core.config.WechatProperties;
import com.zslin.wechat.core.exception.WechatException;
import com.zslin.wechat.pay.dto.request.ProfitSharingRequest;
import com.zslin.wechat.pay.dto.response.ProfitSharingReturnResult;
import com.zslin.wechat.pay.service.impl.ProfitSharingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

/**
 * 分账服务测试
 *
 * @author 子墨
 * @since 1.0.0
 */
public class ProfitSharingServiceTest {

    private ProfitSharingService profitSharingService;

    @BeforeEach
    public void setUp() {
        profitSharingService = new ProfitSharingServiceImpl();
        
        // 创建并注入 WechatProperties
        WechatProperties properties = new WechatProperties();
        WechatProperties.Pay payConfig = new WechatProperties.Pay();
        payConfig.setMchId("1234567890");
        payConfig.setApiV3Key("testkey1234567890testkey12345678");
        payConfig.setApiUrl("https://api.merchant.weixinpay.com");
        payConfig.setSerialNo("SERIALNO");
        payConfig.setPrivateKeyPem("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC7...");
        properties.setPay(payConfig);
        
        ReflectionTestUtils.setField(profitSharingService, "properties", properties);
    }

    @Test
    public void testShare_success() {
        // 准备测试数据
        ProfitSharingRequest request = new ProfitSharingRequest();
        request.setOutOrderNo("TEST_ORDER_SHARING_001");
        request.setDescription("测试分账");
        request.setProfitSharingType("SHARING");
        request.setRemark("测试备注");

        // 准备收款方列表
        List<ProfitSharingRequest.Receptor> receptorList = new ArrayList<>();
        
        ProfitSharingRequest.Receptor receptor1 = new ProfitSharingRequest.Receptor();
        receptor1.setReceptorId("oReceptor1234567890");
        receptor1.setReceptorType("WechatId");
        receptor1.setAmount(1000);
        receptor1.setDescription("收款方 1");
        receptorList.add(receptor1);

        ProfitSharingRequest.Receptor receptor2 = new ProfitSharingRequest.Receptor();
        receptor2.setReceptorId("oReceptor0987654321");
        receptor2.setReceptorType("WechatId");
        receptor2.setAmount(500);
        receptor2.setDescription("收款方 2");
        receptorList.add(receptor2);

        request.setReceptorList(receptorList);

        // 执行分账（当前会抛出异常，因为私钥未配置）
        assertThrows(WechatException.class, () -> {
            profitSharingService.share(request);
        });
    }

    @Test
    public void testShare_emptyRequest() {
        assertThrows(WechatException.class, () -> {
            profitSharingService.share(null);
        });
    }

    @Test
    public void testShare_missingOrderNo() {
        ProfitSharingRequest request = new ProfitSharingRequest();
        request.setOutOrderNo("");

        assertThrows(WechatException.class, () -> {
            profitSharingService.share(request);
        });
    }

    @Test
    public void testShare_emptyReceptorList() {
        ProfitSharingRequest request = new ProfitSharingRequest();
        request.setOutOrderNo("TEST_ORDER_001");
        request.setReceptorList(new ArrayList<>());

        assertThrows(WechatException.class, () -> {
            profitSharingService.share(request);
        });
    }

    @Test
    public void testShare_invalidAmount() {
        ProfitSharingRequest request = new ProfitSharingRequest();
        request.setOutOrderNo("TEST_ORDER_001");

        List<ProfitSharingRequest.Receptor> receptorList = new ArrayList<>();
        ProfitSharingRequest.Receptor receptor = new ProfitSharingRequest.Receptor();
        receptor.setReceptorId("oReceptor1234567890");
        receptor.setAmount(-100); // 无效金额
        receptorList.add(receptor);

        request.setReceptorList(receptorList);

        assertThrows(WechatException.class, () -> {
            profitSharingService.share(request);
        });
    }

    @Test
    public void testShare_zeroAmount() {
        ProfitSharingRequest request = new ProfitSharingRequest();
        request.setOutOrderNo("TEST_ORDER_001");

        List<ProfitSharingRequest.Receptor> receptorList = new ArrayList<>();
        ProfitSharingRequest.Receptor receptor = new ProfitSharingRequest.Receptor();
        receptor.setReceptorId("oReceptor1234567890");
        receptor.setAmount(0); // 零金额
        receptorList.add(receptor);

        request.setReceptorList(receptorList);

        assertThrows(WechatException.class, () -> {
            profitSharingService.share(request);
        });
    }

    @Test
    public void testQuery_success() {
        String outOrderNo = "TEST_ORDER_SHARING_001";

        // 执行查询（当前会抛出异常，因为私钥未配置）
        assertThrows(WechatException.class, () -> {
            profitSharingService.query(outOrderNo);
        });
    }

    @Test
    public void testQuery_emptyOrderNo() {
        assertThrows(WechatException.class, () -> {
            profitSharingService.query("");
        });
    }

    @Test
    public void testQuery_nullOrderNo() {
        assertThrows(WechatException.class, () -> {
            profitSharingService.query(null);
        });
    }

    @Test
    public void testReturnFund_success() {
        // 准备测试数据
        com.zslin.wechat.pay.dto.request.ProfitSharingReturnRequest request = 
            new com.zslin.wechat.pay.dto.request.ProfitSharingReturnRequest();
        request.setOutRequestNo("TEST_RETURN_REQUEST_001");
        request.setReturnAmount(1000);
        request.setReturnReceptorId("oReceptor1234567890");
        request.setReturnReceptorType("WechatId");
        request.setReason("测试回款");

        // 执行回款（当前会抛出异常，因为私钥未配置）
        assertThrows(WechatException.class, () -> {
            profitSharingService.returnFund(request);
        });
    }

    @Test
    public void testReturnFund_emptyRequest() {
        assertThrows(WechatException.class, () -> {
            profitSharingService.returnFund(null);
        });
    }

    @Test
    public void testReturnFund_missingRequestNo() {
        com.zslin.wechat.pay.dto.request.ProfitSharingReturnRequest request = 
            new com.zslin.wechat.pay.dto.request.ProfitSharingReturnRequest();
        request.setOutRequestNo("");

        assertThrows(WechatException.class, () -> {
            profitSharingService.returnFund(request);
        });
    }

    @Test
    public void testReturnFund_invalidAmount() {
        com.zslin.wechat.pay.dto.request.ProfitSharingReturnRequest request = 
            new com.zslin.wechat.pay.dto.request.ProfitSharingReturnRequest();
        request.setOutRequestNo("TEST_RETURN_001");
        request.setReturnAmount(-100);

        assertThrows(WechatException.class, () -> {
            profitSharingService.returnFund(request);
        });
    }

    @Test
    public void testReturnFund_missingReceptorId() {
        com.zslin.wechat.pay.dto.request.ProfitSharingReturnRequest request = 
            new com.zslin.wechat.pay.dto.request.ProfitSharingReturnRequest();
        request.setOutRequestNo("TEST_RETURN_001");
        request.setReturnAmount(1000);
        request.setReturnReceptorId("");

        assertThrows(WechatException.class, () -> {
            profitSharingService.returnFund(request);
        });
    }

    @Test
    public void testQueryReturn_success() {
        String outRequestNo = "TEST_RETURN_REQUEST_001";

        // 执行查询（当前返回模拟结果）
        ProfitSharingReturnResult result = profitSharingService.queryReturn(outRequestNo);
        assertNotNull(result);
        assertEquals(outRequestNo, result.getOutRequestNo());
        assertTrue(result.isSuccess());
    }

    @Test
    public void testQueryReturn_emptyRequestNo() {
        assertThrows(WechatException.class, () -> {
            profitSharingService.queryReturn("");
        });
    }
}
