# Architecture

## Design Principles

This BCrypt implementation follows Domain-Driven Design (DDD) principles with a focus on:

1. **Value Objects**: Immutable objects representing domain concepts
2. **Zero Dependencies**: No external runtime dependencies
3. **Security First**: Secure defaults and memory handling
4. **Clean Architecture**: Clear separation of concerns

## Domain Model

### Value Objects

All core domain concepts are modeled as immutable value objects:

- **Password**: Represents a plain text password
  - Validates input (non-null, non-empty)
  - Handles UTF-8 encoding
  - Truncates at 72 bytes per BCrypt spec
  - Provides secure memory clearing

- **Hash**: Represents a complete BCrypt hash string
  - Parses and validates BCrypt format
  - Extracts components (version, cost, salt, hash)
  - Immutable after creation

- **Salt**: Represents a 16-byte salt value
  - Generates cryptographically secure random salts
  - Handles BCrypt Base64 encoding/decoding
  - Validates length constraints

- **CostFactor**: Represents the work factor (4-31)
  - Validates range constraints
  - Provides sensible defaults
  - Type-safe representation

- **BCryptVersion**: Enum for BCrypt variants
  - VERSION_2A: Original BCrypt
  - VERSION_2B: Fixed version (recommended)

### Service Layer

- **BCryptService**: Main service interface
  - Singleton pattern for efficiency
  - Stateless operations
  - Thread-safe
  - Delegates to BCryptEngine for crypto operations

### Internal Components

- **BCryptEngine**: Core cryptographic implementation
  - Based on reference BCrypt implementation
  - Handles Blowfish cipher operations
  - Expensive key schedule (EksBlowfish)
  - BCrypt Base64 encoding

- **BCryptBase64**: Custom Base64 encoding
  - Uses BCrypt-specific alphabet
  - Different from standard Base64
  - Used for salt and hash encoding

## Package Structure

```
com.lucimber.crypto.bcrypt/
├── BCryptService.java       # Public API
├── Password.java            # Value object
├── Hash.java               # Value object
├── Salt.java               # Value object
├── CostFactor.java         # Value object
├── BCryptVersion.java      # Enum
├── BCryptEngine.java       # Internal crypto (package-private)
└── BCryptBase64.java       # Internal encoding (package-private)
```

## Design Decisions

### Why Singleton Service?

The BCryptService uses a singleton pattern because:
- Stateless operations don't require multiple instances
- Reduces object creation overhead
- Thread-safe implementation
- Familiar pattern for service classes

### Why Value Objects?

Value objects provide:
- Type safety (can't pass salt where password expected)
- Validation at construction time
- Immutability for thread safety
- Clear domain modeling

### Why Package-Private Internals?

BCryptEngine and BCryptBase64 are package-private because:
- Users should only interact with the service API
- Reduces API surface area
- Allows internal refactoring without breaking changes
- Follows principle of least exposure

### Password Truncation vs Rejection

The implementation truncates passwords > 72 bytes rather than rejecting them because:
- Matches behavior of OpenBSD BCrypt
- Compatible with Spring Security and other implementations
- Prevents user confusion
- More forgiving API

## Security Considerations

1. **Constant-Time Comparison**: Hash verification uses constant-time comparison to prevent timing attacks

2. **Secure Random**: Salt generation uses `SecureRandom` for cryptographic quality

3. **Memory Clearing**: Password class provides `clear()` method to zero out sensitive data

4. **No Logging**: Implementation avoids logging sensitive data

5. **Defensive Copying**: All value objects use defensive copying to prevent external modification

## Testing Strategy

- **Unit Tests**: Each value object has comprehensive unit tests
- **Integration Tests**: Compatibility tests with Spring Security and Bouncy Castle
- **Test Coverage**: 100% test pass rate with 79 tests
- **Known Test Vectors**: Verified against official BCrypt test vectors