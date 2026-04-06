package com.zslin.wechat.core.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间工具类
 * <p>
 * 提供常用的日期时间格式化和解析操作
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
public class DateUtils {

    // 常用日期时间格式器
    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private DateUtils() {
        // 工具类禁止实例化
    }

    /**
     * 获取当前时间字符串（默认格式）
     *
     * @return 格式化后的时间字符串
     */
    public static String now() {
        return LocalDateTime.now().format(DEFAULT_FORMATTER);
    }

    /**
     * 获取当前日期字符串
     *
     * @return 格式化后的日期字符串
     */
    public static String today() {
        return LocalDateTime.now().format(DATE_FORMATTER);
    }

    /**
     * 格式化 LocalDateTime
     *
     * @param dateTime 日期时间对象
     * @param pattern  格式模式
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    /**
     * 格式化 LocalDateTime（默认格式）
     *
     * @param dateTime 日期时间对象
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime) {
        return format(dateTime, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 解析字符串为 LocalDateTime
     *
     * @param str     日期时间字符串
     * @param pattern 格式模式
     * @return LocalDateTime 对象
     */
    public static LocalDateTime parse(String str, String pattern) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(str, formatter);
    }

    /**
     * 解析字符串为 LocalDateTime（默认格式）
     *
     * @param str 日期时间字符串
     * @return LocalDateTime 对象
     */
    public static LocalDateTime parse(String str) {
        return parse(str, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取时间戳（毫秒）
     *
     * @return 时间戳
     */
    public static long timestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 将时间戳转换为 LocalDateTime
     *
     * @param timestamp 时间戳（毫秒）
     * @return LocalDateTime 对象
     */
    public static LocalDateTime fromTimestamp(long timestamp) {
        return LocalDateTime.ofEpochSecond(timestamp / 1000, 0, java.time.ZoneOffset.UTC);
    }

    /**
     * 计算两个日期之间的天数差
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 天数差
     */
    public static long daysBetween(java.time.LocalDate start, java.time.LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(start, end);
    }
}
