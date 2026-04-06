package com.zslin.wechat.qrcode.service;

/**
 * 小程序码服务接口
 * <p>
 * 提供小程序码生成功能：临时码、永久码、批量生成
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
public interface QrCodeService {

    /**
     * 生成临时小程序码（推荐）
     * <p>
     * 支持无限场景值，自动扩容
     * </p>
     *
     * @param scene 场景值（最大 32 字符）
     * @param page  跳转路径
     * @return 小程序码图片
     */
    byte[] generateUnlimitedQrCode(String scene, String page);

    /**
     * 生成永久小程序码
     * <p>
     * 限制 10 万个场景值
     * </p>
     *
     * @param scene 场景值
     * @param page  跳转路径
     * @return 小程序码图片
     */
    byte[] generateLimitQrCode(String scene, String page);

    /**
     * 批量生成小程序码
     *
     * @param scenes 场景值列表
     * @param page   跳转路径
     * @param type   码类型（0-临时，1-永久）
     * @return 小程序码列表
     */
    java.util.Map<String, byte[]> batchGenerate(
            java.util.List<String> scenes,
            String page,
            Integer type);

    /**
     * 生成带样式的小程序码
     *
     * @param request 请求参数
     * @return 小程序码结果
     */
    com.zslin.wechat.qrcode.dto.response.QrCodeResult generateWithStyle(
            com.zslin.wechat.qrcode.dto.request.QrCodeRequest request);
}
