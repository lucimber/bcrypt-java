# API Documentation

## Core Classes

### BCryptService

The main service class for BCrypt operations. Uses singleton pattern.

```java
BCryptService service = BCryptService.getInstance();
```

#### Methods

##### `hash(Password password)`
Hashes a password with default settings (2b variant, cost factor 10).

##### `hash(Password password, BCryptVersion version)`
Hashes a password with specified BCrypt version.

##### `hash(Password password, BCryptVersion version, CostFactor costFactor)`
Hashes a password with specified version and cost factor.

##### `hash(Password password, BCryptVersion version, CostFactor costFactor, Salt salt)`
Hashes a password with all parameters specified.

##### `verify(Password password, Hash hash)`
Verifies a password against a hash. Returns `true` if the password matches.

### Value Objects

#### Password

Represents a plain text password.

```java
// From String
Password password = new Password("myPassword");

// From char array (more secure)
char[] chars = {'p', 'a', 's', 's'};
Password password = new Password(chars);

// Clear from memory when done
password.clear();
```

**Note**: Passwords longer than 72 bytes are automatically truncated per BCrypt specification.

#### Hash

Represents a BCrypt hash string.

```java
// Parse existing hash
Hash hash = new Hash("$2a$10$...");

// Get components
BCryptVersion version = hash.getVersion();
CostFactor cost = hash.getCostFactor();
String saltString = hash.getSalt();
String hashString = hash.getValue();
```

#### Salt

Represents a 16-byte salt value.

```java
// Generate random salt
Salt salt = Salt.generateRandom();

// From byte array
byte[] bytes = new byte[16];
Salt salt = new Salt(bytes);

// From BCrypt Base64 string
Salt salt = Salt.fromBCryptString("saltString");
```

#### CostFactor

Represents the cost factor (4-31).

```java
// Create with specific value
CostFactor cost = new CostFactor(12);

// Get default (10)
CostFactor cost = CostFactor.defaultCost();

// Get value
int value = cost.getValue();
```

#### BCryptVersion

Enum representing BCrypt versions.

```java
BCryptVersion.VERSION_2A  // $2a$ prefix
BCryptVersion.VERSION_2B  // $2b$ prefix

// Get prefix
String prefix = version.getPrefix(); // "2a" or "2b"
```

## Example: Complete Password Management

```java
import com.lucimber.crypto.bcrypt.*;

public class PasswordManager {
    private final BCryptService bcryptService = BCryptService.getInstance();
    
    public String hashPassword(String rawPassword) {
        Password password = new Password(rawPassword);
        try {
            // Use 2b variant with cost factor 12 for better security
            Hash hash = bcryptService.hash(
                password, 
                BCryptVersion.VERSION_2B, 
                new CostFactor(12)
            );
            return hash.getValue();
        } finally {
            password.clear(); // Clear password from memory
        }
    }
    
    public boolean verifyPassword(String rawPassword, String storedHash) {
        Password password = new Password(rawPassword);
        try {
            Hash hash = new Hash(storedHash);
            return bcryptService.verify(password, hash);
        } finally {
            password.clear();
        }
    }
}
```

## Thread Safety

- `BCryptService` is thread-safe (singleton)
- All value objects (`Password`, `Hash`, `Salt`, `CostFactor`) are immutable and thread-safe
- `Password.clear()` modifies internal state and should be called when the password is no longer needed

## Performance Considerations

The cost factor exponentially affects processing time:
- Cost 10: ~0.1 seconds
- Cost 12: ~0.4 seconds  
- Cost 14: ~1.6 seconds
- Cost 16: ~6.4 seconds

Choose based on your security requirements and acceptable latency.