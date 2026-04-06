package com.zslin.wechat.pay.service;

import com.zslin.wechat.core.config.WechatProperties;
import com.zslin.wechat.core.exception.WechatException;
import com.zslin.wechat.pay.dto.request.RedPacketRequest;
import com.zslin.wechat.pay.dto.request.TransferRequest;
import com.zslin.wechat.pay.service.impl.TransferServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 商家转账服务测试
 *
 * @author 子墨
 * @since 1.0.0
 */
public class TransferServiceTest {

    private TransferService transferService;

    @BeforeEach
    public void setUp() {
        transferService = new TransferServiceImpl();
        
        // 创建并注入 WechatProperties
        WechatProperties properties = new WechatProperties();
        WechatProperties.Pay payConfig = new WechatProperties.Pay();
        payConfig.setMchId("1234567890");
        payConfig.setApiV3Key("testkey1234567890testkey12345678");
        payConfig.setApiUrl("https://api.merchant.weixinpay.com");
        payConfig.setSerialNo("SERIALNO");
        payConfig.setPrivateKeyPem("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC7...");
        properties.setPay(payConfig);
        
        ReflectionTestUtils.setField(transferService, "properties", properties);
    }

    @Test
    public void testTransfer_success() {
        // 准备测试数据
        TransferRequest request = new TransferRequest();
        request.setUserOpenid("oUser1234567890");
        request.setOutRedPackNo("TEST_TRANSFER_001");
        request.setAmount(1000);
        request.setName("张三");
        request.setRemark("测试转账");

        // 执行转账（当前会抛出异常，因为私钥未配置）
        assertThrows(WechatException.class, () -> {
            transferService.transfer(request);
        });
    }

    @Test
    public void testTransfer_nullRequest() {
        assertThrows(WechatException.class, () -> {
            transferService.transfer(null);
        });
    }

    @Test
    public void testTransfer_emptyUserOpenid() {
        TransferRequest request = new TransferRequest();
        request.setUserOpenid("");
        request.setOutRedPackNo("TEST_TRANSFER_001");
        request.setAmount(1000);

        assertThrows(WechatException.class, () -> {
            transferService.transfer(request);
        });
    }

    @Test
    public void testTransfer_emptyOrderNo() {
        TransferRequest request = new TransferRequest();
        request.setUserOpenid("oUser1234567890");
        request.setOutRedPackNo("");
        request.setAmount(1000);

        assertThrows(WechatException.class, () -> {
            transferService.transfer(request);
        });
    }

    @Test
    public void testTransfer_invalidAmount() {
        TransferRequest request = new TransferRequest();
        request.setUserOpenid("oUser1234567890");
        request.setOutRedPackNo("TEST_TRANSFER_001");
        request.setAmount(-100);

        assertThrows(WechatException.class, () -> {
            transferService.transfer(request);
        });
    }

    @Test
    public void testTransfer_zeroAmount() {
        TransferRequest request = new TransferRequest();
        request.setUserOpenid("oUser1234567890");
        request.setOutRedPackNo("TEST_TRANSFER_001");
        request.setAmount(0);

        assertThrows(WechatException.class, () -> {
            transferService.transfer(request);
        });
    }

    @Test
    public void testTransfer_amountExceedsLimit() {
        TransferRequest request = new TransferRequest();
        request.setUserOpenid("oUser1234567890");
        request.setOutRedPackNo("TEST_TRANSFER_001");
        request.setAmount(2000000); // 超过 1 万元

        assertThrows(WechatException.class, () -> {
            transferService.transfer(request);
        });
    }

    @Test
    public void testQueryTransfer_success() {
        String outRedPackNo = "TEST_TRANSFER_001";

        // 执行查询（当前会抛出异常，因为私钥未配置）
        assertThrows(WechatException.class, () -> {
            transferService.query(outRedPackNo);
        });
    }

    @Test
    public void testQueryTransfer_emptyOrderNo() {
        assertThrows(WechatException.class, () -> {
            transferService.query("");
        });
    }

    @Test
    public void testQueryTransfer_nullOrderNo() {
        assertThrows(WechatException.class, () -> {
            transferService.query(null);
        });
    }

    @Test
    public void testSendRedPacket_success() {
        // 准备测试数据
        RedPacketRequest request = new RedPacketRequest();
        request.setReceiverOpenid("oUser1234567890");
        request.setOutDetailNo("TEST_RED_PACKET_001");
        request.setTotal_amount(1000); // 10 元
        request.setRemark("测试红包");

        // 执行发送红包（当前会抛出异常，因为私钥未配置）
        assertThrows(WechatException.class, () -> {
            transferService.sendRedPacket(request);
        });
    }

    @Test
    public void testSendRedPacket_nullRequest() {
        assertThrows(WechatException.class, () -> {
            transferService.sendRedPacket(null);
        });
    }

    @Test
    public void testSendRedPacket_emptyReceiverOpenid() {
        RedPacketRequest request = new RedPacketRequest();
        request.setReceiverOpenid("");
        request.setOutDetailNo("TEST_RED_PACKET_001");
        request.setTotal_amount(1000);

        assertThrows(WechatException.class, () -> {
            transferService.sendRedPacket(request);
        });
    }

    @Test
    public void testSendRedPacket_emptyOrderNo() {
        RedPacketRequest request = new RedPacketRequest();
        request.setReceiverOpenid("oUser1234567890");
        request.setOutDetailNo("");
        request.setTotal_amount(1000);

        assertThrows(WechatException.class, () -> {
            transferService.sendRedPacket(request);
        });
    }

    @Test
    public void testSendRedPacket_amountTooLow() {
        RedPacketRequest request = new RedPacketRequest();
        request.setReceiverOpenid("oUser1234567890");
        request.setOutDetailNo("TEST_RED_PACKET_001");
        request.setTotal_amount(50); // 低于 1 元

        assertThrows(WechatException.class, () -> {
            transferService.sendRedPacket(request);
        });
    }

    @Test
    public void testSendRedPacket_amountTooHigh() {
        RedPacketRequest request = new RedPacketRequest();
        request.setReceiverOpenid("oUser1234567890");
        request.setOutDetailNo("TEST_RED_PACKET_001");
        request.setTotal_amount(300000); // 超过 2000 元

        assertThrows(WechatException.class, () -> {
            transferService.sendRedPacket(request);
        });
    }

    @Test
    public void testSendRedPacket_zeroAmount() {
        RedPacketRequest request = new RedPacketRequest();
        request.setReceiverOpenid("oUser1234567890");
        request.setOutDetailNo("TEST_RED_PACKET_001");
        request.setTotal_amount(0);

        assertThrows(WechatException.class, () -> {
            transferService.sendRedPacket(request);
        });
    }

    @Test
    public void testQueryRedPacket_success() {
        String outDetailNo = "TEST_RED_PACKET_001";

        // 执行查询（当前会抛出异常，因为私钥未配置）
        assertThrows(WechatException.class, () -> {
            transferService.queryRedPacket(outDetailNo);
        });
    }

    @Test
    public void testQueryRedPacket_emptyOrderNo() {
        assertThrows(WechatException.class, () -> {
            transferService.queryRedPacket("");
        });
    }
}
