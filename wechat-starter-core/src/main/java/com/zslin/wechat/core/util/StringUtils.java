package com.zslin.wechat.core.util;

/**
 * 字符串工具类
 * <p>
 * 提供常用的字符串操作，避免重复代码
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
public class StringUtils {

    private StringUtils() {
        // 工具类禁止实例化
    }

    /**
     * 判断字符串是否为空
     *
     * @param str 待判断的字符串
     * @return true-空，false-非空
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str 待判断的字符串
     * @return true-非空，false-空
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 判断字符串是否为空（包括 null 和空字符串）
     *
     * @param str 待判断的字符串
     * @return true-空，false-非空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str 待判断的字符串
     * @return true-非空，false-空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 字符串转布尔值
     *
     * @param str 字符串
     * @return 布尔值，如果无法解析返回 false
     */
    public static boolean toBoolean(String str) {
        if (isBlank(str)) {
            return false;
        }
        return Boolean.parseBoolean(str.trim().toLowerCase());
    }

    /**
     * 字符串转整数
     *
     * @param str  字符串
     * @param def  默认值
     * @return 整数
     */
    public static int toInt(String str, int def) {
        if (isBlank(str)) {
            return def;
        }
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * 字符串转长整型
     *
     * @param str  字符串
     * @param def  默认值
     * @return 长整型
     */
    public static long toLong(String str, long def) {
        if (isBlank(str)) {
            return def;
        }
        try {
            return Long.parseLong(str.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * 字符串转 Double
     *
     * @param str  字符串
     * @param def  默认值
     * @return Double
     */
    public static Double toDouble(String str, Double def) {
        if (isBlank(str)) {
            return def;
        }
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * 截断字符串
     *
     * @param str     原始字符串
     * @param maxLength 最大长度
     * @param suffix  后缀（截断后添加）
     * @return 截断后的字符串
     */
    public static String truncate(String str, int maxLength, String suffix) {
        if (isEmpty(str) || maxLength <= 0) {
            return str;
        }
        if (str.length() <= maxLength) {
            return str;
        }
        if (suffix == null) {
            suffix = "";
        }
        int actualMax = maxLength - suffix.length();
        if (actualMax <= 0) {
            return suffix;
        }
        return str.substring(0, actualMax) + suffix;
    }

    /**
     * 左填充
     *
     * @param str      原始字符串
     * @param size     目标长度
     * @param padChar  填充字符
     * @return 填充后的字符串
     */
    public static String padLeft(String str, int size, char padChar) {
        if (isEmpty(str)) {
            str = "";
        }
        if (str.length() >= size) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size - str.length(); i++) {
            sb.append(padChar);
        }
        sb.append(str);
        return sb.toString();
    }
}
