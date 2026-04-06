package com.zslin.wechat.pay.service;

import com.zslin.wechat.pay.dto.request.RedPacketRequest;
import com.zslin.wechat.pay.dto.request.TransferRequest;
import com.zslin.wechat.pay.dto.response.RedPacketResult;
import com.zslin.wechat.pay.dto.response.TransferResult;

/**
 * 商家转账服务接口
 *
 * @author 子墨
 * @since 1.0.0
 */
public interface TransferService {

    /**
     * 商家转账
     *
     * @param request 转账请求
     * @return 转账结果
     */
    TransferResult transfer(TransferRequest request);

    /**
     * 查询转账结果
     *
     * @param outRedPackNo 商户订单号
     * @return 转账结果
     */
    TransferResult query(String outRedPackNo);

    /**
     * 发送红包
     *
     * @param request 红包请求
     * @return 红包结果
     */
    RedPacketResult sendRedPacket(RedPacketRequest request);

    /**
     * 查询红包结果
     *
     * @param outDetailNo 订单号
     * @return 红包结果
     */
    RedPacketResult queryRedPacket(String outDetailNo);
}
