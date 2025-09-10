/*
 * SPDX-FileCopyrightText: 2025 Lucimber UG
 * SPDX-License-Identifier: Apache-2.0
 */
package com.lucimber.bcrypt;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a plain text password as a value object. This class ensures that passwords are handled
 * securely and consistently. The password is stored as a char array to allow for secure erasure
 * from memory.
 */
public final class Password {
    private static final int MAX_LENGTH_BYTES = 72; // BCrypt limitation

    private final char[] value;

    /**
     * Creates a new Password from a character array. The array is cloned to prevent external
     * modification. Passwords longer than 72 bytes will be truncated when used.
     *
     * @param password the password characters
     * @throws IllegalArgumentException if password is null or empty
     */
    public Password(char[] password) {
        Objects.requireNonNull(password, "Password cannot be null");
        if (password.length == 0) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        // BCrypt will truncate at 72 bytes - we store the full password
        // and truncate in getBytes()
        this.value = password.clone();
    }

    /**
     * Creates a new Password from a String. This method is provided for convenience but using
     * char[] is preferred for security. Passwords longer than 72 bytes will be truncated when used.
     *
     * @param password the password string
     * @throws IllegalArgumentException if password is null or empty
     */
    public Password(String password) {
        this(Objects.requireNonNull(password, "Password cannot be null").toCharArray());
    }

    /**
     * Gets the password as a byte array encoded in UTF-8. BCrypt operates on bytes, not characters.
     * Passwords longer than 72 bytes are truncated to match BCrypt behavior.
     *
     * @return the password bytes, truncated to 72 bytes if necessary
     */
    public byte[] getBytes() {
        byte[] bytes = new String(value).getBytes(StandardCharsets.UTF_8);
        if (bytes.length > MAX_LENGTH_BYTES) {
            byte[] truncated = new byte[MAX_LENGTH_BYTES];
            System.arraycopy(bytes, 0, truncated, 0, MAX_LENGTH_BYTES);
            return truncated;
        }
        return bytes;
    }

    /**
     * Gets a copy of the password characters. The returned array is a copy to prevent external
     * modification.
     *
     * @return a copy of the password characters
     */
    public char[] toCharArray() {
        return value.clone();
    }

    /**
     * Clears the password from memory. This should be called when the password is no longer needed.
     */
    public void clear() {
        Arrays.fill(value, '\0');
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Password password = (Password) obj;
        return Arrays.equals(value, password.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public String toString() {
        return "Password{length=" + value.length + "}";
    }
}
