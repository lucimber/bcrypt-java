/*
 * SPDX-FileCopyrightText: 2025 Lucimber UG
 * SPDX-License-Identifier: Apache-2.0
 */
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
     * Dynamically generates the API summary from the library's actual components.
     *
     * @return the complete API summary text
     */
    public static String getApiSummary() {
        StringBuilder sb = new StringBuilder();

        // Header with version from package or manifest
        String version = getVersion();
        sb.append("BCrypt Library v").append(version).append("\n");
        sb.append("=".repeat(21 + version.length())).append("\n");
        sb.append("Pure Java BCrypt implementation with zero runtime dependencies\n");
        sb.append("Compatible with Spring Security and Bouncy Castle\n\n");

        sb.append("Main Entry Point: ").append(BCryptService.class.getName()).append("\n");
        sb.append("=".repeat(60)).append("\n\n");

        // Quick Start
        sb.append("Quick Start:\n");
        sb.append("------------\n");
        sb.append("1. Get service instance: BCryptService.getInstance()\n");
        sb.append("2. Hash password: service.hash(new Password(\"secret\"))\n");
        sb.append("3. Verify password: service.verify(password, hash)\n\n");

        // Key Classes
        sb.append("Key Classes:\n");
        sb.append("------------\n");

        // BCryptService
        sb.append("- ")
                .append(BCryptService.class.getSimpleName())
                .append(": Main service class (singleton)\n");
        sb.append("  * hash(Password) -> Hash\n");
        sb.append("  * hash(Password, BCryptVersion) -> Hash\n");
        sb.append("  * hash(Password, BCryptVersion, CostFactor) -> Hash\n");
        sb.append("  * hash(Password, BCryptVersion, CostFactor, Salt) -> Hash\n");
        sb.append("  * verify(Password, Hash) -> boolean\n\n");

        // Password
        sb.append("- ")
                .append(Password.class.getSimpleName())
                .append(": Secure password wrapper\n");
        sb.append("  * new Password(String)\n");
        sb.append("  * new Password(char[])\n");
        sb.append("  * getBytes() -> byte[] (truncated at 72 bytes)\n");
        sb.append("  * clear() -> void (zeroes memory)\n\n");

        // Hash
        sb.append("- ").append(Hash.class.getSimpleName()).append(": BCrypt hash representation\n");
        sb.append("  * new Hash(String) - parse hash string\n");
        sb.append("  * getValue() -> String (full hash)\n");
        sb.append("  * getSalt() -> String (22 chars)\n");
        sb.append("  * getHashPortion() -> String (31 chars)\n");
        sb.append("  * getCostFactor() -> CostFactor\n");
        sb.append("  * getVersion() -> BCryptVersion\n\n");

        // Salt
        sb.append("- ").append(Salt.class.getSimpleName()).append(": 16-byte salt value\n");
        sb.append("  * generateRandom() -> Salt\n");
        sb.append("  * new Salt(byte[16])\n");
        sb.append("  * toBCryptString() -> String (22 chars)\n");
        sb.append("  * fromBCryptString(String) -> Salt\n\n");

        // CostFactor
        sb.append("- ")
                .append(CostFactor.class.getSimpleName())
                .append(": Work factor configuration\n");
        sb.append("  * new CostFactor(int) - range: ")
                .append(CostFactor.MIN_COST)
                .append("-")
                .append(CostFactor.MAX_COST)
                .append("\n");
        sb.append("  * getValue() -> int\n");
        sb.append("  * Default: ").append(CostFactor.DEFAULT_COST).append("\n\n");

        // BCryptVersion
        sb.append("- ").append(BCryptVersion.class.getSimpleName()).append(": Algorithm version\n");
        for (BCryptVersion v : BCryptVersion.values()) {
            sb.append("  * ").append(v.name()).append(" - ").append(v.getPrefix()).append("\n");
        }
        sb.append("\n");

        // Examples
        sb.append("Examples:\n");
        sb.append("---------\n");
        sb.append("// Simple usage with defaults\n");
        sb.append("BCryptService service = BCryptService.getInstance();\n");
        sb.append("Password pwd = new Password(\"myPassword\");\n");
        sb.append("Hash hash = service.hash(pwd);\n");
        sb.append("boolean valid = service.verify(pwd, hash);\n\n");

        sb.append("// Advanced usage with custom settings\n");
        sb.append("CostFactor cost = new CostFactor(12);\n");
        sb.append("Hash strongHash = service.hash(pwd, BCryptVersion.VERSION_2B, cost);\n\n");

        sb.append("// Verify existing hash\n");
        sb.append(
                "String hashString ="
                        + " \"$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy\";\n");
        sb.append("Hash existingHash = new Hash(hashString);\n");
        sb.append(
                "boolean isValid = service.verify(new Password(\"password\"), existingHash);\n\n");

        // Security Notes
        sb.append("Security Notes:\n");
        sb.append("---------------\n");
        sb.append("- Passwords are automatically truncated at 72 bytes (BCrypt spec)\n");
        sb.append("- Empty passwords are rejected (throws IllegalArgumentException)\n");
        sb.append("- Use Password.clear() to zero sensitive memory after use\n");
        sb.append("- Higher cost factors = better security but slower (2^cost iterations)\n");
        sb.append("- Cost factor 10-12 recommended for most applications\n");
        sb.append("- Cost factor should increase over time as hardware improves\n\n");

        // Dependencies
        String groupId = "com.lucimber";
        String artifactId = "lucimber-bcrypt";

        sb.append("Maven Dependency:\n");
        sb.append("-----------------\n");
        sb.append("<dependency>\n");
        sb.append("    <groupId>").append(groupId).append("</groupId>\n");
        sb.append("    <artifactId>").append(artifactId).append("</artifactId>\n");
        sb.append("    <version>").append(version).append("</version>\n");
        sb.append("</dependency>\n\n");

        sb.append("Gradle Dependency:\n");
        sb.append("------------------\n");
        sb.append("implementation '")
                .append(groupId)
                .append(":")
                .append(artifactId)
                .append(":")
                .append(version)
                .append("'\n\n");

        sb.append("License: Apache 2.0\n");
        sb.append("GitHub: https://github.com/lucimber/bcrypt-java\n");

        return sb.toString();
    }

    /**
     * Gets the library version from the package implementation version or a default.
     *
     * @return the version string
     */
    private static String getVersion() {
        Package pkg = BCryptService.class.getPackage();
        String version = pkg != null ? pkg.getImplementationVersion() : null;
        return version != null ? version : "1.0.0";
    }

    /** Prints the API summary to standard output. */
    public static void printApiSummary() {
        System.out.println(getApiSummary());
    }

    /**
     * Main method providing usage information when JAR is executed directly.
     *
     * @param args command line arguments - use "--help" or "-h" to show full API summary
     */
    public static void main(String[] args) {
        // Check if user wants the full API summary/help
        if (args.length > 0 && (args[0].equals("--help") || args[0].equals("-h"))) {
            printApiSummary();
            return;
        }

        String version = getVersion();
        System.out.println("BCrypt Java Library v" + version);
        System.out.println("=".repeat(23 + version.length()));
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
        System.out.println("    <groupId>com.lucimber</groupId>");
        System.out.println("    <artifactId>lucimber-bcrypt</artifactId>");
        System.out.println("    <version>" + version + "</version>");
        System.out.println("  </dependency>");
        System.out.println();
        System.out.println("Gradle dependency:");
        System.out.println("  implementation 'com.lucimber:lucimber-bcrypt:" + version + "'");
        System.out.println();
        System.out.println(
                "For full API documentation, run: java -jar lucimber-bcrypt-"
                        + version
                        + ".jar --help");
        System.out.println("For detailed documentation, see the Javadoc or visit:");
        System.out.println("https://github.com/lucimber/bcrypt-java");
    }
}
