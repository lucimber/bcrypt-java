# Contributing to BCrypt Java Library

We welcome contributions! This document provides guidelines for contributing to the project.

## Code of Conduct

By participating in this project, you agree to abide by our Code of Conduct:
- Be respectful and inclusive
- Welcome newcomers and help them get started
- Focus on constructive criticism
- Respect differing viewpoints and experiences

## How to Contribute

### Reporting Issues

1. Check existing issues to avoid duplicates
2. Use issue templates when available
3. Provide clear description and steps to reproduce
4. Include version information and environment details

### Suggesting Enhancements

1. Check if the enhancement has been suggested
2. Open a discussion first for major changes
3. Explain the use case and benefits
4. Consider backward compatibility

### Pull Requests

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Sign your commits (Required)**
   
   All commits must be signed off according to the Developer Certificate of Origin (DCO):
   ```bash
   git commit -s -m "Your commit message"
   ```
   
   This adds `Signed-off-by: Your Name <your@email.com>` to your commits.
   See our [DCO documentation](DCO.md) for detailed instructions.

4. **Follow coding standards**
   - Java 17+ features
   - Clear, self-documenting code
   - Javadoc for all public methods
   - Consistent formatting

5. **Write tests**
   - Unit tests for new functionality
   - Integration tests for compatibility
   - Maintain 100% test pass rate

6. **Update documentation**
   - Update README if needed
   - Add/update Javadoc
   - Update relevant docs/ files

7. **Commit with clear messages and DCO sign-off**
   ```bash
   git commit -s -m "Add feature: brief description
   
   Detailed explanation of what and why"
   ```
   
   Note: The `-s` flag automatically adds the required DCO sign-off.

8. **Push and create PR**
   - Reference any related issues
   - Describe changes clearly
   - Note any breaking changes

## Development Setup

### Prerequisites

- Java 17 or higher
- Gradle 8.5+
- Git

### Building

```bash
git clone https://github.com/lucimber/bcrypt-java.git
cd bcrypt-java/bcrypt
./gradlew build
```

### Running Tests

```bash
# All tests
./gradlew test

# Specific test class
./gradlew test --tests BCryptServiceTest

# Integration tests only
./gradlew test --tests "*IntegrationTest"
```

### Code Style

- Use 4 spaces for indentation
- Maximum line length: 120 characters
- Braces on same line for classes and methods
- Meaningful variable and method names
- Avoid abbreviations

Example:
```java
/**
 * Verifies a password against a BCrypt hash.
 * 
 * @param password the password to verify
 * @param hash the hash to verify against
 * @return true if the password matches
 */
public boolean verify(Password password, Hash hash) {
    // Implementation
}
```

## Testing Guidelines

### Unit Tests

- Test each public method
- Cover edge cases
- Test error conditions
- Use descriptive test names

```java
@Test
@DisplayName("Should reject empty password")
void shouldRejectEmptyPassword() {
    assertThrows(IllegalArgumentException.class, 
        () -> new Password(""));
}
```

### Integration Tests

- Test compatibility with Spring Security
- Test compatibility with Bouncy Castle
- Verify known test vectors
- Test cross-compatibility

## Documentation

### Javadoc

All public classes and methods must have Javadoc:

```java
/**
 * Brief description.
 * 
 * Detailed explanation if needed.
 * 
 * @param paramName description
 * @return description
 * @throws ExceptionType when this occurs
 */
```

### Markdown Documentation

- Use clear, concise language
- Include code examples
- Keep files focused on single topics
- Update when API changes

## Release Process

1. Ensure all tests pass
2. Update version in build.gradle.kts
3. Update CHANGELOG.md
4. Create release tag
5. Build and publish artifacts

## Questions?

- Open a GitHub Discussion
- Check existing documentation
- Review closed issues and PRs

## Recognition

Contributors will be recognized in:
- GitHub contributors page
- Release notes for significant contributions
- CONTRIBUTORS.md file (for major contributors)

Thank you for contributing to BCrypt Java Library!