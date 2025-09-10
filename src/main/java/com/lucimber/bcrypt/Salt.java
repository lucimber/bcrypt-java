/*
 * SPDX-FileCopyrightText: 2025 Lucimber UG
 * SPDX-License-Identifier: Apache-2.0
 */
package com.lucimber.bcrypt;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a cryptographic salt used in BCrypt hashing. A salt is a random value that ensures the
 * same password produces different hashes. BCrypt uses a 128-bit (16-byte) salt.
 */
public final class Salt {
    /** The required length of a BCrypt salt in bytes. */
    public static final int SALT_LENGTH = 16;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final byte[] value;

    /**
     * Creates a new Salt from a byte array.
     *
     * @param salt the salt bytes (must be exactly SALT_LENGTH bytes)
     * @throws IllegalArgumentException if salt is null or has incorrect length
     */
    public Salt(byte[] salt) {
        Objects.requireNonNull(salt, "Salt cannot be null");
        if (salt.length != SALT_LENGTH) {
            throw new IllegalArgumentException(
                    String.format(
                            "Salt must be exactly %d bytes, but was %d", SALT_LENGTH, salt.length));
        }
        this.value = salt.clone();
    }

    /**
     * Generates a new random salt using a cryptographically secure random number generator.
     *
     * @return a new random Salt
     */
    public static Salt generateRandom() {
        byte[] salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return new Salt(salt);
    }

    /**
     * Creates a Salt from a BCrypt-encoded string. BCrypt uses a custom Base64 encoding with a
     * different character set.
     *
     * @param encoded the BCrypt-encoded salt string
     * @return the decoded Salt
     * @throws IllegalArgumentException if the encoded string is invalid
     */
    public static Salt fromBCryptString(String encoded) {
        Objects.requireNonNull(encoded, "Encoded salt cannot be null");
        byte[] decoded = BCryptBase64.decode(encoded);
        return new Salt(decoded);
    }

    /**
     * Gets a copy of the salt bytes.
     *
     * @return a copy of the salt bytes
     */
    public byte[] getBytes() {
        return value.clone();
    }

    /**
     * Encodes the salt using BCrypt's custom Base64 encoding.
     *
     * @return the BCrypt-encoded salt string
     */
    public String toBCryptString() {
        return BCryptBase64.encode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Salt salt = (Salt) obj;
        return Arrays.equals(value, salt.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public String toString() {
        return "Salt{encoded=" + toBCryptString() + "}";
    }
}
