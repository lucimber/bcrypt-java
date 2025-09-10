/**
 * BCrypt password hashing library for Java.
 *
 * <p>This module provides a pure Java implementation of the BCrypt password hashing algorithm with
 * zero runtime dependencies. It is fully compatible with Spring Security and Bouncy Castle
 * implementations.
 *
 * <p>The module exports the {@code com.lucimber.bcrypt} package which contains:
 *
 * <ul>
 *   <li>{@link com.lucimber.bcrypt.BCryptService} - Main service interface (singleton)
 *   <li>{@link com.lucimber.bcrypt.Password} - Secure password wrapper
 *   <li>{@link com.lucimber.bcrypt.Hash} - BCrypt hash representation
 *   <li>{@link com.lucimber.bcrypt.Salt} - Salt generation and encoding
 *   <li>{@link com.lucimber.bcrypt.CostFactor} - Work factor configuration
 *   <li>{@link com.lucimber.bcrypt.BCryptVersion} - Algorithm version enum
 * </ul>
 *
 * <p>Internal implementation classes (BCryptEngine, BCryptBase64) are not exported and remain
 * encapsulated within the module.
 *
 * @since Java 9
 */
module com.lucimber.bcrypt {
    // Export only the public API package
    exports com.lucimber.bcrypt;

// No dependencies required - this is a zero-dependency library

// Internal packages (none explicitly declared) remain encapsulated:
// - BCryptEngine (package-private)
// - BCryptBase64 (package-private)
}
