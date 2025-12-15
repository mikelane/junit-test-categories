# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**junit-test-categories** is a JUnit 5 extension that enforces Google's hermetic testing practices in Java/Kotlin/Scala projects. It is the Java equivalent of [pytest-test-categories](https://github.com/mikelane/pytest-test-categories).

### Core Features

1. **Test size markers**: `@SmallTest`, `@MediumTest`, `@LargeTest`, `@XLargeTest`
2. **Hermeticity enforcement**: Block network, filesystem, database, subprocess, sleep in small tests
3. **Time limits**: 1s (small), 5min (medium), 15min (large/xlarge)
4. **Distribution validation**: Enforce 80/15/5 test pyramid
5. **Actionable errors**: Every violation includes remediation guidance

### Reference Implementation

The Python reference implementation is at `/Users/mikelane/dev/pytest-test-categories`. When implementing features, consult the Python version for:
- Design philosophy
- Error message formats
- Feature behavior
- Test patterns

## Project Status

This project is in the **initial documentation phase**. No code has been written yet.

See [PRD.md](PRD.md) for full requirements and [ROADMAP.md](ROADMAP.md) for development phases.

## Architecture

### Module Structure

```
junit-test-categories/
├── annotations/          # @SmallTest, @MediumTest, etc. (pure Java, no deps)
├── core/                 # TestSize, violations, enforcement logic
├── agent/                # ByteBuddy-based runtime interception
├── junit5/               # JUnit 5 Extension integration
├── maven-plugin/         # Maven plugin (PRIMARY)
├── gradle-plugin/        # Gradle plugin with DSL
├── docs/                 # Documentation
└── examples/             # Spring Boot, Gradle, Maven examples
```

### Key Technologies

| Technology | Purpose |
|------------|---------|
| **JUnit 5 Jupiter** | Test framework integration |
| **ByteBuddy** | Runtime bytecode manipulation for interception |
| **Maven** | Primary build tool, plugin development |
| **Gradle** | Secondary build tool support |

**Note:** Maven is the primary target because the initial adopter (GDIT) uses Maven for enterprise Java.

### How Interception Works

The agent uses ByteBuddy to intercept JDK methods at runtime:

1. **Network**: `Socket.connect()`, `URL.openConnection()`, `SocketChannel.connect()`
2. **Filesystem**: `Files.*`, `File.*`, `FileInputStream`, `FileOutputStream`
3. **Database**: `DriverManager.getConnection()`
4. **Subprocess**: `ProcessBuilder.start()`, `Runtime` process methods
5. **Sleep**: `Thread.sleep()`, `TimeUnit.sleep()`, `Object.wait()`

The JUnit 5 Extension sets up an `EnforcementContext` (thread-local) before each test, and the interceptors check against it.

## Development Standards

### Test-Driven Development (MANDATORY)

This project follows strict TDD. All code changes require tests first.

**Uncle Bob's Three Rules:**
1. You may not write production code unless it is to make a failing unit test pass
2. You may not write more of a unit test than is sufficient to fail
3. You may not write more production code than is sufficient to pass the one failing unit test

### Test Naming

- Avoid "should" in test names
- Use declarative statements: `validatesEmailFormat()` not `shouldValidateEmailFormat()`
- Tests must be simple - no loops or branching in test code

### Code Style

- Java 11+ syntax
- Google Java Style Guide
- Meaningful names over comments
- Small, focused classes and methods

### Git Workflow

1. **Create GitHub issue first** for ALL work
2. **Create feature branch** from main
3. **Open PR early** and keep it updated
4. **Link PR to issue** using "Fixes #N" in description
5. **No direct commits to main**

### Commit Messages

- Use conventional commits: `feat:`, `fix:`, `docs:`, `test:`, `refactor:`
- Do NOT add co-authored or attribution lines
- Keep messages concise and meaningful

## Design Philosophy

### Developer Experience is a First-Class Concern

**DX is equal to correctness and performance.** See [ADR-004](docs/architecture/decisions/ADR-004-developer-experience-principles.md) for full principles.

Key principles:
1. **Explicit over implicit** (pragmatically) - Users should always know what to expect
2. **Fail fast, fail clearly** - Every error includes what happened, why it matters, and how to fix it
3. **Progressive disclosure** - Simple things simple, complex things possible
4. **No surprises** - Behavior matches expectations formed by the API
5. **Debuggability** - Verbose mode, clear logging, actionable reports
6. **Graceful degradation** - Partial adoption works, gradual migration supported
7. **Minimal footprint** - Annotations module has ZERO dependencies

### No Escape Hatches

There are intentionally NO per-test override markers like `@AllowNetwork`. This is a core design principle.

If a test needs network access, it should be `@MediumTest` or larger. Categories define contracts.

### Fixed Time Limits, Configurable Distribution

**Time limits are fixed** (1s/5min/15min) - these define the meaning of test sizes.

**Distribution targets are configurable** (default 80/15/5 with 5% tolerance) - teams can customize for gradual adoption. See [ADR-002](docs/architecture/decisions/ADR-002-distribution-configurability.md).

### Gradual Adoption

Separate enforcement modes for hermeticity and distribution (see [ADR-003](docs/architecture/decisions/ADR-003-enforcement-modes.md)):
- `OFF` - Initial exploration
- `WARN` - Migration period (fix violations incrementally)
- `STRICT` - Production (violations fail builds)

## Key Files

| File | Purpose |
|------|---------|
| `PRD.md` | Full product requirements document |
| `ROADMAP.md` | Development phases and milestones |
| `CONTRIBUTING.md` | Contribution guidelines |
| `docs/architecture/design-philosophy.md` | Design philosophy (from pytest-test-categories) |

## Commands (Once Project is Set Up)

### Maven (Primary)

```bash
# Build all modules
mvn clean install

# Run tests
mvn test

# Run tests with coverage
mvn test jacoco:report

# Check code style
mvn spotless:check

# Apply code style fixes
mvn spotless:apply

# Install to local repository (for testing)
mvn install -DskipTests
```

### Gradle (Secondary)

```bash
# Build all modules
./gradlew build

# Run tests
./gradlew test

# Run tests with coverage
./gradlew test jacocoTestReport

# Check code style
./gradlew spotlessCheck

# Apply code style fixes
./gradlew spotlessApply

# Publish to local Maven repository
./gradlew publishToMavenLocal
```

## Dependencies to Use

| Dependency | Version | Purpose |
|------------|---------|---------|
| JUnit Jupiter | 5.10+ | Test framework |
| ByteBuddy | 1.14+ | Bytecode manipulation |
| AssertJ | 3.24+ | Fluent assertions |
| Mockito | 5.0+ | Mocking (for our own tests) |

## What NOT to Do

1. **Do not add escape hatch markers** - No `@AllowNetwork`, `@AllowFilesystem`, etc.
2. **Do not make time limits configurable** - They are fixed by design (defines test size meaning)
3. **Do not support JUnit 4** - JUnit 5 only
4. **Do not support TestNG** - Out of scope for initial release
5. **Do not add co-authored lines to commits** - Per project standards
6. **Do not sacrifice DX for "purity"** - If it confuses users, find a better way
7. **Do not ship features without documentation** - Undocumented = doesn't exist

## Resources

- **Python reference**: `/Users/mikelane/dev/pytest-test-categories`
- **JUnit 5 docs**: https://junit.org/junit5/docs/current/user-guide/
- **ByteBuddy docs**: https://bytebuddy.net/#/tutorial
- **Google testing blog**: https://testing.googleblog.com/
- **Software Engineering at Google**: https://abseil.io/resources/swe-book
