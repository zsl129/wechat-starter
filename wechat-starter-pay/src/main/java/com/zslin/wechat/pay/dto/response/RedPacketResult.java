package com.zslin.wechat.pay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 红包结果 DTO
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedPacketResult {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 返回状态码
     */
    private String return_code;

    /**
     * 返回信息
     */
    private String return_msg;

    /**
     * 错误码
     */
    private String err_code;

    /**
     * 订单号
     */
    private String out_detail_no;

    /**
     * 红包发送批次号
     */
    private String detail_id;

    /**
     * 红包总金额，单位：分
     */
    private Integer total_amount;

    /**
     * 红包总人数
     */
    private Integer total_num;

    /**
     * 已领取总金额，单位：分
     */
    private Integer sent_amount;

    /**
     * 已领取红包个数
     */
    private Integer sent_num;

    /**
     * 领取人数
     */
    private Integer receive_num;

    /**
     * 创建时间
     */
    private String send_time;
}
