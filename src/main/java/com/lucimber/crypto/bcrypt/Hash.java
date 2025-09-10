package com.lucimber.crypto.bcrypt;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a BCrypt hash value object. A BCrypt hash contains the algorithm version, cost factor,
 * salt, and the actual hash value. The standard format is: $version$cost$salt_and_hash Example:
 * $2b$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
 */
public final class Hash {
    private static final Pattern BCRYPT_PATTERN =
            Pattern.compile("^\\$(2[ab])\\$([0-9]{2})\\$(.{53})$");

    private final String value;
    private final BCryptVersion version;
    private final CostFactor costFactor;
    private final String saltAndHash;

    /**
     * Creates a new Hash from a BCrypt hash string.
     *
     * @param hash the BCrypt hash string
     * @throws IllegalArgumentException if the hash format is invalid
     */
    public Hash(String hash) {
        Objects.requireNonNull(hash, "Hash cannot be null");

        Matcher matcher = BCRYPT_PATTERN.matcher(hash);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid BCrypt hash format: " + hash);
        }

        this.value = hash;
        this.version = BCryptVersion.fromPrefix(matcher.group(1));
        this.costFactor = new CostFactor(Integer.parseInt(matcher.group(2)));
        this.saltAndHash = matcher.group(3);
    }

    /**
     * Creates a new Hash from its components.
     *
     * @param version the BCrypt version
     * @param costFactor the cost factor
     * @param saltAndHash the combined salt and hash (53 characters in BCrypt encoding)
     * @throws IllegalArgumentException if any parameter is null or invalid
     */
    public Hash(BCryptVersion version, CostFactor costFactor, String saltAndHash) {
        Objects.requireNonNull(version, "Version cannot be null");
        Objects.requireNonNull(costFactor, "Cost factor cannot be null");
        Objects.requireNonNull(saltAndHash, "Salt and hash cannot be null");

        if (saltAndHash.length() != 53) {
            throw new IllegalArgumentException(
                    "Salt and hash must be exactly 53 characters, but was " + saltAndHash.length());
        }

        this.version = version;
        this.costFactor = costFactor;
        this.saltAndHash = saltAndHash;
        this.value =
                String.format(
                        "$%s$%02d$%s", version.getPrefix(), costFactor.getValue(), saltAndHash);
    }

    /**
     * Gets the BCrypt version of this hash.
     *
     * @return the BCrypt version
     */
    public BCryptVersion getVersion() {
        return version;
    }

    /**
     * Gets the cost factor of this hash.
     *
     * @return the cost factor
     */
    public CostFactor getCostFactor() {
        return costFactor;
    }

    /**
     * Gets the salt portion of the hash. The salt is the first 22 characters of the saltAndHash.
     *
     * @return the salt string in BCrypt encoding
     */
    public String getSalt() {
        return saltAndHash.substring(0, 22);
    }

    /**
     * Gets the hash portion (without salt). The hash is the last 31 characters of the saltAndHash.
     *
     * @return the hash string in BCrypt encoding
     */
    public String getHashPortion() {
        return saltAndHash.substring(22);
    }

    /**
     * Gets the complete hash string.
     *
     * @return the complete BCrypt hash string
     */
    public String getValue() {
        return value;
    }

    /**
     * Checks if this hash matches a given password. This is a convenience method that uses
     * BCryptService internally.
     *
     * @param password the password to verify
     * @return true if the password matches this hash, false otherwise
     */
    public boolean verify(Password password) {
        return BCryptService.getInstance().verify(password, this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Hash hash = (Hash) obj;
        return Objects.equals(value, hash.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
