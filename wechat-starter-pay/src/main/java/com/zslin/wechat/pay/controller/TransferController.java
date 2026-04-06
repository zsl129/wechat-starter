package com.zslin.wechat.pay.controller;

import com.zslin.wechat.pay.dto.request.RedPacketRequest;
import com.zslin.wechat.pay.dto.request.TransferRequest;
import com.zslin.wechat.pay.dto.response.RedPacketResult;
import com.zslin.wechat.pay.dto.response.TransferResult;
import com.zslin.wechat.pay.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商家转账与红包控制器
 *
 * @author 子墨
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/pay/transfer")
public class TransferController {

    @Autowired
    private TransferService transferService;

    /**
     * 商家转账
     *
     * @param request 转账请求
     * @return 转账结果
     */
    @PostMapping("/transfer")
    public TransferResult transfer(@RequestBody TransferRequest request) {
        return transferService.transfer(request);
    }

    /**
     * 查询转账结果
     *
     * @param outRedPackNo 商户订单号
     * @return 转账结果
     */
    @GetMapping("/transfer/query/{outRedPackNo}")
    public TransferResult queryTransfer(@PathVariable String outRedPackNo) {
        return transferService.query(outRedPackNo);
    }

    /**
     * 发送红包
     *
     * @param request 红包请求
     * @return 红包结果
     */
    @PostMapping("/redpacket")
    public RedPacketResult sendRedPacket(@RequestBody RedPacketRequest request) {
        return transferService.sendRedPacket(request);
    }

    /**
     * 查询红包结果
     *
     * @param outDetailNo 订单号
     * @return 红包结果
     */
    @GetMapping("/redpacket/query/{outDetailNo}")
    public RedPacketResult queryRedPacket(@PathVariable String outDetailNo) {
        return transferService.queryRedPacket(outDetailNo);
    }
}
