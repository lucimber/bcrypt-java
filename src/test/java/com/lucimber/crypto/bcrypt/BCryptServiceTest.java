/*
 * SPDX-FileCopyrightText: 2025 Lucimber UG
 * SPDX-License-Identifier: Apache-2.0
 */
package com.lucimber.crypto.bcrypt;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BCryptServiceTest {

    private final BCryptService service = BCryptService.getInstance();

    @Test
    @DisplayName("Should be singleton")
    void shouldBeSingleton() {
        BCryptService instance1 = BCryptService.getInstance();
        BCryptService instance2 = BCryptService.getInstance();

        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("Should hash password with default settings")
    void shouldHashPasswordWithDefaultSettings() {
        Password password = new Password("mySecretPassword123");

        Hash hash = service.hash(password);

        assertNotNull(hash);
        assertEquals(BCryptVersion.VERSION_2B, hash.getVersion());
        assertEquals(CostFactor.DEFAULT_COST, hash.getCostFactor().getValue());
    }

    @Test
    @DisplayName("Should hash password with specified version")
    void shouldHashPasswordWithSpecifiedVersion() {
        Password password = new Password("mySecretPassword123");

        Hash hash2a = service.hash(password, BCryptVersion.VERSION_2A);
        Hash hash2b = service.hash(password, BCryptVersion.VERSION_2B);

        assertEquals(BCryptVersion.VERSION_2A, hash2a.getVersion());
        assertEquals(BCryptVersion.VERSION_2B, hash2b.getVersion());
    }

    @Test
    @DisplayName("Should hash password with specified cost factor")
    void shouldHashPasswordWithSpecifiedCostFactor() {
        Password password = new Password("mySecretPassword123");
        CostFactor costFactor = new CostFactor(12);

        Hash hash = service.hash(password, BCryptVersion.VERSION_2B, costFactor);

        assertEquals(12, hash.getCostFactor().getValue());
    }

    @Test
    @DisplayName("Should hash password with specified salt")
    void shouldHashPasswordWithSpecifiedSalt() {
        Password password = new Password("mySecretPassword123");
        Salt salt = Salt.generateRandom();
        CostFactor costFactor = new CostFactor(10);

        Hash hash1 = service.hash(password, BCryptVersion.VERSION_2B, costFactor, salt);
        Hash hash2 = service.hash(password, BCryptVersion.VERSION_2B, costFactor, salt);

        // Same password and salt should produce same hash
        assertEquals(hash1.getValue(), hash2.getValue());
    }

    @Test
    @DisplayName("Should generate different hashes for same password with different salts")
    void shouldGenerateDifferentHashesForSamePasswordWithDifferentSalts() {
        Password password = new Password("mySecretPassword123");

        Hash hash1 = service.hash(password);
        Hash hash2 = service.hash(password);

        // Different salts should produce different hashes
        assertNotEquals(hash1.getValue(), hash2.getValue());
    }

    @Test
    @DisplayName("Should verify correct password")
    void shouldVerifyCorrectPassword() {
        Password password = new Password("correctPassword123");
        Hash hash = service.hash(password);

        assertTrue(service.verify(password, hash));
    }

    @Test
    @DisplayName("Should reject incorrect password")
    void shouldRejectIncorrectPassword() {
        Password correctPassword = new Password("correctPassword123");
        Password wrongPassword = new Password("wrongPassword123");
        Hash hash = service.hash(correctPassword);

        assertFalse(service.verify(wrongPassword, hash));
    }

    @Test
    @DisplayName("Should verify password with 2a hash")
    void shouldVerifyPasswordWith2aHash() {
        // This is a real BCrypt 2a hash of "password"
        String knownHash = "$2a$10$VIhIOofSMqgdGlL4wzE//e3QH8vRzZP6CDZ1NtJOmsvCfGnYHnYGi";
        Hash hash = new Hash(knownHash);
        Password password = new Password("password");

        assertTrue(service.verify(password, hash));
    }

    @Test
    @DisplayName("Should verify password with 2b hash")
    void shouldVerifyPasswordWith2bHash() {
        // This is a real BCrypt 2b hash of "password"
        String knownHash = "$2b$10$WvmhqkA7x0WrsDnGWqhvLu5B6mugeHdCxRVT6CYPKiFVN1sQ97eCO";
        Hash hash = new Hash(knownHash);
        Password password = new Password("password");

        assertTrue(service.verify(password, hash));
    }

    @Test
    @DisplayName("Should handle UTF-8 passwords correctly")
    void shouldHandleUtf8PasswordsCorrectly() {
        Password password = new Password("пароль密码パスワード");

        Hash hash = service.hash(password);

        assertTrue(service.verify(password, hash));
    }

    @Test
    @DisplayName("Should handle maximum length password")
    void shouldHandleMaximumLengthPassword() {
        String maxPassword = "a".repeat(72);
        Password password = new Password(maxPassword);

        Hash hash = service.hash(password);

        assertTrue(service.verify(password, hash));
    }

    @Test
    @DisplayName("Should truncate password longer than 72 bytes")
    void shouldTruncatePasswordLongerThan72Bytes() {
        // BCrypt truncates at 72 bytes
        String password72 = "a".repeat(72);
        String password73 = "a".repeat(73);

        Password pwd72 = new Password(password72);
        Password pwd73 = new Password(password73);

        Hash hash72 = service.hash(pwd72);

        // Password with 73 'a's should match hash of 72 'a's due to truncation
        assertTrue(service.verify(pwd73, hash72));
    }

    @Test
    @DisplayName("Should handle special characters in password")
    void shouldHandleSpecialCharactersInPassword() {
        Password password = new Password("P@ssw0rd!#$%^&*()_+-=[]{}|;:',.<>?/`~");

        Hash hash = service.hash(password);

        assertTrue(service.verify(password, hash));
    }

    @Test
    @DisplayName("Should handle different cost factors correctly")
    void shouldHandleDifferentCostFactorsCorrectly() {
        Password password = new Password("testPassword");

        // Test minimum and higher cost factors
        CostFactor minCost = new CostFactor(4);
        CostFactor highCost = new CostFactor(12);

        Hash hashMin = service.hash(password, BCryptVersion.VERSION_2B, minCost);
        Hash hashHigh = service.hash(password, BCryptVersion.VERSION_2B, highCost);

        assertEquals(4, hashMin.getCostFactor().getValue());
        assertEquals(12, hashHigh.getCostFactor().getValue());

        assertTrue(service.verify(password, hashMin));
        assertTrue(service.verify(password, hashHigh));
    }
}
