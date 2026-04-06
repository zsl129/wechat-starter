package com.zslin.wechat.qrcode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.ByteArrayOutputStream;

/**
 * 小程序码生成结果
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeResult {

    /**
     * 是否生成成功
     */
    private boolean success;

    /**
     * 二维码图片数据（二进制）
     */
    private byte[] qrCodeData;

    /**
     * 图片 URL（如果上传到 CDN）
     */
    private String qrCodeUrl;

    /**
     * 场景值
     */
    private String scene;

    /**
     * 跳转路径
     */
    private String page;

    /**
     * 二维码类型（0-临时码，1-永久码）
     */
    private Integer type;

    /**
     * 图片大小（字节）
     */
    private Long size;

    /**
     * 错误信息（如果失败）
     */
    private String errorMsg;

    /**
     * 获取图片 Base64 编码
     */
    public String toBase64() {
        if (qrCodeData == null) {
            return null;
        }
        return java.util.Base64.getEncoder().encodeToString(qrCodeData);
    }

    /**
     * 转换为输出流
     */
    public ByteArrayOutputStream toByteArrayOutputStream() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (qrCodeData != null) {
            baos.write(qrCodeData, 0, qrCodeData.length);
        }
        return baos;
    }
}
