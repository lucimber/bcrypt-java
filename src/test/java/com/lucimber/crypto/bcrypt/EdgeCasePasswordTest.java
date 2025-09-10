package com.lucimber.crypto.bcrypt;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Edge case tests for password handling in BCrypt implementation.
 *
 * <p>Tests boundary conditions and unusual password inputs including: - Single byte and empty
 * passwords - Whitespace and control characters - Maximum length (72 bytes) and truncation behavior
 * - Null bytes and special characters - ASCII printable range and repeated patterns
 */
class EdgeCasePasswordTest {

    private final BCryptService service = BCryptService.getInstance();

    @Test
    @DisplayName("Should reject empty password")
    void shouldRejectEmptyPassword() {
        // BCrypt implementation should reject empty passwords for security
        assertThrows(
                IllegalArgumentException.class,
                () -> new Password(""),
                "Should reject empty password");

        // Null should also be rejected
        assertThrows(
                NullPointerException.class,
                () -> new Password((String) null),
                "Should reject null password");
    }

    @Test
    @DisplayName("Should handle single byte password")
    void shouldHandleSingleBytePassword() {
        // Test minimum non-empty password
        Password password = new Password("x");
        Hash hash = service.hash(password);

        assertTrue(service.verify(password, hash), "Should verify single byte password");
        assertFalse(
                service.verify(new Password("y"), hash), "Different single byte should not match");
        assertFalse(
                service.verify(new Password("X"), hash),
                "Case sensitive - uppercase should not match");
    }

    @Test
    @DisplayName("Should handle password with only whitespace")
    void shouldHandlePasswordWithOnlyWhitespace() {
        String[] whitespacePasswords = {" ", "  ", "\t", "\n", "\r", " \t\n\r ", "   \t\t\t   "};

        for (String pwd : whitespacePasswords) {
            Password password = new Password(pwd);
            Hash hash = service.hash(password);

            assertTrue(
                    service.verify(password, hash),
                    "Should verify whitespace password: '"
                            + pwd.replace("\n", "\\n").replace("\t", "\\t").replace("\r", "\\r")
                            + "'");
        }
    }

    @Test
    @DisplayName("Should handle password with null bytes")
    void shouldHandlePasswordWithNullBytes() {
        // Test null byte handling - BCrypt should process null bytes as regular characters
        // Note: Some BCrypt implementations treat null as terminator, but this implementation
        // doesn't
        char[] chars = {'p', 'a', 's', 's', '\0', 'w', 'o', 'r', 'd'};
        Password password = new Password(chars);
        Hash hash = service.hash(password);

        assertTrue(service.verify(password, hash), "Should verify password with null byte");

        // Password without null should not match
        Password withoutNull = new Password("password");
        assertFalse(
                service.verify(withoutNull, hash), "Password without null byte should not match");

        // Password with null at different position should not match
        char[] nullAtEnd = {'p', 'a', 's', 's', 'w', 'o', 'r', 'd', '\0'};
        Password differentNull = new Password(nullAtEnd);
        assertFalse(
                service.verify(differentNull, hash), "Null at different position should not match");
    }

    @Test
    @DisplayName("Should handle all ASCII printable characters")
    void shouldHandleAllAsciiPrintableCharacters() {
        // All printable ASCII from space (32) to tilde (126)
        // This includes: !"#$%&'()*+,-./0-9:;<=>?@A-Z[\]^_`a-z{|}~
        StringBuilder sb = new StringBuilder();
        for (char c = 32; c <= 126; c++) {
            sb.append(c);
        }
        String allAscii = sb.toString();
        assertEquals(95, allAscii.length(), "Should have all 95 printable ASCII characters");

        Password password = new Password(allAscii);
        Hash hash = service.hash(password);

        assertTrue(service.verify(password, hash), "Should handle all ASCII printable characters");

        // Verify it's truncated at 72 bytes (all ASCII chars are 1 byte each)
        byte[] bytes = password.getBytes();
        assertEquals(72, bytes.length, "Should be truncated to 72 bytes");
    }

    @Test
    @DisplayName("Should handle control characters")
    void shouldHandleControlCharacters() {
        // Test various control characters
        String[] controlPasswords = {
            "pass\0word", // Null
            "pass\bword", // Backspace
            "pass\fword", // Form feed
            "pass\u0001word", // Start of heading
            "pass\u001Fword" // Unit separator
        };

        for (String pwd : controlPasswords) {
            Password password = new Password(pwd);
            Hash hash = service.hash(password);

            assertTrue(
                    service.verify(password, hash), "Should handle control character in: " + pwd);
        }
    }

    @Test
    @DisplayName("Should handle repeated characters at max length")
    void shouldHandleRepeatedCharactersAtMaxLength() {
        // Test various repetitive patterns at exactly 72 characters
        // These patterns might expose issues with internal state or buffer handling
        String[] patterns = {
            "A".repeat(72), // Single character repeated
            "AB".repeat(36), // Two character pattern
            "ABC".repeat(24), // Three character pattern
            "ABCD".repeat(18), // Four character pattern
            "ABCDEFGH".repeat(9), // Eight character pattern
            "0123456789".repeat(7) + "01" // Numeric pattern
        };

        for (String pattern : patterns) {
            assertEquals(72, pattern.length(), "Pattern should be exactly 72 characters");
            Password password = new Password(pattern);
            assertEquals(72, password.getBytes().length, "Should use exactly 72 bytes");

            Hash hash = service.hash(password);

            assertTrue(
                    service.verify(password, hash),
                    "Should handle pattern: "
                            + pattern.substring(0, Math.min(20, pattern.length()))
                            + "...");

            // Verify that adding chars beyond 72 doesn't change the hash
            Password extended = new Password(pattern + "EXTRA");
            assertTrue(
                    service.verify(extended, hash),
                    "Extended pattern should match due to truncation");
        }
    }

    @Test
    @DisplayName("Should handle passwords differing only in truncated portion")
    void shouldHandlePasswordsDifferingOnlyInTruncatedPortion() {
        // BCrypt truncates at 72 bytes - passwords differing only after byte 72 are equivalent
        String base = "a".repeat(72);
        String password1 = base + "different1";
        String password2 = base + "different2";
        String password3 = base + "completely_different_ending_with_special_chars!@#$%";

        Password pwd1 = new Password(password1);
        Password pwd2 = new Password(password2);
        Password pwd3 = new Password(password3);

        // All passwords should have same 72-byte prefix
        assertArrayEquals(pwd1.getBytes(), pwd2.getBytes(), "Truncated bytes should be identical");
        assertArrayEquals(pwd1.getBytes(), pwd3.getBytes(), "Truncated bytes should be identical");

        Hash hash1 = service.hash(pwd1);

        // All should verify against hash1 due to truncation
        assertTrue(service.verify(pwd1, hash1), "Original password should verify");
        assertTrue(service.verify(pwd2, hash1), "Different suffix should still verify");
        assertTrue(service.verify(pwd3, hash1), "Very different suffix should still verify");

        // When using same salt, hashes should be identical
        Salt salt = Salt.generateRandom();
        CostFactor cost = new CostFactor(10);
        Hash hashWithSalt1 = service.hash(pwd1, BCryptVersion.VERSION_2B, cost, salt);
        Hash hashWithSalt2 = service.hash(pwd2, BCryptVersion.VERSION_2B, cost, salt);
        Hash hashWithSalt3 = service.hash(pwd3, BCryptVersion.VERSION_2B, cost, salt);

        assertEquals(
                hashWithSalt1.getValue(),
                hashWithSalt2.getValue(),
                "Hashes with same salt should be identical for truncated passwords");
        assertEquals(
                hashWithSalt1.getValue(),
                hashWithSalt3.getValue(),
                "Hashes with same salt should be identical for truncated passwords");
    }

    @Test
    @DisplayName("Should handle special characters and symbols")
    void shouldHandleSpecialCharactersAndSymbols() {
        // Test various special characters and symbols
        String[] specialPasswords = {
            "!@#$%^&*()", "<>?:{}[]|\\", "'\"`;,./", "±§£€¥¢", "©®™✓✗", "password!@#123"
        };

        for (String special : specialPasswords) {
            Password password = new Password(special);
            Hash hash = service.hash(password);

            assertTrue(
                    service.verify(password, hash), "Should handle special characters: " + special);

            // Verify case sensitivity with special chars
            if (special.matches(".*[a-zA-Z].*")) {
                Password modified = new Password(special.toUpperCase());
                assertFalse(
                        service.verify(modified, hash),
                        "Should be case sensitive even with special chars");
            }
        }
    }
}
