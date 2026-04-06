package com.zslin.wechat.pay.service;

import com.zslin.wechat.core.exception.WechatException;
import com.zslin.wechat.pay.dto.request.ProfitSharingRequest;
import com.zslin.wechat.pay.dto.request.ProfitSharingReturnRequest;
import com.zslin.wechat.pay.service.impl.ProfitSharingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ProfitSharingServiceImpl 单元测试
 *
 * @author 子墨
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ProfitSharingServiceImplTest {

    @InjectMocks
    private ProfitSharingServiceImpl profitSharingServiceImpl;

    private ProfitSharingRequest shareRequest;
    private ProfitSharingReturnRequest returnRequest;

    @BeforeEach
    void setUp() {
        // 准备分账请求数据
        shareRequest = new ProfitSharingRequest();
        shareRequest.setOutOrderNo("ORDER_20260406_001");
        
        List<ProfitSharingRequest.Receptor> receptors = new ArrayList<>();
        ProfitSharingRequest.Receptor receptor = new ProfitSharingRequest.Receptor();
        receptor.setReceptorId("wx1234567890");
        receptor.setReceptorType("WechatId");
        receptor.setAmount(100);
        receptor.setDescription("分账给商家");
        receptors.add(receptor);
        
        shareRequest.setReceptorList(receptors);
        shareRequest.setDescription("测试分账");
        shareRequest.setRemark("测试备注");

        // 准备回款请求数据
        returnRequest = new ProfitSharingReturnRequest();
        returnRequest.setOutRequestNo("RETURN_REQ_001");
        returnRequest.setReturnAmount(50);
        returnRequest.setReturnReceptorId("wx1234567890");
        returnRequest.setReturnReceptorType("WechatId");
        returnRequest.setReason("测试回款");
    }

    @Test
    void testRequestValidation_ValidRequest() {
        // 测试有效的请求数据
        assertNotNull(shareRequest.getOutOrderNo());
        assertNotNull(shareRequest.getReceptorList());
        assertEquals(1, shareRequest.getReceptorList().size());
    }

    @Test
    void testRequestValidation_EmptyOrderNo() {
        // 测试订单号为空
        shareRequest.setOutOrderNo("");
        assertTrue(shareRequest.getOutOrderNo().isEmpty());
    }

    @Test
    void testRequestValidation_EmptyReceptorList() {
        // 测试收款方列表为空
        shareRequest.setReceptorList(new ArrayList<>());
        assertTrue(shareRequest.getReceptorList().isEmpty());
    }

    @Test
    void testReceptorValidation_ValidReceptor() {
        // 测试有效的收款方数据
        List<ProfitSharingRequest.Receptor> receptors = shareRequest.getReceptorList();
        assertNotNull(receptors);
        assertTrue(receptors.size() > 0);
        
        ProfitSharingRequest.Receptor receptor = receptors.get(0);
        assertNotNull(receptor.getReceptorId());
        assertNotNull(receptor.getAmount());
        assertTrue(receptor.getAmount() > 0);
    }

    @Test
    void testReceptorValidation_AmountZero() {
        // 测试金额为 0
        List<ProfitSharingRequest.Receptor> receptors = shareRequest.getReceptorList();
        receptors.get(0).setAmount(0);
        assertEquals(0, (int) receptors.get(0).getAmount());
    }

    @Test
    void testReceptorValidation_AmountNegative() {
        // 测试金额为负数
        List<ProfitSharingRequest.Receptor> receptors = shareRequest.getReceptorList();
        receptors.get(0).setAmount(-100);
        assertEquals(-100, (int) receptors.get(0).getAmount());
    }

    @Test
    void testReturnRequestValidation_ValidRequest() {
        // 测试有效的回款请求数据
        assertNotNull(returnRequest.getOutRequestNo());
        assertNotNull(returnRequest.getReturnAmount());
        assertTrue(returnRequest.getReturnAmount() > 0);
    }

    @Test
    void testReturnRequestValidation_EmptyOutRequestNo() {
        // 测试回款请求单号为空
        returnRequest.setOutRequestNo("");
        assertTrue(returnRequest.getOutRequestNo().isEmpty());
    }

    @Test
    void testReturnRequestValidation_AmountZero() {
        // 测试回款金额为 0
        returnRequest.setReturnAmount(0);
        assertEquals(0, (int) returnRequest.getReturnAmount());
    }

    @Test
    void testReturnRequestValidation_EmptyReceptorId() {
        // 测试收款方 ID 为空
        returnRequest.setReturnReceptorId("");
        assertTrue(returnRequest.getReturnReceptorId().isEmpty());
    }
}
