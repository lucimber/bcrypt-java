/*
 * SPDX-FileCopyrightText: 2025 Lucimber UG
 * SPDX-License-Identifier: Apache-2.0
 */
package com.lucimber.crypto.bcrypt;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HashTest {

    // Valid BCrypt hash examples
    private static final String VALID_2A_HASH =
            "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
    private static final String VALID_2B_HASH =
            "$2b$12$EXRkfkdmXnagzds2SSitu.MW9.gAVqa9eLS1//RYtYCmB1eLHg.9q";

    @Test
    @DisplayName("Should parse valid 2a hash")
    void shouldParseValid2aHash() {
        Hash hash = new Hash(VALID_2A_HASH);

        assertNotNull(hash);
        assertEquals(BCryptVersion.VERSION_2A, hash.getVersion());
        assertEquals(10, hash.getCostFactor().getValue());
        assertEquals(VALID_2A_HASH, hash.getValue());
    }

    @Test
    @DisplayName("Should parse valid 2b hash")
    void shouldParseValid2bHash() {
        Hash hash = new Hash(VALID_2B_HASH);

        assertNotNull(hash);
        assertEquals(BCryptVersion.VERSION_2B, hash.getVersion());
        assertEquals(12, hash.getCostFactor().getValue());
        assertEquals(VALID_2B_HASH, hash.getValue());
    }

    @Test
    @DisplayName("Should extract salt from hash")
    void shouldExtractSaltFromHash() {
        Hash hash = new Hash(VALID_2A_HASH);
        String salt = hash.getSalt();

        assertNotNull(salt);
        assertEquals(22, salt.length());
        assertEquals("N9qo8uLOickgx2ZMRZoMye", salt);
    }

    @Test
    @DisplayName("Should extract hash portion from hash")
    void shouldExtractHashPortionFromHash() {
        Hash hash = new Hash(VALID_2A_HASH);
        String hashPortion = hash.getHashPortion();

        assertNotNull(hashPortion);
        assertEquals(31, hashPortion.length());
        assertEquals("IjZAgcfl7p92ldGxad68LJZdL17lhWy", hashPortion);
    }

    @Test
    @DisplayName("Should create hash from components")
    void shouldCreateHashFromComponents() {
        BCryptVersion version = BCryptVersion.VERSION_2B;
        CostFactor costFactor = new CostFactor(10);
        String saltAndHash = "N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

        Hash hash = new Hash(version, costFactor, saltAndHash);

        assertEquals(
                "$2b$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy", hash.getValue());
        assertEquals(version, hash.getVersion());
        assertEquals(costFactor, hash.getCostFactor());
    }

    @Test
    @DisplayName("Should throw exception for null hash string")
    void shouldThrowExceptionForNullHashString() {
        assertThrows(NullPointerException.class, () -> new Hash((String) null));
    }

    @Test
    @DisplayName("Should throw exception for invalid hash format")
    void shouldThrowExceptionForInvalidHashFormat() {
        // Missing $ prefix
        assertThrows(
                IllegalArgumentException.class,
                () -> new Hash("2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"));

        // Invalid version
        assertThrows(
                IllegalArgumentException.class,
                () -> new Hash("$2c$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"));

        // Invalid cost factor format
        assertThrows(
                IllegalArgumentException.class,
                () -> new Hash("$2a$1$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"));

        // Too short salt and hash
        assertThrows(IllegalArgumentException.class, () -> new Hash("$2a$10$tooShort"));

        // Too long salt and hash
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new Hash(
                                "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWyExtra"));
    }

    @Test
    @DisplayName("Should throw exception for null components")
    void shouldThrowExceptionForNullComponents() {
        BCryptVersion version = BCryptVersion.VERSION_2A;
        CostFactor costFactor = new CostFactor(10);
        String saltAndHash = "N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

        assertThrows(NullPointerException.class, () -> new Hash(null, costFactor, saltAndHash));
        assertThrows(NullPointerException.class, () -> new Hash(version, null, saltAndHash));
        assertThrows(NullPointerException.class, () -> new Hash(version, costFactor, null));
    }

    @Test
    @DisplayName("Should throw exception for incorrect saltAndHash length")
    void shouldThrowExceptionForIncorrectSaltAndHashLength() {
        BCryptVersion version = BCryptVersion.VERSION_2A;
        CostFactor costFactor = new CostFactor(10);

        assertThrows(
                IllegalArgumentException.class, () -> new Hash(version, costFactor, "tooShort"));
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new Hash(
                                version,
                                costFactor,
                                "N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWyTooLong"));
    }

    @Test
    @DisplayName("Should handle minimum cost factor")
    void shouldHandleMinimumCostFactor() {
        String minCostHash = "$2a$04$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        Hash hash = new Hash(minCostHash);

        assertEquals(4, hash.getCostFactor().getValue());
    }

    @Test
    @DisplayName("Should handle maximum cost factor")
    void shouldHandleMaximumCostFactor() {
        String maxCostHash = "$2b$31$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        Hash hash = new Hash(maxCostHash);

        assertEquals(31, hash.getCostFactor().getValue());
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void shouldImplementEqualsCorrectly() {
        Hash hash1 = new Hash(VALID_2A_HASH);
        Hash hash2 = new Hash(VALID_2A_HASH);
        Hash hash3 = new Hash(VALID_2B_HASH);

        assertEquals(hash1, hash2);
        assertNotEquals(hash1, hash3);
        assertEquals(hash1, hash1); // reflexive
        assertNotEquals(hash1, null);
        assertNotEquals(hash1, "not a hash object");
    }

    @Test
    @DisplayName("Should implement hashCode consistently")
    void shouldImplementHashCodeConsistently() {
        Hash hash1 = new Hash(VALID_2A_HASH);
        Hash hash2 = new Hash(VALID_2A_HASH);

        assertEquals(hash1.hashCode(), hash2.hashCode());
    }

    @Test
    @DisplayName("Should return hash string as toString")
    void shouldReturnHashStringAsToString() {
        Hash hash = new Hash(VALID_2A_HASH);

        assertEquals(VALID_2A_HASH, hash.toString());
    }
}
