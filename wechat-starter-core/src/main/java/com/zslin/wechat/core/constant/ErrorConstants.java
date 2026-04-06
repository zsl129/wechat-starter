package com.zslin.wechat.core.constant;

/**
 * 错误常量定义
 * <p>
 * 定义系统通用错误码和错误信息
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
public final class ErrorConstants {

    private ErrorConstants() {
        // 工具类禁止实例化
    }

    /**
     * 成功
     */
    public static final int SUCCESS_CODE = 200;
    public static final String SUCCESS_MSG = "操作成功";

    /**
     * 通用错误
     */
    public static final int ERROR_CODE = 500;
    public static final String ERROR_MSG = "操作失败";

    /**
     * 参数错误
     */
    public static final int PARAM_ERROR_CODE = 400;
    public static final String PARAM_ERROR_MSG = "参数错误";

    /**
     * 未授权
     */
    public static final int UNAUTHORIZED_CODE = 401;
    public static final String UNAUTHORIZED_MSG = "未授权";

    /**
     * 禁止访问
     */
    public static final int FORBIDDEN_CODE = 403;
    public static final String FORBIDDEN_MSG = "禁止访问";

    /**
     * 资源未找到
     */
    public static final int NOT_FOUND_CODE = 404;
    public static final String NOT_FOUND_MSG = "资源未找到";

    /**
     * 服务不可用
     */
    public static final int SERVICE_UNAVAILABLE_CODE = 503;
    public static final String SERVICE_UNAVAILABLE_MSG = "服务不可用";
}
