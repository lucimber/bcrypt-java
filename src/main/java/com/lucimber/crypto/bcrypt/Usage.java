package com.lucimber.crypto.bcrypt;

/**
 * BCrypt Java Library Usage Documentation
 *
 * <p>This library provides a pure Java implementation of the BCrypt password hashing algorithm with
 * zero runtime dependencies. It follows Domain-Driven Design principles and provides full
 * compatibility with Spring Security and Bouncy Castle implementations.
 *
 * <h2>Quick Start</h2>
 *
 * <pre>{@code
 * // Get the BCrypt service instance
 * BCryptService service = BCryptService.getInstance();
 *
 * // Hash a password
 * Password password = new Password("mySecretPassword");
 * Hash hash = service.hash(password);
 *
 * // Verify a password
 * boolean valid = service.verify(password, hash);
 * }</pre>
 *
 * <h2>Advanced Usage</h2>
 *
 * <pre>{@code
 * // Use specific BCrypt version
 * Hash hash2a = service.hash(password, BCryptVersion.VERSION_2A);
 * Hash hash2b = service.hash(password, BCryptVersion.VERSION_2B);
 *
 * // Use custom cost factor (4-31, default is 10)
 * CostFactor cost = new CostFactor(12);
 * Hash strongHash = service.hash(password, BCryptVersion.VERSION_2B, cost);
 *
 * // Use specific salt (for testing or compatibility)
 * Salt salt = Salt.generateRandom();
 * Hash hashWithSalt = service.hash(password, BCryptVersion.VERSION_2B, cost, salt);
 * }</pre>
 *
 * <h2>Security Considerations</h2>
 *
 * <ul>
 *   <li>Passwords are automatically truncated at 72 bytes (BCrypt specification)
 *   <li>Use {@code Password.clear()} to zero sensitive memory after use
 *   <li>Higher cost factors provide better security but slower performance
 *   <li>Default cost factor of 10 is suitable for most applications
 *   <li>Empty passwords are rejected for security reasons
 * </ul>
 *
 * <h2>Spring Security Compatibility</h2>
 *
 * <pre>{@code
 * // Verify Spring Security generated hash
 * String springHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
 * Hash hash = new Hash(springHash);
 * Password password = new Password("password");
 * boolean valid = service.verify(password, hash);
 * }</pre>
 *
 * <h2>Bouncy Castle Compatibility</h2>
 *
 * <pre>{@code
 * // Generate hash compatible with Bouncy Castle
 * Password password = new Password("testPassword");
 * Hash hash = service.hash(password, BCryptVersion.VERSION_2A);
 * // This hash can be verified by Bouncy Castle's BCrypt implementation
 * }</pre>
 *
 * <h2>API Entry Points</h2>
 *
 * <ul>
 *   <li>{@link BCryptService} - Main service interface (singleton)
 *   <li>{@link Password} - Password value object with secure memory management
 *   <li>{@link Hash} - BCrypt hash value object with parsing and validation
 *   <li>{@link Salt} - 16-byte salt with BCrypt Base64 encoding
 *   <li>{@link CostFactor} - Work factor (4-31) for computational cost
 *   <li>{@link BCryptVersion} - Version enum (2a/2b variants)
 * </ul>
 *
 * @author Lucimber
 * @version 1.0.0
 * @see BCryptService
 * @see <a href="https://github.com/lucimber/bcrypt-java">GitHub Repository</a>
 */
public final class Usage {

    private Usage() {
        // Utility class, not instantiable
    }

    /**
     * Main method providing usage information when JAR is executed directly.
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        System.out.println("BCrypt Java Library v1.0.0");
        System.out.println("=========================");
        System.out.println();
        System.out.println("A pure Java implementation of BCrypt password hashing");
        System.out.println();
        System.out.println("Usage in your Java application:");
        System.out.println();
        System.out.println("  BCryptService service = BCryptService.getInstance();");
        System.out.println("  Password password = new Password(\"myPassword\");");
        System.out.println("  Hash hash = service.hash(password);");
        System.out.println("  boolean valid = service.verify(password, hash);");
        System.out.println();
        System.out.println("Maven dependency:");
        System.out.println("  <dependency>");
        System.out.println("    <groupId>com.lucimber.crypto</groupId>");
        System.out.println("    <artifactId>bcrypt</artifactId>");
        System.out.println("    <version>1.0.0</version>");
        System.out.println("  </dependency>");
        System.out.println();
        System.out.println("Gradle dependency:");
        System.out.println("  implementation 'com.lucimber.crypto:bcrypt:1.0.0'");
        System.out.println();
        System.out.println("For detailed documentation, see the Javadoc or visit:");
        System.out.println("https://github.com/lucimber/bcrypt-java");
    }
}
