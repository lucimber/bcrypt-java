# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]



### ⚠️ Breaking Changes
- bump actions/upload-artifact from 4 to 5 (#33) @dependabot[bot]
### Changed
- deps(deps): bump org.junit.jupiter:junit-jupiter from 6.0.0 to 6.0.1 (#34) @dependabot[bot]
- bump github/codeql-action from 2 to 4 (#31) @dependabot[bot]
- deps(deps): bump org.springframework.security:spring-security-crypto from 6.5.5 to 6.5.6 (#32) @dependabot[bot]
- bump actions/setup-java from 4 to 5 (#27) @dependabot[bot]
- deps(deps): bump org.junit.jupiter:junit-jupiter from 5.13.4 to 6.0.0 (#30) @dependabot[bot]
- deps(deps): bump com.diffplug.spotless from 7.2.1 to 8.0.0 (#29) @dependabot[bot]
- deps(deps): bump org.springframework.security:spring-security-crypto from 6.5.3 to 6.5.5 (#26) @dependabot[bot]
- deps(deps): bump org.bouncycastle:bcprov-jdk18on from 1.81 to 1.82 (#25) @dependabot[bot]
## [1.0.0] - 2025-01-11

### Added
- Initial release of lucimber-bcrypt library
- Pure Java implementation of BCrypt password hashing algorithm
- Zero runtime dependencies
- Support for BCrypt 2a and 2b variants
- Full compatibility with Spring Security BCrypt
- Full compatibility with Bouncy Castle BCrypt
- Domain-driven design with immutable value objects
- Comprehensive test suite (110 tests)
- Java 9+ module support (JPMS)
- Self-documenting JAR with usage help
- Maven Central publishing configuration
- SPDX license headers

### Features
- `BCryptService` - Main service interface (singleton pattern)
- `Password` - Secure password value object with memory clearing
- `Hash` - BCrypt hash representation with parsing and validation
- `Salt` - 16-byte salt with BCrypt Base64 encoding
- `CostFactor` - Work factor configuration (4-31)
- `BCryptVersion` - Algorithm version enum (2a/2b)
- Constant-time password comparison for security
- Automatic password truncation at 72 bytes (BCrypt specification)
- SecureRandom for salt generation

### Security
- Constant-time comparison to prevent timing attacks
- Secure memory handling with Password.clear() method
- Rejection of empty passwords
- Default cost factor of 10 for balanced security/performance

[1.0.0]: https://github.com/lucimber/bcrypt-java/releases/tag/v1.0.0