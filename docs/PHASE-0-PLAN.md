# Phase 0: Foundation - Implementation Plan

## Overview

Phase 0 establishes the project foundation: repository setup, CI/CD, project structure, and documentation. No production code is written in this phase.

**Goal**: A fully configured, empty multi-module Maven project with CI/CD, ready for Phase 1 development.

---

## Decision: Package Namespace

**Decided**: `io.junit.categories`

| Attribute | Value |
|-----------|-------|
| Maven groupId | `io.junit.categories` |
| Base package | `io.junit.categories` |
| Example import | `import io.junit.categories.SmallTest;` |

This namespace clearly associates the library with JUnit while remaining distinct from official JUnit packages.

---

## Phase 0 Tasks

### 0.1 Repository Setup

| Task | Description | Status |
|------|-------------|--------|
| 0.1.1 | Initialize Git repository (if not done) | Pending |
| 0.1.2 | Create `.gitignore` for Java/Maven/Gradle/IDE files | Pending |
| 0.1.3 | Push to GitHub (`mikelane/junit-test-categories`) | Pending |
| 0.1.4 | Configure branch protection on `main` | Pending |
| 0.1.5 | Create issue labels (see below) | Pending |
| 0.1.6 | Create GitHub Project board | Pending |

#### Issue Labels

```
phase:0-foundation     - #0E8A16 (green)
phase:1-mvp            - #1D76DB (blue)
phase:2-network        - #5319E7 (purple)
phase:3-hermeticity    - #FBCA04 (yellow)
phase:4-modes          - #D93F0B (orange)
phase:5-distribution   - #B60205 (red)
phase:6-reporting      - #006B75 (teal)
phase:7-gradle         - #C2E0C6 (light green)
phase:8-polish         - #BFD4F2 (light blue)

type:bug               - #D73A4A (red)
type:feature           - #A2EEEF (cyan)
type:docs              - #0075CA (blue)
type:chore             - #CFD3D7 (gray)
type:test              - #7057FF (purple)

priority:critical      - #B60205 (dark red)
priority:high          - #D93F0B (orange)
priority:medium        - #FBCA04 (yellow)
priority:low           - #0E8A16 (green)
```

---

### 0.2 CI/CD Pipeline

| Task | Description | Status |
|------|-------------|--------|
| 0.2.1 | Create `.github/workflows/ci.yml` | Pending |
| 0.2.2 | Configure matrix build (Java 17, 21) | Pending |
| 0.2.3 | Add code style check (Spotless) | Pending |
| 0.2.4 | Add test coverage (JaCoCo) | Pending |
| 0.2.5 | Add dependency vulnerability scan (Dependabot) | Pending |
| 0.2.6 | Create `.github/workflows/release.yml` (publish to GitHub Packages) | Pending |

#### CI Workflow Structure

```yaml
# .github/workflows/ci.yml
name: CI

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build:
    strategy:
      matrix:
        java: [17, 21]
        os: [ubuntu-latest]
    runs-on: ${{ matrix.os }}
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: ${{ matrix.java }}
          cache: 'maven'
      - name: Build
        run: mvn clean verify -B
      - name: Upload coverage
        uses: codecov/codecov-action@v4
        if: matrix.java == '17'
```

---

### 0.3 Maven Project Structure

| Task | Description | Status |
|------|-------------|--------|
| 0.3.1 | Create parent POM with common configuration | Pending |
| 0.3.2 | Create `annotations` module (empty) | Pending |
| 0.3.3 | Create `core` module (empty) | Pending |
| 0.3.4 | Create `agent` module (empty) | Pending |
| 0.3.5 | Create `junit5` module (empty) | Pending |
| 0.3.6 | Create `maven-plugin` module (empty) | Pending |
| 0.3.7 | Create `gradle-plugin` module (empty, Gradle subproject) | Pending |
| 0.3.8 | Configure Spotless for Google Java Style | Pending |
| 0.3.9 | Configure JaCoCo for coverage | Pending |
| 0.3.10 | Configure versions for all dependencies | Pending |

#### Project Structure

```
junit-test-categories/
├── pom.xml                          # Parent POM
├── annotations/
│   ├── pom.xml
│   └── src/main/java/...            # Empty, placeholder package-info.java
├── core/
│   ├── pom.xml
│   └── src/main/java/...
├── agent/
│   ├── pom.xml
│   └── src/main/java/...
├── junit5/
│   ├── pom.xml
│   └── src/main/java/...
├── maven-plugin/
│   ├── pom.xml
│   └── src/main/java/...
├── gradle-plugin/                   # Gradle subproject (not Maven module)
│   ├── build.gradle.kts
│   └── src/main/kotlin/...
├── docs/
│   ├── architecture/
│   │   └── decisions/               # ADRs
│   └── errors/                      # Error code documentation
└── examples/                        # Will be added in Phase 8
```

#### Parent POM Key Configuration

```xml
<properties>
    <!-- Java -->
    <java.version>17</java.version>
    <maven.compiler.source>${java.version}</maven.compiler.source>
    <maven.compiler.target>${java.version}</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

    <!-- Dependencies -->
    <junit.version>5.10.2</junit.version>
    <bytebuddy.version>1.14.12</bytebuddy.version>
    <assertj.version>3.25.3</assertj.version>
    <mockito.version>5.10.0</mockito.version>

    <!-- Plugins -->
    <spotless.version>2.43.0</spotless.version>
    <jacoco.version>0.8.11</jacoco.version>
</properties>
```

---

### 0.4 Documentation

| Task | Description | Status |
|------|-------------|--------|
| 0.4.1 | Create `SECURITY.md` | Pending |
| 0.4.2 | Update `README.md` with project status badge | Pending |
| 0.4.3 | Create `docs/architecture/decisions/ADR-001-package-namespace.md` | Pending |
| 0.4.4 | Create `docs/architecture/decisions/ADR-002-distribution-configurability.md` | Pending |
| 0.4.5 | Create `docs/architecture/decisions/ADR-003-enforcement-modes.md` | Pending |
| 0.4.6 | Create `docs/errors/README.md` (error code index) | Pending |

#### ADR Template

```markdown
# ADR-NNN: Title

## Status
Accepted | Proposed | Deprecated | Superseded by ADR-XXX

## Context
What is the issue that we're seeing that is motivating this decision?

## Decision
What is the change that we're proposing and/or doing?

## Consequences
What becomes easier or more difficult to do because of this change?
```

---

### 0.5 Security

| Task | Description | Status |
|------|-------------|--------|
| 0.5.1 | Create `SECURITY.md` with disclosure policy | Pending |
| 0.5.2 | Enable GitHub security advisories | Pending |
| 0.5.3 | Configure Dependabot for dependency updates | Pending |

#### SECURITY.md Content

```markdown
# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.x.x   | :white_check_mark: |
| < 1.0   | :x:                |

## Reporting a Vulnerability

Please report security vulnerabilities by emailing [security email TBD].

Do NOT create public GitHub issues for security vulnerabilities.

We will acknowledge receipt within 48 hours and provide a detailed response
within 7 days indicating next steps.
```

---

## Architectural Decision Records (ADRs)

### ADR-001: Package Namespace

**Status**: Accepted

**Context**: We need a Java package namespace for all modules. The namespace affects Maven coordinates (groupId), all import statements, and developer experience.

**Options Considered**:
- `io.testcategories` - Professional, short, memorable
- `dev.hermetic` - Describes the core concept
- `io.junit.categories` - Clear JUnit association
- `dev.testpyramid` - Describes the distribution goal

**Decision**: `io.junit.categories`

**Rationale**: This namespace clearly communicates the library's purpose (JUnit test categories) while remaining distinct from official JUnit packages (`org.junit.*`). It's intuitive for Java developers familiar with JUnit.

---

### ADR-002: Distribution Target Configurability

**Status**: Accepted

**Context**: The Python reference implementation allows teams to configure distribution targets (defaults 80/15/5). The original Java PRD stated these should be fixed.

**Decision**: Allow configuration of both:
1. Distribution targets (default: 80% small, 15% medium, 5% large+xlarge)
2. Tolerance window (default: 5% variance allowed)

**Rationale**:
- Enables gradual adoption for teams with existing test suites
- Matches Python feature parity
- Teams can still use strict defaults if desired

**Configuration Example** (Maven):
```xml
<configuration>
    <smallTarget>80</smallTarget>
    <mediumTarget>15</mediumTarget>
    <largeTarget>5</largeTarget>
    <tolerance>5</tolerance>
</configuration>
```

---

### ADR-003: Separate Enforcement Modes

**Status**: Accepted

**Context**: Teams need to adopt hermeticity enforcement and distribution validation at different paces.

**Decision**: Provide two separate enforcement mode settings:

1. **Hermeticity Enforcement** (`hermeticityMode`)
   - `OFF` - No resource blocking
   - `WARN` - Log violations, tests pass
   - `STRICT` - Violations fail tests

2. **Distribution Enforcement** (`distributionMode`)
   - `OFF` - No validation
   - `WARN` - Log warnings, build passes
   - `STRICT` - Violations fail build

**Rationale**:
- Allows teams to be strict on hermeticity but lenient on distribution during migration
- Matches Python implementation
- Provides flexibility without compromising eventual strictness

**Configuration Example** (Maven):
```xml
<configuration>
    <hermeticityMode>STRICT</hermeticityMode>
    <distributionMode>WARN</distributionMode>
</configuration>
```

---

## Dependencies

### Runtime Dependencies

| Dependency | Version | Module | Purpose |
|------------|---------|--------|---------|
| JUnit Jupiter API | 5.10.2 | junit5 | Test framework integration |
| JUnit Platform Commons | 5.10.2 | junit5 | Platform utilities |
| ByteBuddy | 1.14.12 | agent | Bytecode manipulation |
| ByteBuddy Agent | 1.14.12 | agent | Java agent support |

### Test Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| JUnit Jupiter | 5.10.2 | Testing framework |
| AssertJ | 3.25.3 | Fluent assertions |
| Mockito | 5.10.0 | Mocking |
| ArchUnit | 1.2.1 | Architecture testing |

### Build Dependencies

| Plugin | Version | Purpose |
|--------|---------|---------|
| maven-compiler-plugin | 3.12.1 | Java compilation |
| maven-surefire-plugin | 3.2.5 | Test execution |
| maven-jar-plugin | 3.3.0 | JAR packaging |
| maven-source-plugin | 3.3.0 | Source JAR |
| maven-javadoc-plugin | 3.6.3 | Javadoc JAR |
| spotless-maven-plugin | 2.43.0 | Code formatting |
| jacoco-maven-plugin | 0.8.11 | Code coverage |

---

## Acceptance Criteria for Phase 0

Phase 0 is complete when:

- [ ] Repository is on GitHub with branch protection
- [ ] CI pipeline runs on every PR (Java 17 + 21)
- [ ] All modules exist (even if empty)
- [ ] `mvn clean verify` passes
- [ ] Code style is enforced (Spotless)
- [ ] Coverage reporting works (JaCoCo)
- [ ] SECURITY.md exists
- [ ] ADRs 001-003 are documented
- [ ] Issue labels are created
- [ ] Package namespace is decided

---

## Estimated Effort

| Task Group | Estimated Time |
|------------|----------------|
| Repository Setup | 30 minutes |
| CI/CD Pipeline | 1 hour |
| Maven Structure | 2 hours |
| Documentation | 1 hour |
| **Total** | ~4-5 hours |

---

## Next Steps After Phase 0

Once Phase 0 is complete, Phase 1 (MVP) can begin:

1. Implement `@SmallTest`, `@MediumTest`, `@LargeTest`, `@XLargeTest` annotations
2. Implement `TestSize` enum and timing logic
3. Implement JUnit 5 Extension with timing enforcement only
4. Create basic Maven plugin configuration
5. Write comprehensive tests

Phase 1 does NOT include ByteBuddy/hermeticity - just annotations and timing.
