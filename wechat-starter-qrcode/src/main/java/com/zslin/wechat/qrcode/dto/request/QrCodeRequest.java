package com.zslin.wechat.qrcode.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 小程序码生成请求
 * <p>
 * 支持临时小程序码（推荐）和永久小程序码
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeRequest {

    /**
     * 场景值（最大 32 字符）
     * 临时码：支持无限场景值，自动扩容
     * 永久码：限制 10 万个场景值
     */
    private String scene;

    /**
     * 跳转路径（填 "#" 则跳转到小程序首页）
     * 例如：pages/index?id=123
     */
    private String page = "#";

    /**
     * 二维码图片的边长（正方形）
     * 默认 430，范围 2-1600
     */
    private Integer width = 430;

    /**
     * 是否自动扩容
     * 仅在临时小程序码时有效
     */
    private Boolean autoColor = false;

    /**
     * 边框颜色（十进制表示的 RGB 值）
     * 默认黑色：0x000000
     */
    private String lineColor = "0x000000";

    /**
     * 是否包含 Logo
     */
    private Boolean withLogo = false;

    /**
     * 码类型
     * 0 - 临时小程序码（推荐，支持无限场景）
     * 1 - 永久小程序码（限制 10 万场景）
     */
    private Integer type = 0;
}
