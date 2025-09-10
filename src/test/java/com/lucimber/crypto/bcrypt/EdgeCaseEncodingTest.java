package com.lucimber.crypto.bcrypt;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Edge case tests for UTF-8 encoding and character handling in BCrypt implementation. Tests
 * Unicode, truncation, and encoding edge cases.
 */
class EdgeCaseEncodingTest {

    private final BCryptService service = BCryptService.getInstance();

    @Test
    @DisplayName("Should handle exactly 72 bytes with multi-byte UTF-8")
    void shouldHandleExactly72BytesWithMultiByteUtf8() {
        // Create a string that's exactly 72 bytes with multi-byte chars
        // 24 × "€" (3 bytes each) = 72 bytes
        String euroString = "€".repeat(24);
        assertEquals(72, euroString.getBytes(StandardCharsets.UTF_8).length);

        Password password = new Password(euroString);
        Hash hash = service.hash(password);

        assertTrue(service.verify(password, hash));

        // 73 bytes should be truncated to match
        String euroString73 = "€".repeat(24) + "x";
        Password password73 = new Password(euroString73);

        // Should match because of truncation
        assertTrue(service.verify(password73, hash));
    }

    @Test
    @DisplayName("Should handle UTF-8 truncation at character boundary")
    void shouldHandleUtf8TruncationAtCharacterBoundary() {
        // Create a string where 72-byte truncation happens mid-character
        // 70 bytes of 'a' + one 4-byte emoji = 74 bytes total
        String mixedString = "a".repeat(70) + "🎉";
        assertEquals(74, mixedString.getBytes(StandardCharsets.UTF_8).length);

        Password password = new Password(mixedString);
        byte[] bytes = password.getBytes();

        // Should truncate to exactly 72 bytes
        assertEquals(72, bytes.length);

        // BCrypt truncates at byte level, which may break UTF-8 characters
        // This is expected behavior - BCrypt works with bytes, not characters
        // The truncated bytes might not form valid UTF-8 if cut mid-character

        // Create another password that's exactly 70 bytes
        Password password70 = new Password("a".repeat(70));
        byte[] bytes70 = password70.getBytes();
        assertEquals(70, bytes70.length);

        // The first 70 bytes should match
        for (int i = 0; i < 70; i++) {
            assertEquals(bytes70[i], bytes[i], "Byte at position " + i + " should match");
        }
    }

    @Test
    @DisplayName("Should handle Unicode normalization")
    void shouldHandleUnicodeNormalization() {
        // Test different Unicode representations of the same character
        // é can be represented as single character or e + combining accent
        String composed = "café"; // é as single character
        String decomposed = "cafe\u0301"; // e + combining acute accent

        // These are different byte sequences
        assertNotEquals(
                composed.getBytes(StandardCharsets.UTF_8).length,
                decomposed.getBytes(StandardCharsets.UTF_8).length);

        Password pwd1 = new Password(composed);
        Password pwd2 = new Password(decomposed);

        Hash hash1 = service.hash(pwd1);

        // BCrypt treats these as different passwords (no normalization)
        assertTrue(service.verify(pwd1, hash1));
        assertFalse(service.verify(pwd2, hash1));
    }

    @Test
    @DisplayName("Should handle surrogate pairs correctly")
    void shouldHandleSurrogatePairsCorrectly() {
        // Test with emoji and other characters that use surrogate pairs
        String surrogatePassword = "🎉🎊🎈🎁🎀";
        Password password = new Password(surrogatePassword);
        Hash hash = service.hash(password);

        assertTrue(service.verify(password, hash));

        // Each emoji is 4 bytes in UTF-8
        assertEquals(20, surrogatePassword.getBytes(StandardCharsets.UTF_8).length);
    }

    @Test
    @DisplayName("Should handle bidirectional text")
    void shouldHandleBidirectionalText() {
        // Mix of LTR and RTL text
        String mixed = "Hello שלום مرحبا";
        Password password = new Password(mixed);
        Hash hash = service.hash(password);

        assertTrue(service.verify(password, hash));
    }

    @Test
    @DisplayName("Should handle various UTF-8 byte sequences")
    void shouldHandleVariousUtf8ByteSequences() {
        // Test various UTF-8 sequences
        String[] utf8Strings = {
            "ASCII", // 1-byte sequences
            "Ääöö", // 2-byte sequences
            "€€€", // 3-byte sequences
            "𝔸𝔹ℂ", // 4-byte sequences (mathematical bold)
            "中文测试", // Chinese characters
            "🏳️‍🌈", // Complex emoji with ZWJ
            "а́а̀а̂а̃", // Cyrillic with combining marks
        };

        for (String str : utf8Strings) {
            Password password = new Password(str);
            Hash hash = service.hash(password);

            assertTrue(service.verify(password, hash), "Should handle UTF-8 string: " + str);
        }
    }

    @Test
    @DisplayName("Should handle zero-width and invisible characters")
    void shouldHandleZeroWidthAndInvisibleCharacters() {
        // Test with zero-width and invisible Unicode characters
        String[] invisiblePasswords = {
            "pass\u200Bword", // Zero-width space
            "pass\u200Cword", // Zero-width non-joiner
            "pass\u200Dword", // Zero-width joiner
            "pass\uFEFFword", // Zero-width no-break space (BOM)
            "pass\u2060word", // Word joiner
        };

        for (String pwd : invisiblePasswords) {
            Password password = new Password(pwd);
            Hash hash = service.hash(password);

            assertTrue(
                    service.verify(password, hash),
                    "Should handle invisible character in password");

            // Should not match without the invisible character
            Password withoutInvisible = new Password("password");
            assertFalse(service.verify(withoutInvisible, hash));
        }
    }

    @Test
    @DisplayName("Should handle combining characters")
    void shouldHandleCombiningCharacters() {
        // Test with various combining characters
        String baseChar = "a";
        String[] combiningVersions = {
            "a\u0300", // à (grave accent)
            "a\u0301", // á (acute accent)
            "a\u0302", // â (circumflex)
            "a\u0303", // ã (tilde)
            "a\u0308", // ä (diaeresis)
            "a\u030A", // å (ring above)
        };

        for (String combined : combiningVersions) {
            Password password = new Password(combined);
            Hash hash = service.hash(password);

            assertTrue(
                    service.verify(password, hash),
                    "Should handle combining character: " + combined);

            // Should not match the base character alone
            Password basePassword = new Password(baseChar);
            assertFalse(service.verify(basePassword, hash));
        }
    }

    @Test
    @DisplayName("Should handle mixed encoding edge cases at 72-byte boundary")
    void shouldHandleMixedEncodingEdgeCasesAt72ByteBoundary() {
        // Create strings that hit the 72-byte boundary in interesting ways

        // 71 ASCII bytes + 2-byte UTF-8 char (will be truncated to 72)
        String case1 = "a".repeat(71) + "ä"; // 71 + 2 = 73, truncated to 72
        assertEquals(73, case1.getBytes(StandardCharsets.UTF_8).length);

        // 70 ASCII bytes + 3-byte UTF-8 char (will be truncated to 72)
        String case2 = "a".repeat(70) + "€"; // 70 + 3 = 73, truncated to 72
        assertEquals(73, case2.getBytes(StandardCharsets.UTF_8).length);

        // 69 ASCII bytes + 4-byte UTF-8 char (will be truncated to 72)
        String case3 = "a".repeat(69) + "𝔸"; // 69 + 4 = 73, truncated to 72
        assertEquals(73, case3.getBytes(StandardCharsets.UTF_8).length);

        Password pwd1 = new Password(case1);
        Password pwd2 = new Password(case2);
        Password pwd3 = new Password(case3);

        // All should truncate to 72 bytes
        assertEquals(72, pwd1.getBytes().length);
        assertEquals(72, pwd2.getBytes().length);
        assertEquals(72, pwd3.getBytes().length);

        // Each should hash and verify correctly
        Hash hash1 = service.hash(pwd1);
        Hash hash2 = service.hash(pwd2);
        Hash hash3 = service.hash(pwd3);

        assertTrue(service.verify(pwd1, hash1));
        assertTrue(service.verify(pwd2, hash2));
        assertTrue(service.verify(pwd3, hash3));
    }
}
