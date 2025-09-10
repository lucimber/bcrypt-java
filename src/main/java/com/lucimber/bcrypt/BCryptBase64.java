/*
 * SPDX-FileCopyrightText: 2025 Lucimber UG
 * SPDX-License-Identifier: Apache-2.0
 */
package com.lucimber.bcrypt;

/**
 * BCrypt-specific Base64 encoding/decoding utility. BCrypt uses a custom Base64 alphabet that
 * differs from standard Base64. The BCrypt alphabet is:
 * ./ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789
 */
final class BCryptBase64 {
    private static final String BCRYPT_ALPHABET =
            "./ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final int[] INDEX_TABLE = new int[128];

    static {
        for (int i = 0; i < INDEX_TABLE.length; i++) {
            INDEX_TABLE[i] = -1;
        }
        for (int i = 0; i < BCRYPT_ALPHABET.length(); i++) {
            INDEX_TABLE[BCRYPT_ALPHABET.charAt(i)] = i;
        }
    }

    private BCryptBase64() {
        // Utility class
    }

    /**
     * Encodes bytes to BCrypt Base64 string.
     *
     * @param input the bytes to encode
     * @return the BCrypt Base64 encoded string
     */
    static String encode(byte[] input) {
        if (input == null || input.length == 0) {
            return "";
        }

        StringBuilder output = new StringBuilder();

        for (int i = 0; i < input.length; ) {
            int b1 = input[i++] & 0xff;
            output.append(BCRYPT_ALPHABET.charAt(b1 >> 2));

            if (i < input.length) {
                int b2 = input[i++] & 0xff;
                output.append(BCRYPT_ALPHABET.charAt(((b1 & 0x03) << 4) | (b2 >> 4)));

                if (i < input.length) {
                    int b3 = input[i++] & 0xff;
                    output.append(BCRYPT_ALPHABET.charAt(((b2 & 0x0f) << 2) | (b3 >> 6)));
                    output.append(BCRYPT_ALPHABET.charAt(b3 & 0x3f));
                } else {
                    output.append(BCRYPT_ALPHABET.charAt((b2 & 0x0f) << 2));
                }
            } else {
                output.append(BCRYPT_ALPHABET.charAt((b1 & 0x03) << 4));
            }
        }

        // For BCrypt salt (16 bytes), we need exactly 22 characters
        // 16 bytes = 128 bits = 21.33... base64 chars, padded to 22
        while (output.length() < 22 && input.length == 16) {
            output.append(BCRYPT_ALPHABET.charAt(0));
        }

        return output.toString();
    }

    /**
     * Decodes BCrypt Base64 string to bytes.
     *
     * @param input the BCrypt Base64 string to decode
     * @return the decoded bytes
     * @throws IllegalArgumentException if the input contains invalid characters
     */
    static byte[] decode(String input) {
        if (input == null || input.isEmpty()) {
            return new byte[0];
        }

        // Calculate exact output length for BCrypt
        // For salt: 22 chars -> 16 bytes
        // For hash: 31 chars -> 23 bytes
        int outputLength;
        if (input.length() == 22) {
            outputLength = 16; // Salt
        } else if (input.length() == 31) {
            outputLength = 23; // Hash
        } else {
            // General case
            outputLength = (input.length() * 3) / 4;
        }

        byte[] output = new byte[outputLength];
        int outputIndex = 0;
        int inputIndex = 0;

        while (inputIndex < input.length() && outputIndex < output.length) {
            int c1 = decodeChar(input.charAt(inputIndex++));
            int c2 = (inputIndex < input.length()) ? decodeChar(input.charAt(inputIndex++)) : 0;

            output[outputIndex++] = (byte) ((c1 << 2) | (c2 >> 4));

            if (outputIndex < output.length && inputIndex < input.length()) {
                int c3 = decodeChar(input.charAt(inputIndex++));
                output[outputIndex++] = (byte) ((c2 << 4) | (c3 >> 2));

                if (outputIndex < output.length && inputIndex < input.length()) {
                    int c4 = decodeChar(input.charAt(inputIndex++));
                    output[outputIndex++] = (byte) ((c3 << 6) | c4);
                }
            }
        }

        return output;
    }

    private static int decodeChar(char c) {
        if (c >= INDEX_TABLE.length || INDEX_TABLE[c] == -1) {
            throw new IllegalArgumentException("Invalid BCrypt Base64 character: " + c);
        }
        return INDEX_TABLE[c];
    }
}
