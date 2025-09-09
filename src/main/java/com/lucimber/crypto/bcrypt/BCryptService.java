package com.lucimber.crypto.bcrypt;

/**
 * Service for BCrypt hashing and verification.
 * This is the main entry point for BCrypt operations.
 * Supports both 2a and 2b variants of the BCrypt algorithm.
 */
public class BCryptService {
    private static final BCryptService INSTANCE = new BCryptService();
    
    private BCryptService() {
        // Singleton
    }
    
    /**
     * Gets the singleton instance of BCryptService.
     * 
     * @return the BCryptService instance
     */
    public static BCryptService getInstance() {
        return INSTANCE;
    }
    
    /**
     * Hashes a password using BCrypt with default settings.
     * Uses version 2b and default cost factor.
     * 
     * @param password the password to hash
     * @return the BCrypt hash
     */
    public Hash hash(Password password) {
        return hash(password, BCryptVersion.VERSION_2B, CostFactor.defaultCost());
    }
    
    /**
     * Hashes a password using BCrypt with specified version and default cost factor.
     * 
     * @param password the password to hash
     * @param version the BCrypt version to use
     * @return the BCrypt hash
     */
    public Hash hash(Password password, BCryptVersion version) {
        return hash(password, version, CostFactor.defaultCost());
    }
    
    /**
     * Hashes a password using BCrypt with specified settings.
     * 
     * @param password the password to hash
     * @param version the BCrypt version to use
     * @param costFactor the cost factor
     * @return the BCrypt hash
     */
    public Hash hash(Password password, BCryptVersion version, CostFactor costFactor) {
        Salt salt = Salt.generateRandom();
        return hash(password, version, costFactor, salt);
    }
    
    /**
     * Hashes a password using BCrypt with specified settings and salt.
     * 
     * @param password the password to hash
     * @param version the BCrypt version to use
     * @param costFactor the cost factor
     * @param salt the salt to use
     * @return the BCrypt hash
     */
    public Hash hash(Password password, BCryptVersion version, CostFactor costFactor, Salt salt) {
        // Build salt string in BCrypt format
        StringBuilder saltString = new StringBuilder();
        saltString.append("$");
        saltString.append(version.getPrefix());
        saltString.append("$");
        if (costFactor.getValue() < 10) {
            saltString.append("0");
        }
        saltString.append(costFactor.getValue());
        saltString.append("$");
        saltString.append(salt.toBCryptString());
        
        // Hash the password using BCryptEngine
        String hashValue = BCryptEngine.hashpw(new String(password.getBytes()), saltString.toString());
        
        // Replace version prefix if needed (BCryptEngine might use different version)
        if (version == BCryptVersion.VERSION_2B && hashValue.startsWith("$2a$")) {
            hashValue = "$2b$" + hashValue.substring(4);
        } else if (version == BCryptVersion.VERSION_2A && hashValue.startsWith("$2b$")) {
            hashValue = "$2a$" + hashValue.substring(4);
        }
        
        return new Hash(hashValue);
    }
    
    /**
     * Verifies a password against a BCrypt hash.
     * 
     * @param password the password to verify
     * @param hash the BCrypt hash to verify against
     * @return true if the password matches the hash, false otherwise
     */
    public boolean verify(Password password, Hash hash) {
        try {
            // Use BCryptEngine's checkpw method for verification
            return BCryptEngine.checkpw(new String(password.getBytes()), hash.getValue());
        } catch (Exception e) {
            // Invalid hash format or other error
            return false;
        }
    }
}