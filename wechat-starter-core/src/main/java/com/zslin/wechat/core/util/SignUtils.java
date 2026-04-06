package com.zslin.wechat.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 签名工具类
 * <p>
 * 提供 MD5、SHA256、RSA 等签名算法，用于微信 API 签名生成和验证
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
public class SignUtils {

    private static final Logger log = LoggerFactory.getLogger(SignUtils.class);

    private SignUtils() {
        // 工具类禁止实例化
    }

    /**
     * 生成 MD5 签名
     *
     * @param content 待签名的内容
     * @return 32 位十六进制字符串（小写）
     */
    public static String md5(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(content.getBytes("UTF-8"));
            return bytesToHex(digest).toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("MD5 签名失败", e);
        }
    }

    /**
     * 生成 SHA-256 签名
     *
     * @param content 待签名的内容
     * @return 64 位十六进制字符串（小写）
     */
    public static String sha256(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(content.getBytes("UTF-8"));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 算法不可用", e);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 签名失败", e);
        }
    }

    /**
     * 生成微信支付 v2 签名（MD5）
     * 参数按字典序排序后拼接，最后加上 key
     *
     * @param params 参数键值对（已拼接好的字符串，不包含 key）
     * @param key    商户密钥
     * @return 签名（大写）
     */
    public static String md5Sign(String params, String key) {
        if (params == null || params.isEmpty()) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("密钥不能为空");
        }
        String signStr = params + "&key=" + key;
        return md5(signStr);
    }

    /**
     * 验证签名
     *
     * @param params  参数（已拼接）
     * @param key     密钥
     * @param sign    待验证的签名
     * @return true-签名有效，false-签名无效
     */
    public static boolean verifySign(String params, String key, String sign) {
        if (sign == null || sign.isEmpty()) {
            return false;
        }
        String calculatedSign = md5Sign(params, key);
        return calculatedSign.equals(sign.toUpperCase());
    }

    /**
     * 字节数组转十六进制字符串
     *
     * @param bytes 字节数组
     * @return 十六进制字符串（小写）
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // ==================== 微信支付 v3 签名相关 ====================

    /**
     * 生成微信支付 v3 签名（SHA256 with RSA）
     *
     * @param signData 待签名数据（已拼接好的字符串，包含\n）
     * @param privateKey 私钥
     * @return base64 编码的签名
     */
    public static String signV3(String signData, PrivateKey privateKey) {
        if (signData == null || signData.isEmpty()) {
            throw new IllegalArgumentException("待签名数据不能为空");
        }
        if (privateKey == null) {
            throw new IllegalArgumentException("私钥不能为空");
        }

        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(signData.getBytes("UTF-8"));
            byte[] sign = signature.sign();
            return Base64.getEncoder().encodeToString(sign);
        } catch (Exception e) {
            log.error("生成 v3 签名失败", e);
            throw new RuntimeException("签名生成失败：" + e.getMessage(), e);
        }
    }

    /**
     * 验证微信支付 v3 签名（SHA256 with RSA）
     *
     * @param signData 待验证数据（已拼接好的字符串，包含\n）
     * @param publicKey 公钥
     * @param signature base64 编码的签名
     * @return true-签名有效，false-签名无效
     */
    public static boolean verifyV3(String signData, PublicKey publicKey, String signature) {
        if (signData == null || signData.isEmpty()) {
            return false;
        }
        if (publicKey == null || signature == null || signature.isEmpty()) {
            return false;
        }

        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);
            sig.update(signData.getBytes("UTF-8"));
            byte[] signBytes = Base64.getDecoder().decode(signature);
            return sig.verify(signBytes);
        } catch (Exception e) {
            log.error("验证 v3 签名失败", e);
            return false;
        }
    }

    /**
     * 从 PEM 格式字符串加载私钥
     *
     * @param pem 私钥 PEM 字符串（包含 BEGIN/END 标记）
     * @return 私钥对象
     */
    public static PrivateKey loadPrivateKeyFromPem(String pem) {
        try {
            // 移除 PEM 头尾标记和换行符
            String keyContent = pem.replaceAll("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(keyContent);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            log.error("加载私钥失败", e);
            throw new RuntimeException("私钥加载失败：" + e.getMessage(), e);
        }
    }

    /**
     * 从 PEM 格式字符串加载公钥
     *
     * @param pem 公钥 PEM 字符串（包含 BEGIN/END 标记）
     * @return 公钥对象
     */
    public static PublicKey loadPublicKeyFromPem(String pem) {
        try {
            // 移除 PEM 头尾标记和换行符
            String keyContent = pem.replaceAll("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(keyContent);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            log.error("加载公钥失败", e);
            throw new RuntimeException("公钥加载失败：" + e.getMessage(), e);
        }
    }

    /**
     * 从微信支付证书序列号获取公钥
     *
     * @param serialNo 证书序列号（16 进制）
     * @param certificate 证书内容（base64 编码）
     * @return 公钥对象
     */
    public static PublicKey extractPublicKeyFromCert(String serialNo, String certificate) {
        try {
            byte[] certBytes = Base64.getDecoder().decode(certificate);
            // 这里简化处理，实际需要从证书中提取公钥
            // 由于 Java 的证书解析较复杂，这里暂不实现完整逻辑
            log.warn("从证书提取公钥功能待完善");
            return null;
        } catch (Exception e) {
            log.error("从证书提取公钥失败", e);
            return null;
        }
    }
}
