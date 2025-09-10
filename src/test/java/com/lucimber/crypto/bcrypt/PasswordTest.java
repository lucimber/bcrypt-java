package com.lucimber.crypto.bcrypt;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PasswordTest {

    @Test
    @DisplayName("Should create password from char array")
    void shouldCreatePasswordFromCharArray() {
        char[] chars = "testPassword123".toCharArray();
        Password password = new Password(chars);

        assertNotNull(password);
        assertArrayEquals(chars, password.toCharArray());
    }

    @Test
    @DisplayName("Should create password from string")
    void shouldCreatePasswordFromString() {
        String passwordString = "testPassword123";
        Password password = new Password(passwordString);

        assertNotNull(password);
        assertArrayEquals(passwordString.toCharArray(), password.toCharArray());
    }

    @Test
    @DisplayName("Should throw exception for null char array")
    void shouldThrowExceptionForNullCharArray() {
        assertThrows(NullPointerException.class, () -> new Password((char[]) null));
    }

    @Test
    @DisplayName("Should throw exception for null string")
    void shouldThrowExceptionForNullString() {
        assertThrows(NullPointerException.class, () -> new Password((String) null));
    }

    @Test
    @DisplayName("Should throw exception for empty password")
    void shouldThrowExceptionForEmptyPassword() {
        assertThrows(IllegalArgumentException.class, () -> new Password(new char[0]));
        assertThrows(IllegalArgumentException.class, () -> new Password(""));
    }

    @Test
    @DisplayName("Should accept maximum length password")
    void shouldAcceptMaxLengthPassword() {
        // BCrypt max is 72 bytes
        String maxPassword = "a".repeat(72);
        Password password = new Password(maxPassword);

        assertNotNull(password);
        assertEquals(72, password.getBytes().length);
    }

    @Test
    @DisplayName("Should accept and truncate password exceeding max length")
    void shouldAcceptAndTruncatePasswordExceedingMaxLength() {
        // 100 ASCII characters = 100 bytes (will be truncated to 72)
        String tooLongPassword = "a".repeat(100);
        String expectedTruncated = "a".repeat(72);

        Password password = new Password(tooLongPassword);
        byte[] bytes = password.getBytes();

        assertEquals(72, bytes.length, "Password should be truncated to 72 bytes");
        assertEquals(expectedTruncated, new String(bytes, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Should handle UTF-8 characters correctly")
    void shouldHandleUtf8CharactersCorrectly() {
        String utf8Password = "пароль密码"; // Russian and Chinese characters
        Password password = new Password(utf8Password);

        byte[] bytes = password.getBytes();
        String reconstructed = new String(bytes, StandardCharsets.UTF_8);
        assertEquals(utf8Password, reconstructed);
    }

    @Test
    @DisplayName("Should accept and truncate UTF-8 password exceeding byte limit")
    void shouldAcceptAndTruncateUtf8PasswordExceedingByteLimit() {
        // Each emoji is typically 4 bytes in UTF-8
        String emojiPassword = "😀".repeat(19); // 19 * 4 = 76 bytes > 72

        Password password = new Password(emojiPassword);
        byte[] bytes = password.getBytes();

        assertEquals(72, bytes.length, "Password should be truncated to 72 bytes");
        // After truncation, we should have exactly 18 complete emojis (18 * 4 = 72)
        String truncated = new String(bytes, StandardCharsets.UTF_8);
        assertEquals("😀".repeat(18), truncated);
    }

    @Test
    @DisplayName("Should clear password from memory")
    void shouldClearPasswordFromMemory() {
        char[] chars = "sensitivePassword".toCharArray();
        Password password = new Password(chars);

        password.clear();

        char[] clearedChars = password.toCharArray();
        for (char c : clearedChars) {
            assertEquals('\0', c);
        }
    }

    @Test
    @DisplayName("Should create defensive copy of char array")
    void shouldCreateDefensiveCopyOfCharArray() {
        char[] original = "password".toCharArray();
        Password password = new Password(original);

        // Modify original array
        original[0] = 'X';

        // Password should not be affected
        assertArrayEquals("password".toCharArray(), password.toCharArray());
    }

    @Test
    @DisplayName("Should return defensive copy from toCharArray")
    void shouldReturnDefensiveCopyFromToCharArray() {
        Password password = new Password("password");
        char[] retrieved = password.toCharArray();

        // Modify retrieved array
        retrieved[0] = 'X';

        // Password should not be affected
        assertArrayEquals("password".toCharArray(), password.toCharArray());
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void shouldImplementEqualsCorrectly() {
        Password password1 = new Password("samePassword");
        Password password2 = new Password("samePassword");
        Password password3 = new Password("differentPassword");

        assertEquals(password1, password2);
        assertNotEquals(password1, password3);
        assertEquals(password1, password1); // reflexive
        assertNotEquals(password1, null);
        assertNotEquals(password1, "not a password object");
    }

    @Test
    @DisplayName("Should implement hashCode consistently")
    void shouldImplementHashCodeConsistently() {
        Password password1 = new Password("samePassword");
        Password password2 = new Password("samePassword");

        assertEquals(password1.hashCode(), password2.hashCode());
    }

    @Test
    @DisplayName("Should provide secure toString")
    void shouldProvideSecureToString() {
        Password password = new Password("secretPassword123");
        String toString = password.toString();

        assertFalse(toString.contains("secretPassword123"));
        assertTrue(toString.contains("length="));
    }
}
