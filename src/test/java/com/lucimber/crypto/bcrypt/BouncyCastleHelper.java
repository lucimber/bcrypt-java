package com.lucimber.crypto.bcrypt;

import org.bouncycastle.crypto.generators.OpenBSDBCrypt;
import java.nio.charset.StandardCharsets;

/**
 * Helper class for Bouncy Castle BCrypt compatibility.
 * Bouncy Castle provides both low-level BCrypt.generate() and higher-level OpenBSDBCrypt.
 */
public class BouncyCastleHelper {
    
    /**
     * Generates a BCrypt hash using Bouncy Castle's OpenBSDBCrypt.
     * This is the high-level API that handles all BCrypt details.
     * 
     * @param password the password to hash
     * @param salt the salt (16 bytes)
     * @param cost the cost factor
     * @return the complete BCrypt hash string
     */
    public static String hashWithBouncyCastle(String password, byte[] salt, int cost) {
        // OpenBSDBCrypt.generate expects:
        // - password as char array
        // - salt as byte array
        // - cost as int
        String hash = OpenBSDBCrypt.generate(password.toCharArray(), salt, cost);
        // Bouncy Castle generates $2y$ by default, convert to $2a$ for compatibility
        if (hash.startsWith("$2y$")) {
            hash = "$2a$" + hash.substring(4);
        }
        return hash;
    }
    
    /**
     * Verifies a password against a BCrypt hash using Bouncy Castle.
     * 
     * @param password the password to verify
     * @param hash the BCrypt hash
     * @return true if the password matches the hash
     */
    public static boolean verifyWithBouncyCastle(String password, String hash) {
        return OpenBSDBCrypt.checkPassword(hash, password.toCharArray());
    }
    
    /**
     * Generates a BCrypt hash using Bouncy Castle with a BCrypt-formatted salt string.
     * 
     * @param password the password to hash
     * @param bcryptSalt BCrypt salt string (22 chars)
     * @param cost the cost factor
     * @return the complete BCrypt hash string
     */
    public static String hashWithBCryptSalt(String password, String bcryptSalt, int cost) {
        // Decode the BCrypt salt string to bytes
        byte[] saltBytes = decodeBCryptBase64(bcryptSalt);
        return hashWithBouncyCastle(password, saltBytes, cost);
    }
    
    /**
     * Generates a BCrypt hash compatible with the given hash's parameters.
     * 
     * @param password the password to hash
     * @param existingHash an existing hash to extract parameters from
     * @return the new BCrypt hash string
     */
    public static String hashLikeExisting(String password, String existingHash) {
        // Parse the existing hash
        if (!existingHash.startsWith("$2a$") && !existingHash.startsWith("$2b$")) {
            throw new IllegalArgumentException("Invalid BCrypt hash format");
        }
        
        // Extract cost factor
        int costEnd = existingHash.indexOf('$', 4);
        int cost = Integer.parseInt(existingHash.substring(4, costEnd));
        
        // Extract salt (22 chars after cost)
        String saltString = existingHash.substring(costEnd + 1, costEnd + 23);
        
        return hashWithBCryptSalt(password, saltString, cost);
    }
    
    // BCrypt Base64 decoding
    private static final String BCRYPT_ALPHABET = "./ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    
    private static byte[] decodeBCryptBase64(String s) {
        int len = s.length();
        byte[] bytes = new byte[16]; // BCrypt salt is always 16 bytes
        int byteIndex = 0;
        
        for (int i = 0; i < len && byteIndex < 16; ) {
            int c1 = BCRYPT_ALPHABET.indexOf(s.charAt(i++));
            int c2 = (i < len) ? BCRYPT_ALPHABET.indexOf(s.charAt(i++)) : 0;
            int c3 = (i < len) ? BCRYPT_ALPHABET.indexOf(s.charAt(i++)) : 0;
            int c4 = (i < len) ? BCRYPT_ALPHABET.indexOf(s.charAt(i++)) : 0;
            
            if (byteIndex < 16) bytes[byteIndex++] = (byte) ((c1 << 2) | (c2 >> 4));
            if (byteIndex < 16) bytes[byteIndex++] = (byte) ((c2 << 4) | (c3 >> 2));
            if (byteIndex < 16) bytes[byteIndex++] = (byte) ((c3 << 6) | c4);
        }
        
        return bytes;
    }
}