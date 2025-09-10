package com.lucimber.crypto.bcrypt;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Edge case tests for salt handling and cost factors in BCrypt implementation.
 *
 * <p>Tests boundary conditions including: - Salt edge cases (all zeros, all 0xFF, patterns) - Cost
 * factor boundaries (4-31) and validation - Salt encoding/decoding with BCrypt Base64 -
 * Deterministic hashing with fixed salts - Performance scaling with cost factors - Version
 * differences with same salt
 */
class EdgeCaseSaltTest {

    private final BCryptService service = BCryptService.getInstance();

    @Test
    @DisplayName("Should handle zero bytes in salt")
    void shouldHandleZeroBytesInSalt() {
        // Salt with all zeros - tests edge case of minimum byte values
        byte[] zeroSalt = new byte[16];
        // Arrays.fill not needed - already initialized to zeros
        Salt salt = new Salt(zeroSalt);

        Password password = new Password("testWithZeroSalt");
        Hash hash = service.hash(password, BCryptVersion.VERSION_2A, new CostFactor(10), salt);

        assertTrue(service.verify(password, hash), "Should verify with all-zero salt");

        // Verify salt encoding works correctly with all zeros
        String saltString = salt.toBCryptString();
        assertEquals(22, saltString.length(), "BCrypt salt should always be 22 chars");

        // Verify decoding produces same salt
        Salt decoded = Salt.fromBCryptString(saltString);
        assertArrayEquals(zeroSalt, decoded.getBytes(), "Decoded salt should match original");

        // Verify deterministic hashing with zero salt
        Hash hash2 = service.hash(password, BCryptVersion.VERSION_2A, new CostFactor(10), salt);
        assertEquals(hash.getValue(), hash2.getValue(), "Same salt should produce same hash");
    }

    @Test
    @DisplayName("Should handle same salt producing same hash")
    void shouldHandleSameSaltProducingSameHash() {
        // BCrypt must be deterministic - same inputs produce same output
        Password password = new Password("consistentPassword");
        Salt salt = Salt.generateRandom();
        CostFactor cost = new CostFactor(10);

        // Hash multiple times with same parameters
        Hash hash1 = service.hash(password, BCryptVersion.VERSION_2A, cost, salt);
        Hash hash2 = service.hash(password, BCryptVersion.VERSION_2A, cost, salt);
        Hash hash3 = service.hash(password, BCryptVersion.VERSION_2A, cost, salt);

        // All should be identical with same version, salt, and cost
        assertEquals(hash1.getValue(), hash2.getValue(), "Same inputs should produce same hash");
        assertEquals(hash1.getValue(), hash3.getValue(), "Hash should be deterministic");

        // Different version should produce different hash
        Hash hash2b = service.hash(password, BCryptVersion.VERSION_2B, cost, salt);
        assertNotEquals(
                hash1.getValue(),
                hash2b.getValue(),
                "Different version should produce different hash");

        // But both versions should verify the password
        assertTrue(service.verify(password, hash1), "Should verify with 2a version");
        assertTrue(service.verify(password, hash2b), "Should verify with 2b version");

        // Different cost with same salt should also produce different hash
        Hash hashCost12 =
                service.hash(password, BCryptVersion.VERSION_2A, new CostFactor(12), salt);
        assertNotEquals(
                hash1.getValue(),
                hashCost12.getValue(),
                "Different cost should produce different hash");
        assertTrue(service.verify(password, hashCost12), "Should verify with different cost");
    }

    @Test
    @DisplayName("Should handle minimum and maximum cost factors")
    void shouldHandleMinAndMaxCostFactors() {
        Password password = new Password("testPassword");

        // Test minimum cost factor (4)
        CostFactor minCost = new CostFactor(4);
        Hash minHash = service.hash(password, BCryptVersion.VERSION_2B, minCost);
        assertTrue(service.verify(password, minHash), "Should verify with minimum cost factor");
        assertEquals(4, minHash.getCostFactor().getValue(), "Should preserve cost factor in hash");
        assertTrue(minHash.getValue().contains("$04$"), "Hash should contain cost factor 04");

        // Test maximum cost factor (31)
        CostFactor maxCost = new CostFactor(31);
        assertEquals(31, maxCost.getValue(), "Should accept maximum cost factor 31");

        // Note: Cost 31 would take extremely long (2^31 iterations)
        // We verify construction but skip actual hashing

        // Test various cost factors in valid range
        int[] testCosts = {4, 5, 10, 12, 15, 20, 30, 31};
        for (int costValue : testCosts) {
            CostFactor cost = new CostFactor(costValue);
            assertEquals(costValue, cost.getValue(), "Should create cost factor " + costValue);

            // Only hash with reasonable cost factors
            if (costValue <= 12) {
                Hash hash = service.hash(password, BCryptVersion.VERSION_2B, cost);
                assertTrue(service.verify(password, hash), "Should verify with cost " + costValue);
                assertEquals(
                        costValue,
                        hash.getCostFactor().getValue(),
                        "Should preserve cost " + costValue);

                String expectedCost = String.format("$%02d$", costValue);
                assertTrue(
                        hash.getValue().contains(expectedCost),
                        "Hash should contain cost " + expectedCost);
            }
        }
    }

    @Test
    @DisplayName("Should reject invalid cost factors")
    void shouldRejectInvalidCostFactors() {
        // BCrypt specification requires cost factor between 4 and 31 inclusive

        // Below minimum
        assertThrows(
                IllegalArgumentException.class,
                () -> new CostFactor(3),
                "Should reject cost factor below 4");
        assertThrows(
                IllegalArgumentException.class,
                () -> new CostFactor(0),
                "Should reject cost factor 0");
        assertThrows(
                IllegalArgumentException.class,
                () -> new CostFactor(-1),
                "Should reject negative cost factor");
        assertThrows(
                IllegalArgumentException.class,
                () -> new CostFactor(Integer.MIN_VALUE),
                "Should reject minimum integer value");

        // Above maximum
        assertThrows(
                IllegalArgumentException.class,
                () -> new CostFactor(32),
                "Should reject cost factor above 31");
        assertThrows(
                IllegalArgumentException.class,
                () -> new CostFactor(100),
                "Should reject cost factor 100");
        assertThrows(
                IllegalArgumentException.class,
                () -> new CostFactor(Integer.MAX_VALUE),
                "Should reject maximum integer value");

        // Boundary validation - these should work
        assertDoesNotThrow(() -> new CostFactor(4), "Should accept minimum cost 4");
        assertDoesNotThrow(() -> new CostFactor(31), "Should accept maximum cost 31");
    }

    @Test
    @DisplayName("Should handle all 0xFF bytes in salt")
    void shouldHandleAllFFBytesInSalt() {
        // Salt with all 0xFF - tests edge case of maximum byte values
        byte[] ffSalt = new byte[16];
        for (int i = 0; i < 16; i++) {
            ffSalt[i] = (byte) 0xFF;
        }
        Salt salt = new Salt(ffSalt);

        Password password = new Password("testWithFFSalt");
        Hash hash = service.hash(password, BCryptVersion.VERSION_2A, new CostFactor(10), salt);

        assertTrue(service.verify(password, hash), "Should verify with all-0xFF salt");

        // Verify salt encoding/decoding round-trip
        String encoded = salt.toBCryptString();
        assertEquals(22, encoded.length(), "BCrypt salt should be 22 chars");

        Salt decoded = Salt.fromBCryptString(encoded);
        assertArrayEquals(ffSalt, decoded.getBytes(), "Round-trip should preserve salt bytes");

        // Verify deterministic hashing with 0xFF salt
        Hash hash2 = service.hash(password, BCryptVersion.VERSION_2A, new CostFactor(10), salt);
        assertEquals(hash.getValue(), hash2.getValue(), "Same 0xFF salt should produce same hash");

        // Test that 0xFF salt differs from zero salt
        byte[] zeroSalt = new byte[16];
        Salt zeroSaltObj = new Salt(zeroSalt);
        Hash zeroHash =
                service.hash(password, BCryptVersion.VERSION_2A, new CostFactor(10), zeroSaltObj);
        assertNotEquals(
                hash.getValue(),
                zeroHash.getValue(),
                "Different salts should produce different hashes");
    }

    @Test
    @DisplayName("Should handle alternating byte pattern in salt")
    void shouldHandleAlternatingBytePatternInSalt() {
        // Test various byte patterns in salt

        // Pattern 1: Alternating 0x00 and 0xFF
        byte[] pattern1 = new byte[16];
        for (int i = 0; i < 16; i++) {
            pattern1[i] = (i % 2 == 0) ? (byte) 0x00 : (byte) 0xFF;
        }
        Salt salt1 = new Salt(pattern1);

        // Pattern 2: Incrementing bytes
        byte[] pattern2 = new byte[16];
        for (int i = 0; i < 16; i++) {
            pattern2[i] = (byte) i;
        }
        Salt salt2 = new Salt(pattern2);

        // Pattern 3: Decrementing bytes
        byte[] pattern3 = new byte[16];
        for (int i = 0; i < 16; i++) {
            pattern3[i] = (byte) (15 - i);
        }
        Salt salt3 = new Salt(pattern3);

        Password password = new Password("testWithPatternSalt");

        // Test all patterns
        Hash hash1 = service.hash(password, BCryptVersion.VERSION_2A, new CostFactor(10), salt1);
        Hash hash2 = service.hash(password, BCryptVersion.VERSION_2A, new CostFactor(10), salt2);
        Hash hash3 = service.hash(password, BCryptVersion.VERSION_2A, new CostFactor(10), salt3);

        // All should verify
        assertTrue(service.verify(password, hash1), "Should verify with alternating pattern");
        assertTrue(service.verify(password, hash2), "Should verify with incrementing pattern");
        assertTrue(service.verify(password, hash3), "Should verify with decrementing pattern");

        // All should produce different hashes
        assertNotEquals(
                hash1.getValue(),
                hash2.getValue(),
                "Different patterns should produce different hashes");
        assertNotEquals(
                hash2.getValue(),
                hash3.getValue(),
                "Different patterns should produce different hashes");
        assertNotEquals(
                hash1.getValue(),
                hash3.getValue(),
                "Different patterns should produce different hashes");

        // Verify encoding/decoding preserves patterns
        Salt decoded1 = Salt.fromBCryptString(salt1.toBCryptString());
        assertArrayEquals(pattern1, decoded1.getBytes(), "Should preserve alternating pattern");
    }

    @Test
    @DisplayName("Should verify cost factor affects computation time")
    void shouldVerifyCostFactorAffectsComputationTime() {
        // BCrypt cost factor determines 2^cost iterations
        // Each increment doubles the work, thus doubling the time
        Password password = new Password("testTiming");

        // Use cost factors with clear exponential relationship
        CostFactor cost4 = new CostFactor(4); // 2^4 = 16 iterations
        CostFactor cost6 = new CostFactor(6); // 2^6 = 64 iterations (4x more)
        CostFactor cost8 = new CostFactor(8); // 2^8 = 256 iterations (16x more)

        // Warm up JVM
        service.hash(password, BCryptVersion.VERSION_2B, cost4);

        // Measure cost 4
        long start4 = System.nanoTime();
        Hash hash4 = service.hash(password, BCryptVersion.VERSION_2B, cost4);
        long time4 = System.nanoTime() - start4;

        // Measure cost 6
        long start6 = System.nanoTime();
        Hash hash6 = service.hash(password, BCryptVersion.VERSION_2B, cost6);
        long time6 = System.nanoTime() - start6;

        // Measure cost 8
        long start8 = System.nanoTime();
        Hash hash8 = service.hash(password, BCryptVersion.VERSION_2B, cost8);
        long time8 = System.nanoTime() - start8;

        // Verify exponential growth in computation time
        assertTrue(
                time6 > time4,
                String.format(
                        "Cost 6 (%,d ns) should take longer than cost 4 (%,d ns)", time6, time4));
        assertTrue(
                time8 > time6,
                String.format(
                        "Cost 8 (%,d ns) should take longer than cost 6 (%,d ns)", time8, time6));

        // Cost 8 should take significantly longer than cost 4 (roughly 16x)
        // Allow for some variance due to JVM optimizations
        assertTrue(
                time8 > time4 * 8,
                String.format(
                        "Cost 8 should take at least 8x longer than cost 4. "
                                + "Cost 4: %,d ns, Cost 8: %,d ns, Ratio: %.2fx",
                        time4, time8, (double) time8 / time4));

        // All should verify correctly
        assertTrue(service.verify(password, hash4), "Should verify cost 4 hash");
        assertTrue(service.verify(password, hash6), "Should verify cost 6 hash");
        assertTrue(service.verify(password, hash8), "Should verify cost 8 hash");

        // Different costs should produce different hashes even with same salt
        Salt fixedSalt = Salt.generateRandom();
        Hash fixed4 = service.hash(password, BCryptVersion.VERSION_2B, cost4, fixedSalt);
        Hash fixed8 = service.hash(password, BCryptVersion.VERSION_2B, cost8, fixedSalt);
        assertNotEquals(
                fixed4.getValue(),
                fixed8.getValue(),
                "Different cost factors should produce different hashes");
    }

    @Test
    @DisplayName("Should handle salt length validation")
    void shouldHandleSaltLengthValidation() {
        // BCrypt requires exactly 16-byte salts

        // Valid 16-byte salt
        byte[] valid16 = new byte[16];
        assertDoesNotThrow(() -> new Salt(valid16), "Should accept 16-byte salt");

        // Invalid salt lengths
        byte[] tooShort = new byte[15];
        assertThrows(
                IllegalArgumentException.class,
                () -> new Salt(tooShort),
                "Should reject 15-byte salt");

        byte[] tooLong = new byte[17];
        assertThrows(
                IllegalArgumentException.class,
                () -> new Salt(tooLong),
                "Should reject 17-byte salt");

        byte[] empty = new byte[0];
        assertThrows(
                IllegalArgumentException.class, () -> new Salt(empty), "Should reject empty salt");

        // Null salt
        assertThrows(NullPointerException.class, () -> new Salt(null), "Should reject null salt");
    }
}
