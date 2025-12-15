# Roadmap

This document outlines the development phases for junit-test-categories.

## Project Vision

Create a JUnit 5 extension that brings Google's hermetic testing practices to Java, with feature parity to [pytest-test-categories](https://github.com/mikelane/pytest-test-categories).

---

## Phase 0: Foundation (Current)

**Status:** In Progress

### Goals
- [x] Create PRD with full requirements
- [x] Create CLAUDE.md for agent guidance
- [x] Create ROADMAP.md
- [ ] Create README.md
- [ ] Create CONTRIBUTING.md
- [ ] Create design-philosophy.md
- [ ] Set up GitHub repository
- [ ] Configure GitHub Issues and Projects
- [ ] Set up CI/CD pipeline (GitHub Actions)

### Deliverables
- Project documentation complete
- Repository structure established
- CI pipeline running

---

## Phase 1: MVP - Annotations and Timing

**Target:** v0.1.0

### Goals
- [ ] Create `annotations` module with `@SmallTest`, `@MediumTest`, `@LargeTest`, `@XLargeTest`
- [ ] Create `core` module with `TestSize` enum and timing logic
- [ ] Create `junit5` module with basic JUnit 5 Extension
- [ ] Implement timing enforcement (tests fail if they exceed time limits)
- [ ] Create Maven multi-module project structure
- [ ] Publish to Maven Central (or at least local/GitHub Packages)

### Acceptance Criteria
- Tests can be marked with size annotations
- Tests exceeding time limits fail with clear error messages
- Basic Maven plugin wires the extension automatically

### Out of Scope
- Hermeticity enforcement (network, filesystem, etc.)
- Distribution validation
- Reporting

---

## Phase 2: Network Isolation

**Target:** v0.2.0

### Goals
- [ ] Create `agent` module with ByteBuddy-based instrumentation
- [ ] Implement network interception (`Socket.connect()`, `URL.openConnection()`, etc.)
- [ ] Block all network in `@SmallTest`
- [ ] Allow localhost-only in `@MediumTest`
- [ ] Allow all network in `@LargeTest`/`@XLargeTest`
- [ ] Create actionable error messages with remediation guidance
- [ ] Document agent setup for Maven

### Acceptance Criteria
- Small tests that attempt network connections fail immediately
- Error messages include:
  - What happened (attempted connection to X)
  - How to fix (mock, DI, or change category)
  - Link to documentation
- Medium tests can connect to localhost but not external hosts

### Technical Notes
- Agent must be added to JVM args: `-javaagent:junit-test-categories-agent.jar`
- Maven plugin should handle this automatically
- Must be compatible with parallel test execution (thread-local context)

---

## Phase 3: Full Hermeticity

**Target:** v0.3.0

### Goals
- [ ] Implement filesystem interception (`Files.*`, `File.*`, streams)
- [ ] Implement database interception (`DriverManager.getConnection()`)
- [ ] Implement subprocess interception (`ProcessBuilder.start()`)
- [ ] Implement sleep interception (`Thread.sleep()`, `TimeUnit.sleep()`)
- [ ] Create comprehensive error messages for each violation type

### Acceptance Criteria
- Small tests cannot:
  - Read/write files
  - Connect to databases (including in-memory)
  - Spawn subprocesses
  - Call sleep
- Each violation type has its own error code and remediation guidance

### Error Codes
| Code | Violation |
|------|-----------|
| TC001 | Network access |
| TC002 | Filesystem access |
| TC003 | Database access |
| TC004 | Subprocess creation |
| TC005 | Sleep call |
| TC006 | Timing exceeded |

---

## Phase 4: Enforcement Modes

**Target:** v0.4.0

### Goals
- [ ] Implement `OFF` mode (plugin disabled)
- [ ] Implement `WARN` mode (log violations, don't fail)
- [ ] Implement `STRICT` mode (fail on violations)
- [ ] Add Maven plugin configuration for enforcement mode
- [ ] Add system property override: `-Dtest.categories.enforcement=WARN`

### Acceptance Criteria
- Default mode is `OFF` for backward compatibility
- Warn mode logs all violations but tests pass
- Strict mode fails tests on any violation
- Mode can be set via:
  - Maven plugin configuration
  - System property (overrides plugin config)

---

## Phase 5: Distribution Validation

**Target:** v0.5.0

### Goals
- [ ] Count tests by category during test execution
- [ ] Calculate distribution percentages
- [ ] Validate against targets (80/15/5)
- [ ] Add distribution enforcement mode (separate from hermeticity)
- [ ] Create distribution summary in test output

### Acceptance Criteria
- After test run, distribution is calculated and displayed
- If distribution enforcement is STRICT:
  - Fail if small < 75%
  - Fail if medium > 20%
  - Fail if large/xlarge > 8%
- Distribution report shows:
  - Count per category
  - Percentage per category
  - Target vs actual
  - Pass/fail status

---

## Phase 6: Reporting

**Target:** v0.6.0

### Goals
- [ ] Implement basic report (summary by category)
- [ ] Implement detailed report (per-test listings)
- [ ] Implement JSON report (machine-readable)
- [ ] Add Maven goal for report generation
- [ ] Add report file output configuration

### Acceptance Criteria
- `mvn test-categories:report` generates report
- JSON report includes:
  - Timestamp
  - Distribution summary
  - Violation counts
  - Per-test details (name, category, duration, violations)

---

## Phase 7: Gradle Support

**Target:** v0.7.0

### Goals
- [ ] Create Gradle plugin with Kotlin DSL
- [ ] Mirror all Maven plugin functionality
- [ ] Add Gradle tasks for reports
- [ ] Test with Gradle 7.x and 8.x

### Acceptance Criteria
- Feature parity with Maven plugin
- Works with Gradle Kotlin DSL and Groovy DSL
- Documented in README

---

## Phase 8: Polish and Examples

**Target:** v1.0.0

### Goals
- [ ] Create Spring Boot example project
- [ ] Create plain Maven example project
- [ ] Create Gradle example project
- [ ] Performance optimization (minimize agent overhead)
- [ ] Comprehensive documentation site
- [ ] JaCoCo compatibility testing and documentation
- [ ] Mockito compatibility testing
- [ ] Community feedback incorporation

### Acceptance Criteria
- All examples work out of the box
- Documentation covers all features
- Agent overhead < 5%
- No known compatibility issues with major tools

---

## Future Considerations (Post 1.0)

### Potential Features
- TestNG support
- Kotlin-specific DSL
- IDE plugins (IntelliJ, Eclipse)
- Test flakiness detection
- Historical trend tracking
- Integration with test management tools

### Not Planned
- Configurable time limits (by design)
- Configurable distribution targets (by design)
- Per-test escape hatches (by design)
- JUnit 4 support

---

## Release Versioning

This project follows [Semantic Versioning](https://semver.org/):

- **0.x.y** - Pre-release, API may change
- **1.0.0** - First stable release
- **1.x.y** - Backward-compatible additions
- **2.0.0** - Breaking changes (if ever needed)

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for how to get involved in development.

Each roadmap item should have a corresponding GitHub Issue before work begins.
