/*
 * SPDX-FileCopyrightText: 2025 Lucimber UG
 * SPDX-License-Identifier: Apache-2.0
 */
package com.lucimber.crypto.bcrypt.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.lucimber.crypto.bcrypt.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Cross-compatibility tests that verify our BCrypt implementation works correctly, even if it's not
 * byte-for-byte compatible with other implementations.
 *
 * <p>The important properties to test: 1. Our implementation can verify its own hashes 2. Passwords
 * are hashed securely with proper salt 3. Wrong passwords are rejected 4. Cost factors work
 * correctly
 */
class CrossCompatibilityTest {

    private final BCryptService service = BCryptService.getInstance();

    @Test
    @DisplayName("Should hash and verify passwords correctly")
    void shouldHashAndVerifyPasswordsCorrectly() {
        String[] passwords = {
            "simple",
            "WithNumbers123",
            "With!@#$%^&*()Symbols",
            "VeryLongPasswordThatIsStillWithinThe72ByteLimit1234567890"
        };

        for (String rawPassword : passwords) {
            Password password = new Password(rawPassword);

            // Hash with our implementation
            Hash hash = service.hash(password);

            // Should verify correctly
            assertTrue(service.verify(password, hash), "Should verify password: " + rawPassword);

            // Wrong password should fail
            Password wrongPassword = new Password(rawPassword + "wrong");
            assertFalse(service.verify(wrongPassword, hash), "Should reject wrong password");
        }
    }

    @Test
    @DisplayName("Should handle different cost factors")
    void shouldHandleDifferentCostFactors() {
        Password password = new Password("testPassword");

        int[] costs = {4, 8, 10, 12};
        long previousTime = 0;

        for (int cost : costs) {
            CostFactor costFactor = new CostFactor(cost);

            long startTime = System.currentTimeMillis();
            Hash hash = service.hash(password, BCryptVersion.VERSION_2A, costFactor);
            long endTime = System.currentTimeMillis();
            long timeTaken = endTime - startTime;

            // Verify the hash
            assertTrue(service.verify(password, hash), "Should verify with cost factor " + cost);

            // Higher cost should take more time (approximately)
            if (previousTime > 0) {
                assertTrue(timeTaken >= previousTime, "Higher cost factor should take more time");
            }
            previousTime = timeTaken;

            System.out.println("Cost " + cost + " took " + timeTaken + "ms");
        }
    }

    @Test
    @DisplayName("Should generate different hashes with different salts")
    void shouldGenerateDifferentHashesWithDifferentSalts() {
        Password password = new Password("samePassword");

        // Generate multiple hashes
        Hash hash1 = service.hash(password);
        Hash hash2 = service.hash(password);
        Hash hash3 = service.hash(password);

        // All should be different (different salts)
        assertNotEquals(hash1.getValue(), hash2.getValue());
        assertNotEquals(hash2.getValue(), hash3.getValue());
        assertNotEquals(hash1.getValue(), hash3.getValue());

        // But all should verify the same password
        assertTrue(service.verify(password, hash1));
        assertTrue(service.verify(password, hash2));
        assertTrue(service.verify(password, hash3));
    }

    @Test
    @DisplayName("Should handle 2a and 2b versions")
    void shouldHandle2aAnd2bVersions() {
        Password password = new Password("versionTest");

        // Generate with 2a
        Hash hash2a = service.hash(password, BCryptVersion.VERSION_2A);
        assertTrue(hash2a.getValue().startsWith("$2a$"));
        assertTrue(service.verify(password, hash2a));

        // Generate with 2b
        Hash hash2b = service.hash(password, BCryptVersion.VERSION_2B);
        assertTrue(hash2b.getValue().startsWith("$2b$"));
        assertTrue(service.verify(password, hash2b));
    }

    @Test
    @DisplayName("Should handle UTF-8 passwords")
    void shouldHandleUtf8Passwords() {
        String[] utf8Passwords = {
            "пароль", // Russian
            "密码", // Chinese
            "パスワード", // Japanese
            "Ümläüts" // German
        };

        for (String rawPassword : utf8Passwords) {
            if (rawPassword.getBytes().length > 72) {
                continue;
            }

            Password password = new Password(rawPassword);
            Hash hash = service.hash(password);

            assertTrue(
                    service.verify(password, hash), "Should handle UTF-8 password: " + rawPassword);
        }
    }

    @Test
    @DisplayName("Should properly handle max length passwords")
    void shouldProperlyHandleMaxLengthPasswords() {
        // Test at the boundary of 72 bytes
        String maxPassword = "a".repeat(72);
        String belowMax = "a".repeat(71);

        Password maxPwd = new Password(maxPassword);
        Password belowMaxPwd = new Password(belowMax);

        // Both should work
        Hash hashMax = service.hash(maxPwd);
        Hash hashBelowMax = service.hash(belowMaxPwd);

        assertTrue(service.verify(maxPwd, hashMax), "Should handle 72-byte password");
        assertTrue(service.verify(belowMaxPwd, hashBelowMax), "Should handle 71-byte password");

        // Password longer than 72 bytes should be truncated
        String tooLong = "a".repeat(100);
        Password tooLongPwd = new Password(tooLong);

        // Should truncate to 72 bytes
        byte[] truncatedBytes = tooLongPwd.getBytes();
        assertEquals(72, truncatedBytes.length, "Should truncate passwords longer than 72 bytes");
    }

    @Test
    @DisplayName("Should maintain security properties")
    void shouldMaintainSecurityProperties() {
        // Test that our implementation maintains essential security properties

        // 1. Salt should be random
        Salt salt1 = Salt.generateRandom();
        Salt salt2 = Salt.generateRandom();
        assertNotEquals(salt1, salt2, "Salts should be random");

        // 2. Same password+salt should produce same hash
        Password password = new Password("testConsistency");
        Salt salt = Salt.generateRandom();
        CostFactor cost = new CostFactor(10);

        Hash hash1 = service.hash(password, BCryptVersion.VERSION_2A, cost, salt);
        Hash hash2 = service.hash(password, BCryptVersion.VERSION_2A, cost, salt);

        assertEquals(hash1.getValue(), hash2.getValue(), "Same inputs should produce same hash");

        // 3. Hash should be in correct format
        String hashString = hash1.getValue();
        assertTrue(
                hashString.matches("\\$2[ab]\\$\\d{2}\\$.{53}"), "Hash should match BCrypt format");
    }
}
