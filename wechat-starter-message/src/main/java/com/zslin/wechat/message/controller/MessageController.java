package com.zslin.wechat.message.controller;

import com.zslin.wechat.message.dto.request.MessageSendRequest;
import com.zslin.wechat.message.dto.response.MessageSendResult;
import com.zslin.wechat.message.service.WechatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消息推送控制器
 * <p>
 * 提供消息发送的 HTTP 接口
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private WechatMessageService messageService;

    /**
     * 发送订阅消息
     *
     * @param request 消息请求
     * @return 发送结果
     */
    @PostMapping("/send")
    public MessageSendResult sendSubscribeMessage(@RequestBody MessageSendRequest request) {
        return messageService.sendSubscribeMessage(request);
    }

    /**
     * 批量发送消息
     *
     * @param toUserIds  用户 ID 列表
     * @param templateId 模板 ID
     * @param data       消息数据
     * @param page       页面路径
     * @return 发送结果列表
     */
    @PostMapping("/batch-send")
    public List<MessageSendResult> batchSend(
            @RequestParam List<String> toUserIds,
            @RequestParam String templateId,
            @RequestBody Map<String, Object> data,
            @RequestParam(required = false) String page) {
        return messageService.batchSend(toUserIds, templateId, data, page);
    }

    /**
     * 发送订单通知
     *
     * @param toUser     用户 OpenID
     * @param orderNo    订单编号
     * @param orderStatus 订单状态
     * @param orderTime  下单时间
     * @param orderAmount 订单金额
     * @param remark     备注
     * @return 发送结果
     */
    @PostMapping("/order-notify")
    public MessageSendResult sendOrderNotify(
            @RequestParam String toUser,
            @RequestParam String orderNo,
            @RequestParam String orderStatus,
            @RequestParam String orderTime,
            @RequestParam String orderAmount,
            @RequestParam(required = false) String remark) {
        
        MessageSendRequest request = new MessageSendRequest();
        request.setToUser(toUser);
        request.setTemplateId("ORDER_NOTIFY");
        request.setPage("pages/order/detail?orderNo=" + orderNo);
        
        MessageSendRequest.MessageData orderNoData = new MessageSendRequest.MessageData();
        orderNoData.setValue(orderNo);
        orderNoData.setColor("#173177");
        
        MessageSendRequest.MessageData orderStatusData = new MessageSendRequest.MessageData();
        orderStatusData.setValue(orderStatus);
        orderStatusData.setColor("#173177");
        
        MessageSendRequest.MessageData orderTimeData = new MessageSendRequest.MessageData();
        orderTimeData.setValue(orderTime);
        orderTimeData.setColor("#173177");
        
        MessageSendRequest.MessageData orderAmountData = new MessageSendRequest.MessageData();
        orderAmountData.setValue("¥" + orderAmount);
        orderAmountData.setColor("#173177");
        
        Map<String, MessageSendRequest.MessageData> data = new java.util.HashMap<>();
        data.put("thing1", orderNoData);
        data.put("thing2", orderStatusData);
        data.put("time1", orderTimeData);
        data.put("price1", orderAmountData);
        
        if (remark != null && !remark.isEmpty()) {
            MessageSendRequest.MessageData remarkData = new MessageSendRequest.MessageData();
            remarkData.setValue(remark);
            remarkData.setColor("#173177");
            data.put("thing3", remarkData);
        }
        
        request.setData(data);
        return messageService.sendSubscribeMessage(request);
    }

    /**
     * 发送支付成功通知
     *
     * @param toUser     用户 OpenID
     * @param goodsName  商品名称
     * @param payAmount  支付金额
     * @param payTime    支付时间
     * @param payMethod  支付方式
     * @return 发送结果
     */
    @PostMapping("/payment-success")
    public MessageSendResult sendPaymentSuccess(
            @RequestParam String toUser,
            @RequestParam String goodsName,
            @RequestParam String payAmount,
            @RequestParam String payTime,
            @RequestParam(required = false) String payMethod) {
        
        MessageSendRequest request = new MessageSendRequest();
        request.setToUser(toUser);
        request.setTemplateId("PAYMENT_SUCCESS");
        request.setPage("pages/order/success");
        
        MessageSendRequest.MessageData goodsNameData = new MessageSendRequest.MessageData();
        goodsNameData.setValue(goodsName);
        goodsNameData.setColor("#173177");
        
        MessageSendRequest.MessageData payAmountData = new MessageSendRequest.MessageData();
        payAmountData.setValue("¥" + payAmount);
        payAmountData.setColor("#173177");
        
        MessageSendRequest.MessageData payTimeData = new MessageSendRequest.MessageData();
        payTimeData.setValue(payTime);
        payTimeData.setColor("#173177");
        
        MessageSendRequest.MessageData payMethodData = new MessageSendRequest.MessageData();
        payMethodData.setValue(payMethod != null ? payMethod : "微信支付");
        payMethodData.setColor("#173177");
        
        Map<String, MessageSendRequest.MessageData> data = new java.util.HashMap<>();
        data.put("thing1", goodsNameData);
        data.put("price1", payAmountData);
        data.put("time1", payTimeData);
        data.put("thing2", payMethodData);
        
        request.setData(data);
        return messageService.sendSubscribeMessage(request);
    }
}
