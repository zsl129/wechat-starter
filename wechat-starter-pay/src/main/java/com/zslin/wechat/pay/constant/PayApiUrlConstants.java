package com.zslin.wechat.pay.constant;

/**
 * 微信支付 API 地址常量
 *
 * @author 子墨
 * @since 1.0.0
 */
public class PayApiUrlConstants {

    private static final String BASE_URL = "https://api.mch.weixin.qq.com";

    // ========== 支付 ==========
    public static final String JSAPI_PAY = BASE_URL + "/v3/pay/transactions/jsapi";
    public static final String NATIVE_PAY = BASE_URL + "/v3/pay/transactions/native";
    public static final String APP_PAY = BASE_URL + "/v3/pay/transactions/app";

    // ========== 订单查询 ==========
    public static final String QUERY_ORDER_BY_TXNID = BASE_URL + "/v3/transaction/out-trade-no";
    public static final String QUERY_ORDER_BY_OUTTRADENO = BASE_URL + "/v3/transaction/out-trade-no";

    // ========== 退款 ==========
    public static final String REFUND = BASE_URL + "/v3/refund/domestic/refunds";
    public static final String QUERY_REFUND = BASE_URL + "/v3/refund/domestic/refunds/{out_refund_no}";

    // ========== 分账 ==========
    public static final String PROFITSHARING_ADD_RECEIVER = BASE_URL + "/v3/profitsharing/receivers/batchadd";
    public static final String PROFITSHARING = BASE_URL + "/v3/profitsharing/transfers";
    public static final String QUERY_PROFITSHARING = BASE_URL + "/v3/profitsharing/results/{out_request_no}";

    // ========== 商家转账 ==========
    public static final String MCH_TRANSFER = BASE_URL + "/v3/transfer/batches";
    public static final String QUERY_TRANSFER = BASE_URL + "/v3/transfer/batches/{out_batch_no}";

    // ========== 现金红包 ==========
    public static final String CASH_RED_PACKET = BASE_URL + "/v3/mktmoney/redpackets/single";

    // ========== 对账单 ==========
    public static final String DOWNLOAD_BILL = BASE_URL + "/v3/bill/tradebill";

    private PayApiUrlConstants() {
        // 工具类，禁止实例化
    }
}
