package com.lucimber.crypto.bcrypt;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Edge case tests for hash validation and malformed inputs in BCrypt implementation. Tests error
 * handling and hash format validation.
 */
class EdgeCaseHashTest {

    private final BCryptService service = BCryptService.getInstance();

    @Test
    @DisplayName("Should handle malformed hash strings gracefully")
    void shouldHandleMalformedHashStringsGracefully() {
        // Various malformed hashes
        String[] malformedHashes = {
            "$2a$10$", // Too short
            "$2a$10$tooShort", // Salt too short
            "$2x$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy", // Invalid version
            "$2a$99$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy", // Invalid cost
            "$2a$1$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy", // Cost too low
            "2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy", // Missing $
            "$2a10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy", // Missing $
            "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWyEXTRA", // Too long
            "", // Empty string
            "not a hash at all", // Random string
            "$1a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy", // Wrong version prefix
            "$2a$ab$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy", // Non-numeric cost
        };

        for (String malformed : malformedHashes) {
            assertThrows(
                    Exception.class,
                    () -> new Hash(malformed),
                    "Should reject malformed hash: " + malformed);
        }
    }

    @Test
    @DisplayName("Should reject verify with malformed hash")
    void shouldRejectVerifyWithMalformedHash() {
        Password password = new Password("test");

        // Try to verify with various invalid hashes
        String[] invalidHashes = {
            "$2a$10$invalid", "$2x$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy", ""
        };

        for (String invalid : invalidHashes) {
            // Should either throw exception when creating Hash or return false on verify
            try {
                Hash hash = new Hash(invalid);
                assertFalse(
                        service.verify(password, hash),
                        "Should not verify with invalid hash: " + invalid);
            } catch (Exception e) {
                // Expected - invalid hash format
                assertTrue(
                        e instanceof IllegalArgumentException || e instanceof NullPointerException,
                        "Should throw appropriate exception for: " + invalid);
            }
        }
    }

    @Test
    @DisplayName("Should handle edge case cost factors in hash string")
    void shouldHandleEdgeCaseCostFactorsInHashString() {
        // Test parsing of minimum cost factor
        String minCostHash = "$2a$04$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        Hash hashMin = new Hash(minCostHash);
        assertEquals(4, hashMin.getCostFactor().getValue());

        // Test parsing of maximum cost factor
        String maxCostHash = "$2a$31$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        Hash hashMax = new Hash(maxCostHash);
        assertEquals(31, hashMax.getCostFactor().getValue());

        // Test single digit cost factor
        String singleDigitHash = "$2a$09$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        Hash hashSingle = new Hash(singleDigitHash);
        assertEquals(9, hashSingle.getCostFactor().getValue());
    }

    @Test
    @DisplayName("Should extract components correctly from valid hash")
    void shouldExtractComponentsCorrectlyFromValidHash() {
        // Use a real valid BCrypt hash for testing
        String testHash = "$2b$12$RZUqjFh0uTNJTVZmqJzxNu0dcdNlBcNrTpLmaLOYYpiGKwdNXLvbO";
        Hash hash = new Hash(testHash);

        // Check version
        assertEquals(BCryptVersion.VERSION_2B, hash.getVersion());

        // Check cost factor
        assertEquals(12, hash.getCostFactor().getValue());

        // Check salt (first 22 chars of the encoded part)
        String salt = hash.getSalt();
        assertEquals(22, salt.length());
        assertEquals("RZUqjFh0uTNJTVZmqJzxNu", salt);

        // Check hash portion (remaining 31 chars)
        String hashPortion = hash.getHashPortion();
        assertEquals(31, hashPortion.length());
        assertEquals("0dcdNlBcNrTpLmaLOYYpiGKwdNXLvbO", hashPortion);
    }

    @Test
    @DisplayName("Should handle version differences correctly")
    void shouldHandleVersionDifferencesCorrectly() {
        Password password = new Password("versionTest");

        // Create hashes with different versions
        Hash hash2a = service.hash(password, BCryptVersion.VERSION_2A);
        Hash hash2b = service.hash(password, BCryptVersion.VERSION_2B);

        // Verify version prefixes
        assertTrue(hash2a.getValue().startsWith("$2a$"));
        assertTrue(hash2b.getValue().startsWith("$2b$"));

        // Both should verify the password
        assertTrue(service.verify(password, hash2a));
        assertTrue(service.verify(password, hash2b));

        // Check that version is correctly extracted
        assertEquals(BCryptVersion.VERSION_2A, hash2a.getVersion());
        assertEquals(BCryptVersion.VERSION_2B, hash2b.getVersion());
    }

    @Test
    @DisplayName("Should validate salt and hash length in hash string")
    void shouldValidateSaltAndHashLengthInHashString() {
        // BCrypt format: $version$cost$[22 char salt][31 char hash]
        // Total after last $: 53 characters (22 salt + 31 hash)
        String validHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        Hash hash = new Hash(validHash);

        // Verify the salt and hash components
        assertEquals(22, hash.getSalt().length(), "Salt should be 22 characters");
        assertEquals(31, hash.getHashPortion().length(), "Hash portion should be 31 characters");

        // Extract the full encoded portion after cost factor
        String saltAndHash = validHash.substring(validHash.lastIndexOf('$') + 1);
        assertEquals(53, saltAndHash.length(), "Total salt+hash should be 53 characters");

        // Test various invalid lengths

        // 52 chars - one character too short
        String tooShortBy1 = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhW";
        assertThrows(
                IllegalArgumentException.class,
                () -> new Hash(tooShortBy1),
                "Should reject hash with 52 characters");

        // 54 chars - one character too long
        String tooLongBy1 = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWyX";
        assertThrows(
                IllegalArgumentException.class,
                () -> new Hash(tooLongBy1),
                "Should reject hash with 54 characters");

        // 40 chars - significantly too short (missing hash portion)
        String onlySaltPortion = "$2a$10$N9qo8uLOickgx2ZMRZoMye";
        assertThrows(
                IllegalArgumentException.class,
                () -> new Hash(onlySaltPortion),
                "Should reject hash with only salt portion");

        // 60 chars - significantly too long
        String wayTooLong = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWyEXTRAxtra";
        assertThrows(
                IllegalArgumentException.class,
                () -> new Hash(wayTooLong),
                "Should reject hash with extra characters");

        // Test edge case with valid BCrypt Base64 characters but wrong length
        String validCharsWrongLength =
                "$2a$10$abcdefghijklmnopqrstuABCDEFGHIJKLMNOPQRSTUVWXYZ01234";
        assertThrows(
                IllegalArgumentException.class,
                () -> new Hash(validCharsWrongLength),
                "Should reject hash with valid chars but wrong length");
    }
}
