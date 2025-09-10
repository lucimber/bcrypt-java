package com.lucimber.crypto.bcrypt;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Edge case tests for UTF-8 encoding and character handling in BCrypt implementation.
 *
 * <p>Tests Unicode and encoding edge cases including: - Multi-byte UTF-8 characters at 72-byte
 * boundary - Character truncation mid-sequence - Unicode normalization forms (NFC vs NFD) -
 * Surrogate pairs and emoji - Bidirectional text (LTR/RTL) - Zero-width and invisible characters -
 * Combining diacritical marks
 */
class EdgeCaseEncodingTest {

    private final BCryptService service = BCryptService.getInstance();

    @Test
    @DisplayName("Should handle exactly 72 bytes with multi-byte UTF-8")
    void shouldHandleExactly72BytesWithMultiByteUtf8() {
        // Test exact 72-byte boundary with different multi-byte characters

        // Case 1: 24 × "€" (3 bytes each) = 72 bytes
        String euroString = "€".repeat(24);
        assertEquals(
                72,
                euroString.getBytes(StandardCharsets.UTF_8).length,
                "Should be exactly 72 bytes");

        Password euroPassword = new Password(euroString);
        Hash euroHash = service.hash(euroPassword);
        assertTrue(service.verify(euroPassword, euroHash), "Should verify 72-byte Euro string");

        // Adding any character should be truncated
        String euroString73 = euroString + "x";
        Password euroPassword73 = new Password(euroString73);
        assertTrue(
                service.verify(euroPassword73, euroHash),
                "Should match due to truncation at 72 bytes");

        // Case 2: 36 × 2-byte characters
        String twoByteString = "ä".repeat(36); // 36 × 2 = 72 bytes
        assertEquals(
                72,
                twoByteString.getBytes(StandardCharsets.UTF_8).length,
                "Should be exactly 72 bytes");

        Password twoBytePwd = new Password(twoByteString);
        Hash twoByteHash = service.hash(twoBytePwd);
        assertTrue(
                service.verify(twoBytePwd, twoByteHash),
                "Should verify 72-byte 2-byte char string");

        // Case 3: 18 × 4-byte characters
        String fourByteString = "🎉".repeat(18); // 18 × 4 = 72 bytes
        assertEquals(
                72,
                fourByteString.getBytes(StandardCharsets.UTF_8).length,
                "Should be exactly 72 bytes");

        Password fourBytePwd = new Password(fourByteString);
        Hash fourByteHash = service.hash(fourBytePwd);
        assertTrue(service.verify(fourBytePwd, fourByteHash), "Should verify 72-byte emoji string");
    }

    @Test
    @DisplayName("Should handle UTF-8 truncation at character boundary")
    void shouldHandleUtf8TruncationAtCharacterBoundary() {
        // Test truncation that occurs mid-character in UTF-8 sequences
        // BCrypt truncates at byte level, potentially breaking UTF-8 characters

        // Case 1: 4-byte character truncated
        String emoji70 = "a".repeat(70) + "🎉"; // 70 + 4 = 74 bytes
        assertEquals(
                74, emoji70.getBytes(StandardCharsets.UTF_8).length, "Original should be 74 bytes");

        Password emojiPwd = new Password(emoji70);
        byte[] emojiBytes = emojiPwd.getBytes();
        assertEquals(72, emojiBytes.length, "Should truncate to exactly 72 bytes");

        // Case 2: 3-byte character truncated
        String euro71 = "a".repeat(71) + "€"; // 71 + 3 = 74 bytes
        assertEquals(
                74, euro71.getBytes(StandardCharsets.UTF_8).length, "Original should be 74 bytes");

        Password euroPwd = new Password(euro71);
        byte[] euroBytes = euroPwd.getBytes();
        assertEquals(72, euroBytes.length, "Should truncate to exactly 72 bytes");

        // Case 3: 2-byte character truncated
        String umlaut72 = "a".repeat(72) + "ä"; // 72 + 2 = 74 bytes
        assertEquals(
                74,
                umlaut72.getBytes(StandardCharsets.UTF_8).length,
                "Original should be 74 bytes");

        Password umlautPwd = new Password(umlaut72);
        byte[] umlautBytes = umlautPwd.getBytes();
        assertEquals(72, umlautBytes.length, "Should truncate to exactly 72 bytes");

        // Verify that truncation cuts at byte level, not character level
        // The emoji case should have only the first 2 bytes of the 4-byte emoji
        Password exact70 = new Password("a".repeat(70));
        byte[] exact70Bytes = exact70.getBytes();

        // First 70 bytes should match exactly
        for (int i = 0; i < 70; i++) {
            assertEquals(
                    exact70Bytes[i],
                    emojiBytes[i],
                    "Byte at position " + i + " should match before truncation point");
        }

        // Bytes 70-71 are partial UTF-8 sequence from the truncated emoji
        // This demonstrates BCrypt's byte-level truncation behavior
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
        // Surrogate pairs are used in UTF-16 for characters outside the BMP
        // In UTF-8, these become 4-byte sequences

        // Test various 4-byte UTF-8 characters (surrogate pairs in UTF-16)
        String emojis = "🎉🎊🎈🎁🎀"; // 5 emojis
        assertEquals(
                20,
                emojis.getBytes(StandardCharsets.UTF_8).length,
                "5 emojis × 4 bytes = 20 bytes");

        Password emojiPwd = new Password(emojis);
        Hash emojiHash = service.hash(emojiPwd);
        assertTrue(service.verify(emojiPwd, emojiHash), "Should handle emoji password");

        // Test mathematical symbols (also 4-byte UTF-8)
        String mathSymbols = "𝔸𝔹ℂ𝔻𝔼"; // Mathematical bold letters
        Password mathPwd = new Password(mathSymbols);
        Hash mathHash = service.hash(mathPwd);
        assertTrue(service.verify(mathPwd, mathHash), "Should handle mathematical symbols");

        // Test at 72-byte boundary with surrogate pairs
        String boundaryEmojis = "🎉".repeat(18); // Exactly 72 bytes
        assertEquals(
                72,
                boundaryEmojis.getBytes(StandardCharsets.UTF_8).length,
                "Should be exactly 72 bytes");

        Password boundaryPwd = new Password(boundaryEmojis);
        Hash boundaryHash = service.hash(boundaryPwd);
        assertTrue(service.verify(boundaryPwd, boundaryHash), "Should handle emojis at boundary");

        // Adding one more emoji should be truncated
        String overflowEmojis = boundaryEmojis + "🎊";
        Password overflowPwd = new Password(overflowEmojis);
        assertTrue(service.verify(overflowPwd, boundaryHash), "Should match due to truncation");
    }

    @Test
    @DisplayName("Should handle bidirectional text")
    void shouldHandleBidirectionalText() {
        // Test mixing left-to-right (LTR) and right-to-left (RTL) scripts

        // English (LTR) + Hebrew (RTL) + Arabic (RTL)
        String mixed = "Hello שלום مرحبا";
        Password mixedPwd = new Password(mixed);
        Hash mixedHash = service.hash(mixedPwd);
        assertTrue(service.verify(mixedPwd, mixedHash), "Should handle mixed LTR/RTL text");

        // Test with directional marks
        String withMarks = "Test\u202Eمرحبا\u202CHello"; // RLO and PDF marks
        Password marksPwd = new Password(withMarks);
        Hash marksHash = service.hash(marksPwd);
        assertTrue(service.verify(marksPwd, marksHash), "Should handle directional marks");

        // Pure RTL text
        String pureRtl = "עברית וערבית معًا";
        Password rtlPwd = new Password(pureRtl);
        Hash rtlHash = service.hash(rtlPwd);
        assertTrue(service.verify(rtlPwd, rtlHash), "Should handle pure RTL text");

        // Verify byte-level processing ignores text direction
        byte[] mixedBytes = mixedPwd.getBytes();
        assertTrue(mixedBytes.length > 0, "Should have non-empty byte representation");
    }

    @Test
    @DisplayName("Should handle various UTF-8 byte sequences")
    void shouldHandleVariousUtf8ByteSequences() {
        // Comprehensive test of UTF-8 encoding from 1 to 4 bytes

        // Structure: String, expected byte count per character, description
        Object[][] testCases = {
            {"ASCII", 1, "Basic ASCII (1-byte sequences)"},
            {"Ääöö", 2, "Latin Extended-A (2-byte sequences)"},
            {"€¥£", 3, "Currency symbols (3-byte sequences)"},
            {"𝔸𝔹ℂ", 4, "Mathematical bold (4-byte sequences)"},
            {"中文测试", 3, "Chinese characters (3-byte CJK)"},
            {"日本語", 3, "Japanese characters (3-byte)"},
            {"한글", 3, "Korean Hangul (3-byte)"},
            {"🏳️‍🌈", -1, "Complex emoji with ZWJ sequence"},
            {"а́а̀а̂а̃", -1, "Cyrillic with combining diacriticals"},
            {"Ω±∞≈", -1, "Mixed mathematical symbols"},
            {"\u0000\u0001\u001F", 1, "Control characters"},
        };

        for (Object[] testCase : testCases) {
            String str = (String) testCase[0];
            String desc = (String) testCase[2];

            Password password = new Password(str);
            Hash hash = service.hash(password);

            assertTrue(service.verify(password, hash), "Should handle " + desc + ": " + str);

            // Verify consistent hashing
            Hash hash2 = service.hash(password);
            assertNotEquals(
                    hash.getValue(),
                    hash2.getValue(),
                    "Different salt should produce different hash for: " + str);

            // But both should verify
            assertTrue(
                    service.verify(password, hash2),
                    "Should verify with different salt for: " + str);
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
        // Test how different multi-byte characters are truncated at the 72-byte boundary
        // This demonstrates BCrypt's byte-level truncation behavior

        // Case 1: 2-byte character straddling boundary (71 ASCII + 2-byte char)
        String twoByteCase = "a".repeat(71) + "ä"; // 71 + 2 = 73 bytes
        assertEquals(73, twoByteCase.getBytes(StandardCharsets.UTF_8).length, "Original: 73 bytes");

        Password twoBytePwd = new Password(twoByteCase);
        assertEquals(72, twoBytePwd.getBytes().length, "Should truncate to 72 bytes");

        // Only first byte of the 2-byte character is kept
        byte[] twoByteBytes = twoBytePwd.getBytes();
        assertEquals((byte) 'a', twoByteBytes[70], "Byte 70 should be 'a'");
        // Byte 71 is the first byte of the truncated ä character

        // Case 2: 3-byte character straddling boundary (70 ASCII + 3-byte char)
        String threeByteCase = "a".repeat(70) + "€"; // 70 + 3 = 73 bytes
        assertEquals(
                73, threeByteCase.getBytes(StandardCharsets.UTF_8).length, "Original: 73 bytes");

        Password threeBytePwd = new Password(threeByteCase);
        assertEquals(72, threeBytePwd.getBytes().length, "Should truncate to 72 bytes");

        // Only first 2 bytes of the 3-byte character are kept
        byte[] threeByteBytes = threeBytePwd.getBytes();
        assertEquals((byte) 'a', threeByteBytes[69], "Byte 69 should be 'a'");
        // Bytes 70-71 are the first 2 bytes of the truncated € character

        // Case 3: 4-byte character straddling boundary (69 ASCII + 4-byte char)
        String fourByteCase = "a".repeat(69) + "𝔸"; // 69 + 4 = 73 bytes
        assertEquals(
                73, fourByteCase.getBytes(StandardCharsets.UTF_8).length, "Original: 73 bytes");

        Password fourBytePwd = new Password(fourByteCase);
        assertEquals(72, fourBytePwd.getBytes().length, "Should truncate to 72 bytes");

        // Only first 3 bytes of the 4-byte character are kept
        byte[] fourByteBytes = fourBytePwd.getBytes();
        assertEquals((byte) 'a', fourByteBytes[68], "Byte 68 should be 'a'");
        // Bytes 69-71 are the first 3 bytes of the truncated 𝔸 character

        // All passwords should hash and verify successfully despite truncation
        Hash hash1 = service.hash(twoBytePwd);
        Hash hash2 = service.hash(threeBytePwd);
        Hash hash3 = service.hash(fourBytePwd);

        assertTrue(service.verify(twoBytePwd, hash1), "Should verify 2-byte truncation case");
        assertTrue(service.verify(threeBytePwd, hash2), "Should verify 3-byte truncation case");
        assertTrue(service.verify(fourBytePwd, hash3), "Should verify 4-byte truncation case");

        // Verify that passwords with complete characters at boundary work too
        String exact72 = "a".repeat(72);
        Password exact72Pwd = new Password(exact72);
        assertEquals(72, exact72Pwd.getBytes().length, "Should be exactly 72 bytes");

        Hash exact72Hash = service.hash(exact72Pwd);
        assertTrue(service.verify(exact72Pwd, exact72Hash), "Should verify exact 72-byte password");
    }
}
