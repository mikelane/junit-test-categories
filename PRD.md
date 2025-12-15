# Product Requirements Document: junit-test-categories

## Overview

**junit-test-categories** is a JUnit 5 extension that enforces Google's hermetic testing practices in Java/Kotlin/Scala projects. It is the Java equivalent of [pytest-test-categories](https://github.com/mikelane/pytest-test-categories), bringing the same philosophy, feature set, and developer experience to the JVM ecosystem.

### Vision

Eliminate flaky tests in Java projects by enforcing test hermeticity at runtime, validating test pyramid distribution, and providing actionable error messages that guide developers toward better testing practices.

### Problem Statement

Java test suites suffer from the same problems as Python test suites:

1. **Flaky tests** - Tests that pass locally but fail in CI due to network timeouts, race conditions, or shared state
2. **Slow CI pipelines** - No time budgets means tests grow unbounded
3. **Inverted test pyramid** - Too many slow integration tests, too few fast unit tests
4. **No enforced boundaries** - "Unit tests" that secretly hit the database, network, or filesystem

The root cause is tests with hidden external dependencies that make them non-deterministic.

### Solution

junit-test-categories brings Google's battle-tested testing philosophy (from *"Software Engineering at Google"*) to Java:

| What | How |
|------|-----|
| **Categorize tests by size** | `@SmallTest`, `@MediumTest`, `@LargeTest`, `@XLargeTest` |
| **Enforce hermeticity** | Block network, filesystem, database, subprocess, sleep in small tests |
| **Enforce time limits** | 1s for small, 5min for medium, 15min for large/xlarge |
| **Validate distribution** | Maintain healthy 80/15/5 test pyramid |

---

## Goals and Non-Goals

### Goals

1. **Feature parity with pytest-test-categories** - Same philosophy, same enforcement, same developer experience
2. **Seamless JUnit 5 integration** - Works with existing test infrastructure
3. **Actionable error messages** - Every violation includes remediation guidance
4. **Gradual adoption path** - off/warn/strict modes for migration
5. **Build tool integration** - First-class Gradle and Maven support
6. **Zero configuration default** - Works out of the box with sensible defaults
7. **Open source** - MIT license, community-driven development

### Non-Goals

1. **TestNG support** - JUnit 5 only (TestNG could be a future extension)
2. **Configurable time limits** - Fixed by design per Google's standards
3. **Per-test escape hatches** - No `@AllowNetwork` or similar markers
4. **Backward compatibility with JUnit 4** - JUnit 5 only

---

## Test Size Categories

| Resource | Small | Medium | Large | XLarge |
|----------|-------|--------|-------|--------|
| **Time Limit** | 1s | 5min | 15min | 15min |
| **Network** | Blocked | Localhost only | Allowed | Allowed |
| **Filesystem** | Blocked | Allowed | Allowed | Allowed |
| **Database** | Blocked | Allowed | Allowed | Allowed |
| **Subprocess** | Blocked | Allowed | Allowed | Allowed |
| **Sleep** | Blocked | Allowed | Allowed | Allowed |

**Small tests** must be *hermetic* - completely isolated from external resources. This eliminates flakiness at the source.

---

## User Stories

### US-1: Mark tests with size categories

**As a** developer
**I want to** mark my tests with size annotations
**So that** the framework knows what constraints to enforce

```java
@SmallTest
class UserValidatorTest {
    @Test
    void validatesEmailFormat() {
        assertThat(UserValidator.isValidEmail("user@example.com")).isTrue();
    }
}

@MediumTest
class UserRepositoryTest {
    @Test
    void persistsUser() {
        // Can use localhost database
    }
}
```

### US-2: Receive actionable error on network violation

**As a** developer
**I want to** see a clear error message when my small test tries to access the network
**So that** I know exactly how to fix it

```
[TC001] Network Access Violation
Test:     UserServiceTest.fetchesUserProfile
Category: SMALL
Location: com.example.UserServiceTest (UserServiceTest.java:42)

What happened:
  Attempted network connection to api.example.com:443

To fix (choose one):
  1. Mock the HTTP client using Mockito or WireMock
  2. Use dependency injection to provide a fake/stub client
  3. Change test category to @MediumTest (if network is required)

Documentation: https://junit-test-categories.dev/errors/TC001
```

### US-3: Gradual migration with warn mode

**As a** tech lead adopting this in an existing codebase
**I want to** see violations as warnings without failing the build
**So that** I can identify and fix issues incrementally

```kotlin
// build.gradle.kts
testCategories {
    enforcement = WARN  // Log violations, don't fail
}
```

### US-4: Enforce test distribution

**As a** engineering manager
**I want to** ensure our test suite maintains a healthy pyramid
**So that** we don't drift toward slow, flaky integration tests

```kotlin
testCategories {
    distributionEnforcement = STRICT
    // Fails if: <75% small, >20% medium, or >8% large/xlarge
}
```

### US-5: Generate test distribution reports

**As a** CI engineer
**I want to** export test distribution data as JSON
**So that** I can track trends and integrate with dashboards

```bash
./gradlew testDistributionReport --format=json
```

### US-6: Use base test classes

**As a** developer
**I want to** inherit from base test classes
**So that** I get the size annotation automatically

```java
class UserValidatorTest extends SmallTest {
    @Test
    void validatesEmailFormat() {
        // Automatically marked as @SmallTest
    }
}
```

---

## Functional Requirements

### FR-1: Annotations

| Annotation | Time Limit | Network | Filesystem | Database | Subprocess | Sleep |
|------------|-----------|---------|------------|----------|------------|-------|
| `@SmallTest` | 1s | Blocked | Blocked | Blocked | Blocked | Blocked |
| `@MediumTest` | 5min | Localhost | Allowed | Allowed | Allowed | Allowed |
| `@LargeTest` | 15min | Allowed | Allowed | Allowed | Allowed | Allowed |
| `@XLargeTest` | 15min | Allowed | Allowed | Allowed | Allowed | Allowed |

- Annotations can be applied at class or method level
- Method-level annotations override class-level
- Tests without annotations default to behavior defined by configuration (default: no enforcement, warn about missing marker)

### FR-2: Network Isolation

**Small tests:**
- Block all `java.net.Socket.connect()` calls
- Block `java.net.URL.openConnection()`
- Block `java.net.HttpURLConnection` usage
- Block `java.nio.channels.SocketChannel.connect()`

**Medium tests:**
- Allow connections to localhost (127.0.0.1, ::1, localhost)
- Block all external network connections

**Large/XLarge tests:**
- Allow all network connections

### FR-3: Filesystem Isolation

**Small tests:**
- Block `java.io.File` constructor and methods
- Block `java.nio.file.Files.*` methods
- Block `java.io.FileInputStream`, `FileOutputStream`
- Block `java.io.RandomAccessFile`

**Medium/Large/XLarge tests:**
- Allow all filesystem operations

### FR-4: Database Isolation

**Small tests:**
- Block `java.sql.DriverManager.getConnection()`
- Block connection pool initialization (HikariCP, etc.)
- Block in-memory databases (H2, HSQLDB in-memory mode)

**Medium/Large/XLarge tests:**
- Allow all database connections

### FR-5: Subprocess Isolation

**Small tests:**
- Block `java.lang.ProcessBuilder.start()`
- Block `java.lang.Runtime` process execution methods

**Medium/Large/XLarge tests:**
- Allow subprocess creation

### FR-6: Sleep Isolation

**Small tests:**
- Block `java.lang.Thread.sleep()`
- Block `java.util.concurrent.TimeUnit.sleep()`
- Block `java.lang.Object.wait()` with timeout

**Medium/Large/XLarge tests:**
- Allow sleep operations

### FR-7: Timing Enforcement

- Track wall-clock time for each test
- Fail test if it exceeds the time limit for its category
- Time limits are fixed and not configurable:
  - Small: 1 second
  - Medium: 5 minutes (300 seconds)
  - Large: 15 minutes (900 seconds)
  - XLarge: 15 minutes (900 seconds)

### FR-8: Distribution Validation

- Count tests by category after collection
- Calculate percentages
- Validate against targets:
  - Small: 80% (tolerance +/-5%)
  - Medium: 15% (tolerance +/-5%)
  - Large/XLarge combined: 5% (tolerance +/-3%)
- Targets and tolerances are fixed and not configurable

### FR-9: Enforcement Modes

| Mode | Behavior |
|------|----------|
| `OFF` | No enforcement, plugin disabled |
| `WARN` | Log violations as warnings, tests continue |
| `STRICT` | Fail tests on violations |

### FR-10: Reporting

**Basic report:** Summary statistics by size category
**Detailed report:** Per-test listings with outcomes and timings
**JSON report:** Machine-readable format for CI integration

---

## Non-Functional Requirements

### NFR-1: Performance

- Agent instrumentation overhead < 5% on test execution time
- No impact on production code (test-scoped only)
- Compatible with parallel test execution

### NFR-2: Compatibility

- JUnit 5.8+ (Jupiter)
- Java 11+ (LTS versions: 11, 17, 21)
- Kotlin 1.8+
- Gradle 7.0+
- Maven 3.6+
- Compatible with JaCoCo (agent ordering documented)
- Compatible with Mockito inline mock maker
- Compatible with Spring Boot Test

### NFR-3: Developer Experience

- Zero configuration required for basic usage
- Clear, actionable error messages
- Comprehensive documentation
- Working examples for common frameworks

---

## Architecture

### Components

```
                     junit-test-categories
|-----------------------------------------------------------------|
|                                                                  |
|  +--------------+  +--------------+  +--------------+            |
|  | annotations  |  |    core      |  |    agent     |            |
|  |              |  |              |  |              |            |
|  | @SmallTest   |  | TestSize     |  | ByteBuddy    |            |
|  | @MediumTest  |  | Enforcement  |  | interceptors |            |
|  | @LargeTest   |  | Violations   |  |              |            |
|  | @XLargeTest  |  |              |  |              |            |
|  +--------------+  +--------------+  +--------------+            |
|                                                                  |
|  +--------------+  +--------------+  +--------------+            |
|  |   junit5     |  |gradle-plugin |  | maven-plugin |            |
|  |              |  |              |  |              |            |
|  | Extension    |  | DSL config   |  | XML config   |            |
|  | Lifecycle    |  | Agent wiring |  | Agent wiring |            |
|  | Reporting    |  | Tasks        |  | Goals        |            |
|  +--------------+  +--------------+  +--------------+            |
|                                                                  |
|-----------------------------------------------------------------|
```

### Module Responsibilities

| Module | Responsibility | Dependencies |
|--------|---------------|--------------|
| `annotations` | Size marker annotations | None (pure Java) |
| `core` | Domain types, violations, enforcement logic | annotations |
| `agent` | ByteBuddy-based runtime interception | core, ByteBuddy |
| `junit5` | JUnit 5 Extension, lifecycle hooks | core, JUnit Jupiter |
| `gradle-plugin` | Gradle integration, DSL, tasks | junit5, agent |
| `maven-plugin` | Maven integration, configuration, goals | junit5, agent |

---

## Design Philosophy

### No Escape Hatches

junit-test-categories intentionally does **not** provide per-test override markers like `@AllowNetwork`. This is a deliberate design choice.

If a test needs network access, it should be marked with `@MediumTest` or larger. The categories define contracts - a small test is hermetic or it's not a small test.

See [Design Philosophy](docs/architecture/design-philosophy.md) for full rationale.

### Fixed Constraints

Time limits, distribution targets, and tolerance bands are **not configurable**. This ensures:

1. Consistent meaning across all projects using the library
2. No "configuration drift" where teams loosen constraints
3. Alignment with Google's published standards

### Gradual Adoption

The enforcement modes (OFF -> WARN -> STRICT) exist for migration, not permanent bypass. The expected journey:

1. **Week 1:** OFF - Explore, see what exists
2. **Weeks 2-4:** WARN - Fix violations incrementally
3. **Week 5+:** STRICT - Enforced, violations fail builds

---

## Success Metrics

| Metric | Target |
|--------|--------|
| Feature parity with pytest-test-categories | 100% |
| Test coverage | 100% |
| Documentation coverage | All public APIs documented |
| Example projects | Spring Boot, Gradle Kotlin, Maven |
| Adoption | 100+ GitHub stars in first 6 months |

---

## Open Questions

1. **Package namespace:** `io.testcategories` vs `dev.testcategories` vs `org.junitpioneer.testcategories`?
2. **Should we contribute to JUnit Pioneer instead of creating a new project?**
3. **Kotlin DSL vs Java for Gradle plugin implementation?**

---

## References

- [pytest-test-categories](https://github.com/mikelane/pytest-test-categories) - Python reference implementation
- [Software Engineering at Google](https://abseil.io/resources/swe-book) - Testing chapter
- [JUnit 5 Extension Model](https://junit.org/junit5/docs/current/user-guide/#extensions)
- [ByteBuddy](https://bytebuddy.net/) - Runtime bytecode manipulation
- [JaCoCo Agent](https://www.jacoco.org/jacoco/trunk/doc/agent.html) - Reference for Java agent architecture
