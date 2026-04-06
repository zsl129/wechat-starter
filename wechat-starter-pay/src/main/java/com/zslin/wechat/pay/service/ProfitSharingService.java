package com.zslin.wechat.pay.service;

import com.zslin.wechat.pay.dto.request.ProfitSharingRequest;
import com.zslin.wechat.pay.dto.request.ProfitSharingReturnRequest;
import com.zslin.wechat.pay.dto.response.ProfitSharingResult;
import com.zslin.wechat.pay.dto.response.ProfitSharingReturnResult;

/**
 * 分账服务接口
 *
 * @author 子墨
 * @since 1.0.0
 */
public interface ProfitSharingService {

    /**
     * 分账下单
     *
     * @param request 分账请求
     * @return 分账结果
     */
    ProfitSharingResult share(ProfitSharingRequest request);

    /**
     * 查询分账结果
     *
     * @param outOrderNo 商户订单号
     * @return 分账结果
     */
    ProfitSharingResult query(String outOrderNo);

    /**
     * 申请分账回款
     *
     * @param request 回款请求
     * @return 回款结果
     */
    ProfitSharingReturnResult returnFund(ProfitSharingReturnRequest request);

    /**
     * 查询分账回款结果
     *
     * @param outRequestNo 分账请求单号
     * @return 回款结果
     */
    ProfitSharingReturnResult queryReturn(String outRequestNo);
}
