package com.lucimber.crypto.bcrypt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Edge case tests for password handling in BCrypt implementation.
 * Tests boundary conditions and unusual password inputs.
 */
class EdgeCasePasswordTest {
    
    private final BCryptService service = BCryptService.getInstance();
    
    @Test
    @DisplayName("Should handle single byte password")
    void shouldHandleSingleBytePassword() {
        Password password = new Password("x");
        Hash hash = service.hash(password);
        
        assertTrue(service.verify(password, hash));
        assertFalse(service.verify(new Password("y"), hash));
    }
    
    @Test
    @DisplayName("Should handle password with only whitespace")
    void shouldHandlePasswordWithOnlyWhitespace() {
        String[] whitespacePasswords = {
            " ",
            "  ",
            "\t",
            "\n",
            "\r",
            " \t\n\r ",
            "   \t\t\t   "
        };
        
        for (String pwd : whitespacePasswords) {
            Password password = new Password(pwd);
            Hash hash = service.hash(password);
            
            assertTrue(service.verify(password, hash),
                "Should verify whitespace password: '" + pwd.replace("\n", "\\n")
                    .replace("\t", "\\t").replace("\r", "\\r") + "'");
        }
    }
    
    @Test
    @DisplayName("Should handle password with null bytes")
    void shouldHandlePasswordWithNullBytes() {
        // BCrypt treats null byte as terminator in some implementations
        char[] chars = {'p', 'a', 's', 's', '\0', 'w', 'o', 'r', 'd'};
        Password password = new Password(chars);
        Hash hash = service.hash(password);
        
        assertTrue(service.verify(password, hash));
        
        // Password without null should not match
        Password withoutNull = new Password("password");
        assertFalse(service.verify(withoutNull, hash));
    }
    
    @Test
    @DisplayName("Should handle all ASCII printable characters")
    void shouldHandleAllAsciiPrintableCharacters() {
        // All printable ASCII from space to ~
        StringBuilder sb = new StringBuilder();
        for (char c = 32; c <= 126; c++) {
            sb.append(c);
        }
        String allAscii = sb.toString();
        
        Password password = new Password(allAscii);
        Hash hash = service.hash(password);
        
        assertTrue(service.verify(password, hash));
    }
    
    @Test
    @DisplayName("Should handle control characters")
    void shouldHandleControlCharacters() {
        // Test various control characters
        String[] controlPasswords = {
            "pass\0word",     // Null
            "pass\bword",     // Backspace
            "pass\fword",     // Form feed
            "pass\u0001word", // Start of heading
            "pass\u001Fword"  // Unit separator
        };
        
        for (String pwd : controlPasswords) {
            Password password = new Password(pwd);
            Hash hash = service.hash(password);
            
            assertTrue(service.verify(password, hash),
                "Should handle control character in: " + pwd);
        }
    }
    
    @Test
    @DisplayName("Should handle repeated characters at max length")
    void shouldHandleRepeatedCharactersAtMaxLength() {
        // Test patterns that might expose issues
        String[] patterns = {
            "A".repeat(72),
            "AB".repeat(36),
            "ABC".repeat(24),
            "ABCD".repeat(18),
            "ABCDEFGH".repeat(9),
            "0123456789".repeat(7) + "01"
        };
        
        for (String pattern : patterns) {
            assertEquals(72, pattern.length());
            Password password = new Password(pattern);
            Hash hash = service.hash(password);
            
            assertTrue(service.verify(password, hash),
                "Should handle pattern: " + pattern.substring(0, 20) + "...");
        }
    }
    
    @Test
    @DisplayName("Should handle passwords differing only in truncated portion")
    void shouldHandlePasswordsDifferingOnlyInTruncatedPortion() {
        // Two passwords that differ only after byte 72
        String base = "a".repeat(72);
        String password1 = base + "different1";
        String password2 = base + "different2";
        
        Password pwd1 = new Password(password1);
        Password pwd2 = new Password(password2);
        
        Hash hash1 = service.hash(pwd1);
        
        // Both should verify against hash1 due to truncation
        assertTrue(service.verify(pwd1, hash1));
        assertTrue(service.verify(pwd2, hash1));
        
        // Hashes with same salt should be identical
        Salt salt = Salt.generateRandom();
        Hash hashWithSalt1 = service.hash(pwd1, BCryptVersion.VERSION_2B, new CostFactor(10), salt);
        Hash hashWithSalt2 = service.hash(pwd2, BCryptVersion.VERSION_2B, new CostFactor(10), salt);
        
        assertEquals(hashWithSalt1.getValue(), hashWithSalt2.getValue());
    }
}