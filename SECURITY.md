# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.x.x   | :white_check_mark: |
| < 1.0   | :x: (pre-release)  |

## Reporting a Vulnerability

**Please do NOT report security vulnerabilities through public GitHub issues.**

Instead, please report security vulnerabilities by emailing the maintainers directly.

### What to Include

When reporting a vulnerability, please include:

1. **Description** - A clear description of the vulnerability
2. **Impact** - What an attacker could potentially achieve
3. **Steps to Reproduce** - Detailed steps to reproduce the issue
4. **Affected Versions** - Which versions are affected
5. **Suggested Fix** - If you have one (optional)

### Response Timeline

- **Acknowledgment**: Within 48 hours
- **Initial Assessment**: Within 7 days
- **Fix Timeline**: Depends on severity
  - Critical: 7 days
  - High: 14 days
  - Medium: 30 days
  - Low: 60 days

### Disclosure Policy

We follow coordinated disclosure:

1. Reporter notifies us privately
2. We confirm the vulnerability
3. We develop and test a fix
4. We release the fix
5. We publicly disclose the vulnerability (crediting the reporter if desired)

We ask that you give us reasonable time to address vulnerabilities before public disclosure.

## Security Best Practices for Users

### Agent Configuration

When using the ByteBuddy agent, ensure:

- The agent JAR is from a trusted source (Maven Central or GitHub Releases)
- Verify checksums when downloading manually
- Keep the library updated to receive security patches

### Dependency Management

This library depends on:

- **JUnit 5** - Testing framework
- **ByteBuddy** - Bytecode manipulation

Keep these dependencies updated. Use tools like:

- `mvn versions:display-dependency-updates`
- Dependabot (enabled on this repository)
- OWASP Dependency-Check

## Security Features

This library is designed to improve test quality and does not:

- Access the network (the library itself is hermetic)
- Store or transmit any data
- Require elevated privileges
- Execute arbitrary code

The ByteBuddy agent intercepts JDK methods only during test execution to enforce hermeticity constraints.
