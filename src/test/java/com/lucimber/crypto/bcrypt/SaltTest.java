/*
 * SPDX-FileCopyrightText: 2025 Lucimber UG
 * SPDX-License-Identifier: Apache-2.0
 */
package com.lucimber.crypto.bcrypt;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SaltTest {

    @Test
    @DisplayName("Should create salt from valid byte array")
    void shouldCreateSaltFromValidByteArray() {
        byte[] saltBytes = new byte[Salt.SALT_LENGTH];
        for (int i = 0; i < saltBytes.length; i++) {
            saltBytes[i] = (byte) i;
        }

        Salt salt = new Salt(saltBytes);

        assertNotNull(salt);
        assertArrayEquals(saltBytes, salt.getBytes());
    }

    @Test
    @DisplayName("Should throw exception for null byte array")
    void shouldThrowExceptionForNullByteArray() {
        assertThrows(NullPointerException.class, () -> new Salt(null));
    }

    @Test
    @DisplayName("Should throw exception for incorrect salt length")
    void shouldThrowExceptionForIncorrectSaltLength() {
        byte[] tooShort = new byte[Salt.SALT_LENGTH - 1];
        byte[] tooLong = new byte[Salt.SALT_LENGTH + 1];

        assertThrows(IllegalArgumentException.class, () -> new Salt(tooShort));
        assertThrows(IllegalArgumentException.class, () -> new Salt(tooLong));
    }

    @Test
    @DisplayName("Should generate random salt")
    void shouldGenerateRandomSalt() {
        Salt salt1 = Salt.generateRandom();
        Salt salt2 = Salt.generateRandom();

        assertNotNull(salt1);
        assertNotNull(salt2);
        assertEquals(Salt.SALT_LENGTH, salt1.getBytes().length);
        assertEquals(Salt.SALT_LENGTH, salt2.getBytes().length);

        // Should generate different salts (extremely unlikely to be equal)
        assertNotEquals(salt1, salt2);
    }

    @Test
    @DisplayName("Should create defensive copy of byte array")
    void shouldCreateDefensiveCopyOfByteArray() {
        byte[] original = new byte[Salt.SALT_LENGTH];
        for (int i = 0; i < original.length; i++) {
            original[i] = (byte) i;
        }

        Salt salt = new Salt(original);

        // Modify original array
        original[0] = (byte) 99;

        // Salt should not be affected
        assertNotEquals(original[0], salt.getBytes()[0]);
    }

    @Test
    @DisplayName("Should return defensive copy from getBytes")
    void shouldReturnDefensiveCopyFromGetBytes() {
        Salt salt = Salt.generateRandom();
        byte[] retrieved = salt.getBytes();
        byte originalValue = retrieved[0];

        // Modify retrieved array
        retrieved[0] = (byte) (originalValue + 1);

        // Salt should not be affected
        assertEquals(originalValue, salt.getBytes()[0]);
    }

    @Test
    @DisplayName("Should encode and decode BCrypt string correctly")
    void shouldEncodeAndDecodeBCryptStringCorrectly() {
        byte[] saltBytes = new byte[Salt.SALT_LENGTH];
        for (int i = 0; i < saltBytes.length; i++) {
            saltBytes[i] = (byte) (i * 7); // Some pattern
        }

        Salt originalSalt = new Salt(saltBytes);
        String encoded = originalSalt.toBCryptString();

        assertNotNull(encoded);
        assertTrue(encoded.length() > 0);

        // Decode back
        Salt decodedSalt = Salt.fromBCryptString(encoded);
        assertArrayEquals(originalSalt.getBytes(), decodedSalt.getBytes());
    }

    @Test
    @DisplayName("Should handle BCrypt string with all possible byte values")
    void shouldHandleBCryptStringWithAllPossibleByteValues() {
        // Test edge cases
        byte[] allZeros = new byte[Salt.SALT_LENGTH];
        byte[] allOnes = new byte[Salt.SALT_LENGTH];
        for (int i = 0; i < Salt.SALT_LENGTH; i++) {
            allOnes[i] = (byte) 0xFF;
        }

        Salt zeroSalt = new Salt(allZeros);
        Salt oneSalt = new Salt(allOnes);

        String zeroEncoded = zeroSalt.toBCryptString();
        String oneEncoded = oneSalt.toBCryptString();

        Salt zeroDecoded = Salt.fromBCryptString(zeroEncoded);
        Salt oneDecoded = Salt.fromBCryptString(oneEncoded);

        assertArrayEquals(allZeros, zeroDecoded.getBytes());
        assertArrayEquals(allOnes, oneDecoded.getBytes());
    }

    @Test
    @DisplayName("Should throw exception for null BCrypt string")
    void shouldThrowExceptionForNullBCryptString() {
        assertThrows(NullPointerException.class, () -> Salt.fromBCryptString(null));
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void shouldImplementEqualsCorrectly() {
        byte[] saltBytes1 = new byte[Salt.SALT_LENGTH];
        byte[] saltBytes2 = new byte[Salt.SALT_LENGTH];
        for (int i = 0; i < Salt.SALT_LENGTH; i++) {
            saltBytes1[i] = (byte) i;
            saltBytes2[i] = (byte) i;
        }

        Salt salt1 = new Salt(saltBytes1);
        Salt salt2 = new Salt(saltBytes2);
        Salt salt3 = Salt.generateRandom();

        assertEquals(salt1, salt2);
        assertNotEquals(salt1, salt3);
        assertEquals(salt1, salt1); // reflexive
        assertNotEquals(salt1, null);
        assertNotEquals(salt1, "not a salt object");
    }

    @Test
    @DisplayName("Should implement hashCode consistently")
    void shouldImplementHashCodeConsistently() {
        byte[] saltBytes = new byte[Salt.SALT_LENGTH];
        for (int i = 0; i < saltBytes.length; i++) {
            saltBytes[i] = (byte) i;
        }

        Salt salt1 = new Salt(saltBytes.clone());
        Salt salt2 = new Salt(saltBytes.clone());

        assertEquals(salt1.hashCode(), salt2.hashCode());
    }

    @Test
    @DisplayName("Should provide informative toString")
    void shouldProvideInformativeToString() {
        Salt salt = Salt.generateRandom();
        String toString = salt.toString();

        assertTrue(toString.contains("Salt"));
        assertTrue(toString.contains("encoded="));
    }
}
