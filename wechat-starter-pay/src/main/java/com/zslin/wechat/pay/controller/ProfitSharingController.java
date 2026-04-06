package com.zslin.wechat.pay.controller;

import com.zslin.wechat.pay.dto.request.ProfitSharingRequest;
import com.zslin.wechat.pay.dto.request.ProfitSharingReturnRequest;
import com.zslin.wechat.pay.dto.response.ProfitSharingResult;
import com.zslin.wechat.pay.dto.response.ProfitSharingReturnResult;
import com.zslin.wechat.pay.service.ProfitSharingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 分账控制器
 *
 * @author 子墨
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/pay/profit-sharing")
public class ProfitSharingController {

    @Autowired
    private ProfitSharingService profitSharingService;

    /**
     * 分账下单
     *
     * @param request 分账请求
     * @return 分账结果
     */
    @PostMapping("/share")
    public ProfitSharingResult share(@RequestBody ProfitSharingRequest request) {
        return profitSharingService.share(request);
    }

    /**
     * 查询分账结果
     *
     * @param outOrderNo 商户订单号
     * @return 分账结果
     */
    @GetMapping("/query/{outOrderNo}")
    public ProfitSharingResult query(@PathVariable String outOrderNo) {
        return profitSharingService.query(outOrderNo);
    }

    /**
     * 申请分账回款
     *
     * @param request 回款请求
     * @return 回款结果
     */
    @PostMapping("/return")
    public ProfitSharingReturnResult returnFund(@RequestBody ProfitSharingReturnRequest request) {
        return profitSharingService.returnFund(request);
    }

    /**
     * 查询分账回款结果
     *
     * @param outRequestNo 分账请求单号
     * @return 回款结果
     */
    @GetMapping("/return/query/{outRequestNo}")
    public ProfitSharingReturnResult queryReturn(@PathVariable String outRequestNo) {
        return profitSharingService.queryReturn(outRequestNo);
    }
}
