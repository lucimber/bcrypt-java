/**
 * BCrypt password hashing library for Java.
 *
 * <p>This module provides a pure Java implementation of the BCrypt password hashing algorithm with
 * zero runtime dependencies. It is fully compatible with Spring Security and Bouncy Castle
 * implementations.
 *
 * <p>The module exports the {@code com.lucimber.crypto.bcrypt} package which contains:
 *
 * <ul>
 *   <li>{@link com.lucimber.crypto.bcrypt.BCryptService} - Main service interface (singleton)
 *   <li>{@link com.lucimber.crypto.bcrypt.Password} - Secure password wrapper
 *   <li>{@link com.lucimber.crypto.bcrypt.Hash} - BCrypt hash representation
 *   <li>{@link com.lucimber.crypto.bcrypt.Salt} - Salt generation and encoding
 *   <li>{@link com.lucimber.crypto.bcrypt.CostFactor} - Work factor configuration
 *   <li>{@link com.lucimber.crypto.bcrypt.BCryptVersion} - Algorithm version enum
 * </ul>
 *
 * <p>Internal implementation classes (BCryptEngine, BCryptBase64) are not exported and remain
 * encapsulated within the module.
 *
 * @since Java 9
 */
module com.lucimber.crypto.bcrypt {
    // Export only the public API package
    exports com.lucimber.crypto.bcrypt;

// No dependencies required - this is a zero-dependency library

// Internal packages (none explicitly declared) remain encapsulated:
// - BCryptEngine (package-private)
// - BCryptBase64 (package-private)
}
