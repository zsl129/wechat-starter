package com.zslin.wechat.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 工具类测试
 *
 * @author 子墨
 */
public class CoreUtilsTest {

    // ========== SignUtils 测试 ==========
    
    @Test
    void testMd5() {
        String result = SignUtils.md5("hello");
        assertNotNull(result);
        assertEquals(32, result.length());
        // MD5("hello") = "5D41402ABC4B2A76B9719D911017C592"
        assertEquals("5D41402ABC4B2A76B9719D911017C592", result);
    }

    @Test
    void testMd5Empty() {
        assertNull(SignUtils.md5(""));
        assertNull(SignUtils.md5(null));
    }

    @Test
    void testSha256() {
        String result = SignUtils.sha256("hello");
        assertNotNull(result);
        assertEquals(64, result.length());
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", result);
    }

    @Test
    void testMd5Sign() {
        String sign = SignUtils.md5Sign("param1=value1&param2=value2", "key123");
        assertNotNull(sign);
        assertEquals(32, sign.length());
    }

    @Test
    void testMd5SignParamsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            SignUtils.md5Sign("", "key");
        });
    }

    @Test
    void testVerifySign() {
        String params = "param1=value1";
        String key = "key123";
        String sign = SignUtils.md5Sign(params, key);
        
        assertTrue(SignUtils.verifySign(params, key, sign));
        assertFalse(SignUtils.verifySign(params, key, "wrong_sign"));
        assertFalse(SignUtils.verifySign(params, key, null));
    }

    // ========== StringUtils 测试 ==========
    
    @Test
    void testIsBlank() {
        assertTrue(StringUtils.isBlank(null));
        assertTrue(StringUtils.isBlank(""));
        assertTrue(StringUtils.isBlank("   "));
        assertFalse(StringUtils.isBlank("hello"));
        assertFalse(StringUtils.isBlank("  hello  "));
    }

    @Test
    void testIsEmpty() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty(" "));
        assertFalse(StringUtils.isEmpty("hello"));
    }

    @Test
    void testToInt() {
        assertEquals(123, StringUtils.toInt("123", 0));
        assertEquals(0, StringUtils.toInt("", 0));
        assertEquals(0, StringUtils.toInt(null, 0));
        assertEquals(-1, StringUtils.toInt("abc", -1));
    }

    @Test
    void testToLong() {
        assertEquals(123456789L, StringUtils.toLong("123456789", 0L));
        assertEquals(0L, StringUtils.toLong("", 0L));
    }

    @Test
    void testTruncate() {
        // "hello world" 长度 11，maxLength=10，suffix=". .."(4 位)
        // 实际截取：10-4=6 位 + ". .." = "hello . .."
        assertEquals("hello . ..", StringUtils.truncate("hello world", 10, ". .."));
        assertEquals("hello", StringUtils.truncate("hello", 10, ". .."));
        assertEquals(". ..", StringUtils.truncate("hello world", 4, ". .."));
    }

    @Test
    void testPadLeft() {
        assertEquals("00123", StringUtils.padLeft("123", 5, '0'));
        assertEquals("12345", StringUtils.padLeft("12345", 5, '0'));
        assertEquals("00000", StringUtils.padLeft("", 5, '0'));
    }

    // ========== JsonUtils 测试 ==========
    
    @Test
    void testToJson() {
        TestObject obj = new TestObject();
        obj.setName("test");
        obj.setValue(123);
        
        String json = JsonUtils.toJson(obj);
        assertNotNull(json);
        assertTrue(json.contains("test"));
        assertTrue(json.contains("123"));
    }

    @Test
    void testFromJson() {
        String json = "{\"name\":\"test\",\"value\":123}";
        TestObject obj = JsonUtils.fromJson(json, TestObject.class);
        
        assertNotNull(obj);
        assertEquals("test", obj.getName());
        assertEquals(123, obj.getValue());
    }

    @Test
    void testFromJsonNull() {
        assertNull(JsonUtils.fromJson(null, TestObject.class));
        assertNull(JsonUtils.fromJson("", TestObject.class));
    }

    // 测试用的内部类
    private static class TestObject {
        private String name;
        private int value;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
    }
}
