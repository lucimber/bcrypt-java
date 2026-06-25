# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed

- Routine maintenance: refreshed test, build, and CI dependencies — notably
  Bouncy Castle 1.84 (an upstream security release) and Spring Security 7.1.0.
  All are build/test-scope only; the published library has no runtime
  dependencies, so none of these changes affect consumers.

## [1.0.0] - 2025-09-11

### Added

- Initial release of the `lucimber-bcrypt` library — a pure-Java implementation
  of the BCrypt password hashing algorithm with zero runtime dependencies.
- Support for the BCrypt `2a` and `2b` variants.
- Cross-implementation compatibility with Spring Security and Bouncy Castle BCrypt.
- Domain-driven, immutable value-object API:
  - `BCryptService` — main service interface (singleton).
  - `Password` — secure password value object with memory clearing.
  - `Hash` — BCrypt hash parsing and validation.
  - `Salt` — 16-byte salt with BCrypt Base64 encoding.
  - `CostFactor` — work factor configuration (4–31).
  - `BCryptVersion` — algorithm version enum (`2a`/`2b`).
- Java Platform Module System (JPMS) support.
- Self-documenting JAR with usage help and Maven Central publishing configuration.
- SPDX license headers across all sources.

### Security

- Constant-time hash comparison to resist timing attacks.
- Secure memory handling via `Password.clear()`.
- `SecureRandom`-based salt generation.
- Automatic password truncation at 72 bytes per the BCrypt specification.
- Rejection of empty passwords; default cost factor of 10 for a balanced
  security/performance trade-off.

[Unreleased]: https://github.com/lucimber/bcrypt-java/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/lucimber/bcrypt-java/releases/tag/v1.0.0
