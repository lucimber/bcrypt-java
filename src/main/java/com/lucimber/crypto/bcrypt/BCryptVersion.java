package com.lucimber.crypto.bcrypt;

/**
 * Represents the version of the BCrypt algorithm.
 * BCrypt has different versions that handle certain edge cases differently.
 * The most common versions are 2a and 2b.
 */
public enum BCryptVersion {
    /**
     * Version 2a: The original secure version after fixing the 2 vulnerability.
     * Some implementations had a bug with handling non-ASCII characters.
     */
    VERSION_2A("2a"),
    
    /**
     * Version 2b: Fixed the handling of non-ASCII characters.
     * This is the recommended version for new applications.
     */
    VERSION_2B("2b");
    
    private final String prefix;
    
    BCryptVersion(String prefix) {
        this.prefix = prefix;
    }
    
    /**
     * Gets the version prefix used in BCrypt hashes.
     * 
     * @return the version prefix (e.g., "2a", "2b")
     */
    public String getPrefix() {
        return prefix;
    }
    
    /**
     * Parses a BCrypt version from its prefix string.
     * 
     * @param prefix the version prefix (e.g., "2a", "2b")
     * @return the corresponding BCryptVersion
     * @throws IllegalArgumentException if the prefix is not recognized
     */
    public static BCryptVersion fromPrefix(String prefix) {
        for (BCryptVersion version : values()) {
            if (version.prefix.equals(prefix)) {
                return version;
            }
        }
        throw new IllegalArgumentException("Unknown BCrypt version: " + prefix);
    }
}