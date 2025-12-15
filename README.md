# junit-test-categories

**Enforce Google's hermetic testing practices in Java.**

Block network, filesystem, and subprocess access in unit tests. Validate your test pyramid. Eliminate flaky tests.

[![CI](https://github.com/mikelane/junit-test-categories/actions/workflows/ci.yml/badge.svg)](https://github.com/mikelane/junit-test-categories/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://openjdk.org/)
[![JUnit 5](https://img.shields.io/badge/JUnit-5-green.svg)](https://junit.org/junit5/)

---

## Status: Pre-Release

This project is currently in the documentation and planning phase. No code has been released yet.

See [ROADMAP.md](ROADMAP.md) for development timeline.

---

## Why junit-test-categories?

### The Problem

**Flaky tests are a symptom. Hidden external dependencies are the disease.**

Most test suites suffer from:

- **Flaky tests** - Tests that pass locally but fail in CI due to network timeouts, race conditions, or shared state
- **Slow CI pipelines** - No time budgets means tests grow unbounded
- **Inverted test pyramid** - Too many slow integration tests, too few fast unit tests
- **No enforced boundaries** - "Unit tests" that secretly hit the database, network, or filesystem

### The Solution

junit-test-categories brings Google's battle-tested testing philosophy (from *"Software Engineering at Google"*) to Java:

| What | How |
|------|-----|
| **Categorize tests by size** | `@SmallTest`, `@MediumTest`, `@LargeTest`, `@XLargeTest` |
| **Enforce hermeticity** | Block network, filesystem, database, subprocess in small tests |
| **Enforce time limits** | 1s for small, 5min for medium, 15min for large |
| **Validate distribution** | Maintain healthy 80/15/5 test pyramid |

When a small test tries to access the network, it fails immediately with actionable guidance:

```
[TC001] Network Access Violation
Test:     UserServiceTest.fetchesUserProfile
Category: SMALL

What happened:
  Attempted network connection to api.example.com:443

To fix (choose one):
  1. Mock the HTTP client using Mockito or WireMock
  2. Use dependency injection to provide a fake/stub client
  3. Change test category to @MediumTest (if network is required)

Documentation: https://junit-test-categories.dev/errors/TC001
```

---

## Test Size Categories

| Resource | Small | Medium | Large | XLarge |
|----------|-------|--------|-------|--------|
| **Time Limit** | 1s | 5min | 15min | 15min |
| **Network** | Blocked | Localhost | Allowed | Allowed |
| **Filesystem** | Blocked | Allowed | Allowed | Allowed |
| **Database** | Blocked | Allowed | Allowed | Allowed |
| **Subprocess** | Blocked | Allowed | Allowed | Allowed |
| **Sleep** | Blocked | Allowed | Allowed | Allowed |

**Small tests** must be *hermetic* - completely isolated from external resources. This eliminates flakiness at the source.

---

## Quick Start (Coming Soon)

### Maven

```xml
<dependency>
    <groupId>io.junit.categories</groupId>
    <artifactId>junit-test-categories-junit5</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

### Usage

```java
import io.junit.categories.SmallTest;
import io.junit.categories.MediumTest;
import org.junit.jupiter.api.Test;

@SmallTest  // Hermetic: no network, no filesystem, no database, 1s timeout
class UserValidatorTest {

    @Test
    void validatesEmailFormat() {
        assertThat(UserValidator.isValidEmail("user@example.com")).isTrue();
    }
}

@MediumTest  // Localhost allowed, filesystem allowed, 5min timeout
class UserRepositoryTest {

    @Test
    void persistsUser() {
        // Can use localhost database
    }
}
```

### Configuration

```xml
<!-- pom.xml -->
<plugin>
    <groupId>io.junit.categories</groupId>
    <artifactId>junit-test-categories-maven-plugin</artifactId>
    <version>1.0.0</version>
    <configuration>
        <!-- STRICT, WARN, or OFF -->
        <hermeticityMode>STRICT</hermeticityMode>
        <distributionMode>WARN</distributionMode>
    </configuration>
</plugin>
```

---

## Philosophy: No Escape Hatches

junit-test-categories intentionally does **not** provide per-test override markers like `@AllowNetwork`.

If a test needs network access, it should be marked with `@MediumTest` or larger:

```java
// Wrong: There is no escape hatch
@SmallTest
@AllowNetwork  // This annotation does not exist!
void testApiCall() { }

// Correct: Use the appropriate test size
@MediumTest
void testApiCall() { }

// Or: Mock the dependency
@SmallTest
void testApiCall() {
    when(httpClient.get(any())).thenReturn(mockResponse);
    // ...
}
```

**Why?**

1. **Flaky tests are expensive** - escape hatches become the norm, defeating the purpose
2. **Categories have meaning** - if a "small" test can access the network, it's not really a small test
3. **Encourages better design** - mocking and dependency injection lead to more testable code

---

## Documentation

- [PRD.md](PRD.md) - Full product requirements
- [ROADMAP.md](ROADMAP.md) - Development timeline
- [CONTRIBUTING.md](CONTRIBUTING.md) - How to contribute
- [docs/architecture/design-philosophy.md](docs/architecture/design-philosophy.md) - Design rationale

---

## Related Projects

- [pytest-test-categories](https://github.com/mikelane/pytest-test-categories) - Python reference implementation
- [Software Engineering at Google](https://abseil.io/resources/swe-book) - Source of testing philosophy

---

## License

MIT License - see [LICENSE](LICENSE) for details.

---

## Contributing

We welcome contributions! See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

This project is in early development. The best way to contribute right now is to:

1. Review the [PRD.md](PRD.md) and provide feedback
2. Star the repository to show interest
3. Open issues for questions or suggestions
