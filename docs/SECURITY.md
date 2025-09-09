# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |

## Reporting a Vulnerability

If you discover a security vulnerability, please do the following:

1. **Do NOT** create a public GitHub issue
2. Email the security team at: devdev@lucimber.com
3. Include:
   - Description of the vulnerability
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if any)

We will respond within 48 hours and work with you to understand and address the issue.

## Security Best Practices

### For Users

1. **Cost Factor Selection**
   - Use cost factor 10 or higher for production
   - Consider 12+ for highly sensitive data
   - Balance security with performance requirements

2. **Password Handling**
   ```java
   Password password = new Password(userInput);
   try {
       // Use password
       Hash hash = bcryptService.hash(password);
   } finally {
       password.clear(); // Always clear sensitive data
   }
   ```

3. **Hash Storage**
   - Store complete hash string including version and salt
   - Never store passwords in plain text
   - Use secure database connections

4. **Version Selection**
   - Prefer VERSION_2B for new applications
   - VERSION_2A for compatibility with older systems

### Security Features

1. **Constant-Time Comparison**
   - Prevents timing attacks during verification
   - All hash comparisons use constant-time algorithm

2. **Salt Generation**
   - Uses `SecureRandom` for cryptographic quality
   - 16 bytes (128 bits) of entropy
   - Unique salt for each password

3. **Password Truncation**
   - Automatically truncates at 72 bytes
   - Consistent with BCrypt specification
   - Prevents buffer overflow attacks

4. **Memory Management**
   - `Password.clear()` zeros out memory
   - Defensive copying prevents external modification
   - No logging of sensitive data

### Known Limitations

1. **72-Byte Maximum**
   - BCrypt truncates passwords at 72 bytes
   - For longer passwords, consider pre-hashing with SHA-256

2. **Cost Factor Limits**
   - Maximum cost factor is 31
   - Higher values may cause excessive CPU usage
   - DoS potential with very high cost factors

3. **Unicode Handling**
   - Passwords are UTF-8 encoded before hashing
   - Different Unicode normalizations may produce different hashes
   - Consider normalizing input before hashing

### Recommendations

1. **Regular Updates**
   - Keep the library updated
   - Monitor security advisories
   - Review changelog for security fixes

2. **Defense in Depth**
   - Use BCrypt as part of broader security strategy
   - Implement rate limiting
   - Monitor for brute force attempts
   - Use secure communication channels

3. **Testing**
   - Verify compatibility with your stack
   - Test with various password lengths and characters
   - Validate cost factor performance

## Cryptographic Details

- **Algorithm**: BCrypt (Blowfish-based)
- **Key Derivation**: Expensive key schedule (EksBlowfish)
- **Salt Length**: 128 bits
- **Output Length**: 184 bits
- **Work Factor**: 2^cost iterations

## Compliance

This implementation is suitable for:
- OWASP password storage recommendations
- PCI DSS password requirements
- GDPR data protection standards

## Security Audit

The implementation has been:
- Tested against known test vectors
- Verified compatible with Spring Security
- Validated against timing attacks
- Reviewed for memory safety

## Contact

For security concerns: devdev@lucimber.com