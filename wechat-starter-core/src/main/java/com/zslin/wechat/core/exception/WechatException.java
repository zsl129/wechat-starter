package com.zslin.wechat.core.exception;

import lombok.Getter;

/**
 * 微信业务异常
 * <p>
 * 用于封装微信相关的业务异常，提供统一的异常处理机制
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Getter
public class WechatException extends RuntimeException {

    private final int code;

    /**
     * 根据错误码抛出异常
     *
     * @param errorCode 错误码枚举
     */
    public WechatException(WechatExceptionCodes errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 根据错误码和原因抛出异常
     *
     * @param errorCode 错误码枚举
     * @param cause     异常原因
     */
    public WechatException(WechatExceptionCodes errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
    }

    /**
     * 根据错误码和自定义消息抛出异常
     *
     * @param errorCode 错误码枚举
     * @param message   自定义消息
     */
    public WechatException(WechatExceptionCodes errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    /**
     * 根据错误码、自定义消息和原因抛出异常
     *
     * @param errorCode 错误码枚举
     * @param message   自定义消息
     * @param cause     异常原因
     */
    public WechatException(WechatExceptionCodes errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode.getCode();
    }

    /**
     * 直接指定错误码和消息
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public WechatException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 直接指定错误码、消息和原因
     *
     * @param code    错误码
     * @param message 错误消息
     * @param cause   异常原因
     */
    public WechatException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
