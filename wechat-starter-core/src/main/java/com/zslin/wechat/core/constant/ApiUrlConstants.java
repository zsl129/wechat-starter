package com.zslin.wechat.core.constant;

/**
 * 微信 API 地址常量
 * <p>
 * 集中管理所有微信 API 的 URL 地址
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
public final class ApiUrlConstants {

    private ApiUrlConstants() {
        // 工具类禁止实例化
    }

    /**
     * 微信 API 基础地址
     */
    public static final String WECHAT_API_BASE_URL = "https://api.weixin.qq.com";

    /**
     * 小程序登录 - 校验 code 并获取 openid 和 session_key
     */
    public static final String MINIAPP_JS_CODE2SESSION = WECHAT_API_BASE_URL + "/sns/jscode2session";

    /**
     * 用户信息获取（旧版，已下线）
     * 注：现在需要通过加密数据解密获取
     */
    @Deprecated
    public static final String MINIAPP_GET_USERINFO = WECHAT_API_BASE_URL + "/sns/userinfo";

    /**
     * 微信授权 - OAuth2.0 网页授权
     */
    public static final String OAUTH2_AUTHORIZE = WECHAT_API_BASE_URL + "/sns/oauth2/access_token";

    /**
     * 微信支付 - 统一下单
     */
    public static final String PAY_UNIFIED_ORDER = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";

    /**
     * 微信支付 - 查询订单
     */
    public static final String PAY_ORDER_QUERY = "https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/";

    /**
     * 微信支付 - 退款
     */
    public static final String PAY_REFUND = "https://api.mch.weixin.qq.com/v3/refund/domestic/refunds";

    /**
     * 消息模板 - 获取模板列表
     */
    public static final String MESSAGE_TEMPLATE_LIST = WECHAT_API_BASE_URL + "/cgi-bin/message/template/get";

    /**
     * 消息模板 - 发送订阅消息
     */
    public static final String MESSAGE_SEND_SUBSCRIBE = WECHAT_API_BASE_URL + "/cgi-bin/message/subscribe/send";

    /**
     * 微信服务器配置 URL
     */
    public static final String SERVER_CONFIG_URL = WECHAT_API_BASE_URL + "/cgi-binticket/cgi-bin/token";
}
