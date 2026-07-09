package cc.hongz.ztools.response.ajax;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link AjaxStatuses} 工具类单元测试.
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("AjaxStatuses 工具类测试")
class AjaxStatusesTest {

    // ==================== buildCode 测试 ====================

    @Nested
    @DisplayName("buildCode 构建状态码")
    class BuildCodeTests {

        @Test
        @DisplayName("buildCode(prefix, number) - 默认分隔符")
        void testBuildCode_DefaultSeparator() {
            assertEquals("USER_404", AjaxStatuses.buildCode("USER", "404"));
            assertEquals("COMMON_000", AjaxStatuses.buildCode("COMMON", "000"));
            assertEquals("ORDER_2001", AjaxStatuses.buildCode("ORDER", "2001"));
        }

        @Test
        @DisplayName("buildCode(prefix, number, separator) - 自定义分隔符")
        void testBuildCode_CustomSeparator() {
            assertEquals("ORD-2001", AjaxStatuses.buildCode("ORD", "2001", "-"));
            assertEquals("USER.404", AjaxStatuses.buildCode("USER", "404", "."));
            assertEquals("SYS:500", AjaxStatuses.buildCode("SYS", "500", ":"));
        }

        @Test
        @DisplayName("buildCode - prefix 为空抛出异常")
        void testBuildCode_NullPrefix() {
            assertThrows(IllegalArgumentException.class, () -> {
                AjaxStatuses.buildCode(null, "404");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                AjaxStatuses.buildCode("", "404");
            });
        }

        @Test
        @DisplayName("buildCode - number 为空抛出异常")
        void testBuildCode_NullNumber() {
            assertThrows(IllegalArgumentException.class, () -> {
                AjaxStatuses.buildCode("USER", null);
            });
            assertThrows(IllegalArgumentException.class, () -> {
                AjaxStatuses.buildCode("USER", "");
            });
        }

        @Test
        @DisplayName("buildCode - separator 为空抛出异常")
        void testBuildCode_NullSeparator() {
            assertThrows(IllegalArgumentException.class, () -> {
                AjaxStatuses.buildCode("USER", "404", null);
            });
            assertThrows(IllegalArgumentException.class, () -> {
                AjaxStatuses.buildCode("USER", "404", "");
            });
        }
    }

    // ==================== extractPrefix 测试 ====================

    @Nested
    @DisplayName("extractPrefix 提取前缀")
    class ExtractPrefixTests {

        @Test
        @DisplayName("extractPrefix(code) - 默认分隔符")
        void testExtractPrefix_DefaultSeparator() {
            assertEquals("USER", AjaxStatuses.extractPrefix("USER_404"));
            assertEquals("COMMON", AjaxStatuses.extractPrefix("COMMON_000"));
            assertEquals("ORDER", AjaxStatuses.extractPrefix("ORDER_2001"));
        }

        @Test
        @DisplayName("extractPrefix(code, separator) - 自定义分隔符")
        void testExtractPrefix_CustomSeparator() {
            assertEquals("ORD", AjaxStatuses.extractPrefix("ORD-2001", "-"));
            assertEquals("USER", AjaxStatuses.extractPrefix("USER.404", "."));
        }

        @Test
        @DisplayName("extractPrefix - 无分隔符返回 COMMON")
        void testExtractPrefix_NoSeparator() {
            assertEquals("COMMON", AjaxStatuses.extractPrefix("200"));
            assertEquals("COMMON", AjaxStatuses.extractPrefix("SUCCESS"));
        }

        @Test
        @DisplayName("extractPrefix - null 或空字符串返回 COMMON")
        void testExtractPrefix_NullOrEmpty() {
            assertEquals("COMMON", AjaxStatuses.extractPrefix(null));
            assertEquals("COMMON", AjaxStatuses.extractPrefix(""));
        }

        @Test
        @DisplayName("extractPrefix - 分隔符在末尾返回前缀")
        void testExtractPrefix_TrailingSeparator() {
            assertEquals("USER", AjaxStatuses.extractPrefix("USER_"));
        }
    }

    // ==================== extractNumber 测试 ====================

    @Nested
    @DisplayName("extractNumber 提取数字部分")
    class ExtractNumberTests {

        @Test
        @DisplayName("extractNumber(code) - 默认分隔符")
        void testExtractNumber_DefaultSeparator() {
            assertEquals("404", AjaxStatuses.extractNumber("USER_404"));
            assertEquals("000", AjaxStatuses.extractNumber("COMMON_000"));
            assertEquals("2001", AjaxStatuses.extractNumber("ORDER_2001"));
        }

        @Test
        @DisplayName("extractNumber(code, separator) - 自定义分隔符")
        void testExtractNumber_CustomSeparator() {
            assertEquals("2001", AjaxStatuses.extractNumber("ORD-2001", "-"));
            assertEquals("404", AjaxStatuses.extractNumber("USER.404", "."));
        }

        @Test
        @DisplayName("extractNumber - 无分隔符返回原码")
        void testExtractNumber_NoSeparator() {
            assertEquals("200", AjaxStatuses.extractNumber("200"));
            assertEquals("SUCCESS", AjaxStatuses.extractNumber("SUCCESS"));
        }

        @Test
        @DisplayName("extractNumber - null 或空字符串返回原值")
        void testExtractNumber_NullOrEmpty() {
            assertNull(AjaxStatuses.extractNumber(null));
            assertEquals("", AjaxStatuses.extractNumber(""));
        }
    }

    // ==================== isValidFormat 测试 ====================

    @Nested
    @DisplayName("isValidFormat 验证格式")
    class IsValidFormatTests {

        @Test
        @DisplayName("isValidFormat(code) - 默认分隔符")
        void testIsValidFormat_DefaultSeparator() {
            assertTrue(AjaxStatuses.isValidFormat("USER_404"));
            assertTrue(AjaxStatuses.isValidFormat("COMMON_000"));
            assertFalse(AjaxStatuses.isValidFormat("200"));
            assertFalse(AjaxStatuses.isValidFormat("_404"));
            assertFalse(AjaxStatuses.isValidFormat("USER_"));
        }

        @Test
        @DisplayName("isValidFormat(code, separator) - 自定义分隔符")
        void testIsValidFormat_CustomSeparator() {
            assertTrue(AjaxStatuses.isValidFormat("ORD-2001", "-"));
            assertFalse(AjaxStatuses.isValidFormat("ORD2001", "-"));
            assertFalse(AjaxStatuses.isValidFormat("-2001", "-"));
            assertFalse(AjaxStatuses.isValidFormat("ORD-", "-"));
        }

        @Test
        @DisplayName("isValidFormat - null 或空字符串返回 false")
        void testIsValidFormat_NullOrEmpty() {
            assertFalse(AjaxStatuses.isValidFormat(null));
            assertFalse(AjaxStatuses.isValidFormat(""));
            assertFalse(AjaxStatuses.isValidFormat(null, "_"));
            assertFalse(AjaxStatuses.isValidFormat("USER_404", null));
            assertFalse(AjaxStatuses.isValidFormat("USER_404", ""));
        }
    }

    // ==================== DEFAULT_SEPARATOR 常量测试 ====================

    @Test
    @DisplayName("DEFAULT_SEPARATOR 常量值为下划线")
    void testDefaultSeparator() {
        assertEquals("_", AjaxStatuses.DEFAULT_SEPARATOR);
    }
}
