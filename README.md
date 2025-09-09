# BCrypt Java Library

A zero-dependency, pure Java implementation of the BCrypt password hashing algorithm with full compatibility with Spring Security and other major implementations.

## Features

- **Zero runtime dependencies** - Pure Java implementation
- **BCrypt 2a and 2b variants** support
- **Spring Security compatible** - Integration test coverage
- **Bouncy Castle compatible** - Full compatibility with helper methods
- **Domain-driven design** - Clean architecture with value objects
- **Secure by default** - Constant-time comparison, secure memory handling
- **Well tested** - Comprehensive unit and integration tests

## Quick Start

### Installation

#### Gradle
```gradle
dependencies {
    implementation 'com.lucimber.crypto:bcrypt:1.0.0'
}
```

#### Maven
```xml
<dependency>
    <groupId>com.lucimber.crypto</groupId>
    <artifactId>bcrypt</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Basic Usage

```java
import com.lucimber.crypto.bcrypt.*;

// Get the service instance
BCryptService bcryptService = BCryptService.getInstance();

// Hash a password
Password password = new Password("mySecretPassword");
Hash hash = bcryptService.hash(password);
String hashString = hash.getValue(); // $2b$10$...

// Verify a password
boolean isValid = bcryptService.verify(password, hash);
```

### Advanced Usage

```java
// Use specific BCrypt version (2a or 2b)
Hash hash = bcryptService.hash(password, BCryptVersion.VERSION_2A);

// Use custom cost factor (4-31, default is 10)
CostFactor costFactor = new CostFactor(12);
Hash hash = bcryptService.hash(password, BCryptVersion.VERSION_2B, costFactor);

// Use specific salt (for testing/compatibility)
Salt salt = Salt.generateRandom();
Hash hash = bcryptService.hash(password, BCryptVersion.VERSION_2A, costFactor, salt);
```

## Documentation

- [API Documentation](docs/API.md) - Detailed API reference
- [Architecture](docs/ARCHITECTURE.md) - Domain-driven design and architecture decisions
- [Security](docs/SECURITY.md) - Security considerations and best practices
- [Contributing](docs/CONTRIBUTING.md) - How to contribute to the project

## Compatibility

This implementation is fully compatible with:
- Spring Security BCrypt
- Bouncy Castle (with helper methods)
- OpenBSD BCrypt
- jBCrypt

Verified test vector:
- Password: `abc123xyz`
- Hash: `$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW`

## Requirements

- Java 17 or higher
- Gradle 8.5+ (for building)

## Building from Source

```bash
git clone https://github.com/lucimber/bcrypt-java.git
cd bcrypt-java
./gradlew build
```

Run tests:
```bash
./gradlew test
```

## Project Structure

```
bcrypt/
├── src/
│   ├── main/java/com/lucimber/crypto/bcrypt/
│   │   ├── BCryptService.java      # Main service interface
│   │   ├── Password.java           # Password value object
│   │   ├── Hash.java              # Hash value object
│   │   ├── Salt.java              # Salt value object
│   │   └── ...
│   └── test/java/                  # Comprehensive test suite
├── docs/                           # Detailed documentation
├── build.gradle.kts               # Build configuration
└── README.md
```

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Contributing

We welcome contributions! Please see our [Contributing Guide](docs/CONTRIBUTING.md) for details.

**Note:** All contributions require DCO sign-off. Add `-s` flag when committing: `git commit -s`

## Security

For security concerns, please see our [Security Policy](docs/SECURITY.md).

## Support

- **Issues**: [GitHub Issues](https://github.com/lucimber/bcrypt-java/issues)
- **Discussions**: [GitHub Discussions](https://github.com/lucimber/bcrypt-java/discussions)

## Acknowledgments

This implementation is based on the BCrypt algorithm designed by Niels Provos and David Mazières, as described in their paper "A Future-Adaptable Password Scheme" presented at USENIX 1999.