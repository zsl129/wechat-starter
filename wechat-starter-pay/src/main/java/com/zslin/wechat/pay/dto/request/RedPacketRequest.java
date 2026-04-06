package com.zslin.wechat.pay.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 红包请求 DTO
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedPacketRequest {

    /**
     * 接收红包的用户的 openid
     */
    private String receiverOpenid;

    /**
     * 订单号
     */
    private String outDetailNo;

    /**
     * 红包总金额，单位：分
     */
    private Integer total_amount;

    /**
     * 微信商户号，填写 0 或空时代表随机发
     */
    private Integer wx_account = 0;

    /**
     * 红包标题
     */
    private String remark;

    /**
     * 活动 ID
     */
    private String activity_id;

    /**
     * 红包类型：NORMAL-普通红包、MARKETING-营销红包
     */
    private String lucky_type = "NORMAL";

    /**
     * 封板图片 ID
     */
    private String cover_imgid;
}
