package com.lucimber.crypto.bcrypt;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Edge case tests for salt handling and cost factors in BCrypt implementation. Tests boundary
 * conditions for salts and work factors.
 */
class EdgeCaseSaltTest {

    private final BCryptService service = BCryptService.getInstance();

    @Test
    @DisplayName("Should handle zero bytes in salt")
    void shouldHandleZeroBytesInSalt() {
        // Create salt with all zeros
        byte[] zeroSalt = new byte[16];
        Salt salt = new Salt(zeroSalt);

        Password password = new Password("testWithZeroSalt");
        Hash hash = service.hash(password, BCryptVersion.VERSION_2A, new CostFactor(10), salt);

        assertTrue(service.verify(password, hash));

        // Verify salt encoding works correctly
        String saltString = salt.toBCryptString();
        assertEquals(22, saltString.length());
    }

    @Test
    @DisplayName("Should handle same salt producing same hash")
    void shouldHandleSameSaltProducingSameHash() {
        Password password = new Password("consistentPassword");
        Salt salt = Salt.generateRandom();
        CostFactor cost = new CostFactor(10);

        // Hash multiple times with same salt
        Hash hash1 = service.hash(password, BCryptVersion.VERSION_2A, cost, salt);
        Hash hash2 = service.hash(password, BCryptVersion.VERSION_2A, cost, salt);
        Hash hash3 = service.hash(password, BCryptVersion.VERSION_2B, cost, salt);

        // Same version should produce identical hashes
        assertEquals(hash1.getValue(), hash2.getValue());

        // Different version should produce different hash
        assertNotEquals(hash1.getValue(), hash3.getValue());

        // But both should verify the password
        assertTrue(service.verify(password, hash1));
        assertTrue(service.verify(password, hash3));
    }

    @Test
    @DisplayName("Should handle minimum and maximum cost factors")
    void shouldHandleMinAndMaxCostFactors() {
        Password password = new Password("testPassword");

        // Minimum cost factor (4)
        CostFactor minCost = new CostFactor(4);
        Hash minHash = service.hash(password, BCryptVersion.VERSION_2B, minCost);
        assertTrue(service.verify(password, minHash));
        assertEquals(4, minHash.getCostFactor().getValue());

        // Maximum cost factor (31) - might be slow
        CostFactor maxCost = new CostFactor(31);

        // For 31, just verify we can create it, but don't actually hash (too slow)
        assertDoesNotThrow(() -> new CostFactor(31));

        // Test a reasonably high cost factor instead
        CostFactor highCost = new CostFactor(15);
        Hash highHash = service.hash(password, BCryptVersion.VERSION_2B, highCost);
        assertTrue(service.verify(password, highHash));
        assertEquals(15, highHash.getCostFactor().getValue());
    }

    @Test
    @DisplayName("Should reject invalid cost factors")
    void shouldRejectInvalidCostFactors() {
        assertThrows(IllegalArgumentException.class, () -> new CostFactor(3));
        assertThrows(IllegalArgumentException.class, () -> new CostFactor(32));
        assertThrows(IllegalArgumentException.class, () -> new CostFactor(0));
        assertThrows(IllegalArgumentException.class, () -> new CostFactor(-1));
        assertThrows(IllegalArgumentException.class, () -> new CostFactor(100));
    }

    @Test
    @DisplayName("Should handle all 0xFF bytes in salt")
    void shouldHandleAllFFBytesInSalt() {
        // Create salt with all 0xFF bytes
        byte[] ffSalt = new byte[16];
        for (int i = 0; i < 16; i++) {
            ffSalt[i] = (byte) 0xFF;
        }
        Salt salt = new Salt(ffSalt);

        Password password = new Password("testWithFFSalt");
        Hash hash = service.hash(password, BCryptVersion.VERSION_2A, new CostFactor(10), salt);

        assertTrue(service.verify(password, hash));

        // Verify salt can be encoded and decoded correctly
        String encoded = salt.toBCryptString();
        Salt decoded = Salt.fromBCryptString(encoded);
        assertArrayEquals(ffSalt, decoded.getBytes());
    }

    @Test
    @DisplayName("Should handle alternating byte pattern in salt")
    void shouldHandleAlternatingBytePatternInSalt() {
        // Create salt with alternating 0x00 and 0xFF
        byte[] patternSalt = new byte[16];
        for (int i = 0; i < 16; i++) {
            patternSalt[i] = (i % 2 == 0) ? (byte) 0x00 : (byte) 0xFF;
        }
        Salt salt = new Salt(patternSalt);

        Password password = new Password("testWithPatternSalt");
        Hash hash = service.hash(password, BCryptVersion.VERSION_2A, new CostFactor(10), salt);

        assertTrue(service.verify(password, hash));
    }

    @Test
    @DisplayName("Should verify cost factor affects computation time")
    void shouldVerifyCostFactorAffectsComputationTime() {
        Password password = new Password("testTiming");

        // Test that higher cost factors take more time
        CostFactor cost4 = new CostFactor(4);
        CostFactor cost8 = new CostFactor(8);

        long start4 = System.currentTimeMillis();
        Hash hash4 = service.hash(password, BCryptVersion.VERSION_2B, cost4);
        long time4 = System.currentTimeMillis() - start4;

        long start8 = System.currentTimeMillis();
        Hash hash8 = service.hash(password, BCryptVersion.VERSION_2B, cost8);
        long time8 = System.currentTimeMillis() - start8;

        // Cost 8 should take noticeably longer than cost 4
        // (approximately 16 times longer, but we'll just check it's longer)
        assertTrue(
                time8 > time4,
                "Higher cost factor should take more time. Cost 4: "
                        + time4
                        + "ms, Cost 8: "
                        + time8
                        + "ms");

        // Both should verify correctly
        assertTrue(service.verify(password, hash4));
        assertTrue(service.verify(password, hash8));
    }
}
