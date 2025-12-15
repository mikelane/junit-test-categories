# Design Philosophy

This document explains the core design philosophy behind junit-test-categories and the reasoning for its strict enforcement approach.

This philosophy is shared with [pytest-test-categories](https://github.com/mikelane/pytest-test-categories), the Python reference implementation.

## The "No Escape Hatches" Philosophy

junit-test-categories is built on a fundamental principle: **small tests must be truly hermetic**. This means:

- No network access (not even localhost for small tests)
- No filesystem access
- No subprocess spawning
- No database connections (including in-memory databases)
- No sleep calls
- Must complete within 1 second

Unlike many testing tools that provide optional enforcement or easy overrides, junit-test-categories is intentionally strict. This design choice is rooted in Google's "Software Engineering at Google" testing philosophy.

### Why Strictness Matters

**1. Flaky tests are expensive**

Flaky tests erode trust in the test suite. When developers can't rely on test results, they:
- Ignore legitimate failures
- Re-run tests multiple times "just in case"
- Spend hours debugging phantom failures
- Eventually stop writing tests altogether

By enforcing hermeticity at the framework level, we eliminate entire categories of flakiness.

**2. Escape hatches become the norm**

When a tool provides easy ways to bypass restrictions, teams inevitably use them:

```java
// "Just this once" becomes standard practice
@SmallTest
@AllowNetwork  // Hypothetical escape hatch - DOES NOT EXIST
void testUserService() {
    Response response = httpClient.get("http://api.example.com/users");  // Still flaky!
    // ...
}
```

junit-test-categories deliberately does not provide such annotations. If your test needs network access, it should be marked as `@MediumTest` or larger.

**3. Categories have meaning**

The test size categories are not arbitrary labels - they define contracts:

| Category | Meaning | Resources |
|----------|---------|-----------|
| Small | Unit test, single process, in-memory | None (hermetic) |
| Medium | Integration test, single machine | Localhost network, filesystem |
| Large | System test, multi-machine possible | Full network, external services |
| XLarge | Performance/stress test | Unlimited |

If a "small" test can access the network, it's not really a small test. The label loses meaning.

## Trade-offs and Design Decisions

### Decision: Block all network for small tests

**What we chose**: Small tests cannot make any network connections, not even to localhost.

**Alternative considered**: Allow localhost connections for small tests.

**Rationale**: Even localhost connections can be flaky:
- Port conflicts in parallel test execution
- Service startup timing issues
- Resource exhaustion under load

If you need localhost, use `@MediumTest`. Medium tests allow localhost-only connections.

### Decision: Block in-memory databases

**What we chose**: Even in-memory databases (H2, HSQLDB) are blocked in small tests.

**Alternative considered**: Allow in-memory databases since they don't involve I/O.

**Rationale**:
1. Consistency - database usage is database usage, regardless of storage
2. Design smell - if you need a database, your test might be too integrated
3. Encourages better patterns - use repository fakes, not in-memory databases

```java
// Instead of this:
@SmallTest
void testUserRepository() {
    Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");  // Blocked!
    UserRepository repo = new UserRepository(conn);
    // ...
}

// Do this:
@SmallTest
void testUserRepository() {
    UserRepository repo = new FakeUserRepository();  // In-memory, no database
    // ...
}
```

### Decision: Enforcement modes exist, but for migration only

**What we chose**: Three enforcement modes - `STRICT`, `WARN`, and `OFF`.

**Why have modes if we're strict?**

The modes exist to support **gradual adoption**, not permanent bypass:

1. **OFF**: For initial exploration - see what would fail
2. **WARN**: For migration - fix violations incrementally
3. **STRICT**: The destination - where you should end up

```xml
<!-- pom.xml - a migration journey -->

<!-- Week 1: Discovery -->
<enforcement>off</enforcement>

<!-- Week 2-4: Migration -->
<enforcement>warn</enforcement>

<!-- Week 5+: Enforced -->
<enforcement>strict</enforcement>
```

### Decision: No per-test exemptions

**What we chose**: No `@AllowNetwork` or similar annotations.

**Alternative considered**: Per-test overrides (like some network-blocking libraries provide).

**Rationale**: Per-test overrides defeat the purpose:
- They proliferate (every test becomes "special")
- They're never removed (technical debt)
- They make the category meaningless

Instead, use the right category:

```java
// Wrong: Forcing a square peg into a round hole
@SmallTest
@AllowNetwork  // DON'T DO THIS - doesn't exist anyway
void testExternalApi() {
    // ...
}

// Right: Use the appropriate category
@MediumTest  // Honest about what the test does
void testExternalApi() {
    // ...
}
```

### Decision: Fixed time limits

**What we chose**: Time limits are fixed and not configurable.

**Alternative considered**: Allow teams to customize time limits.

**Rationale**:
1. Consistent meaning - "small test" means the same thing everywhere
2. No drift - teams can't gradually loosen constraints
3. Google's standards - these limits are battle-tested at scale

| Category | Time Limit | Non-negotiable |
|----------|-----------|----------------|
| Small | 1 second | Yes |
| Medium | 5 minutes | Yes |
| Large | 15 minutes | Yes |
| XLarge | 15 minutes | Yes |

### Decision: Fixed distribution targets

**What we chose**: Distribution targets (80/15/5) are fixed.

**Alternative considered**: Allow teams to customize targets.

**Rationale**:
1. The 80/15/5 ratio is proven at Google scale
2. Custom targets become "whatever we have now"
3. Encourages improvement rather than accepting status quo

## Comparison with Other Approaches

### ArchUnit

ArchUnit provides static analysis for architectural rules:

```java
@ArchTest
static final ArchRule noNetworkInUnitTests = noClasses()
    .that().resideInAPackage("..unit..")
    .should().accessClassesThat().resideInAPackage("java.net..");
```

**Difference**: ArchUnit catches violations at compile/analysis time based on imports. junit-test-categories catches violations at runtime when actual connections are attempted. These are **complementary** approaches:
- Use **ArchUnit** to catch obvious violations early
- Use **junit-test-categories** to catch runtime violations that static analysis misses

### Testcontainers

Testcontainers spins up Docker containers for integration tests:

```java
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
```

**Difference**: Testcontainers is for medium/large tests that need real dependencies. junit-test-categories enforces that small tests don't use such dependencies. These are **complementary**:
- Use **Testcontainers** in medium tests
- Use **junit-test-categories** to ensure small tests stay hermetic

### WireMock

WireMock mocks HTTP services:

```java
stubFor(get("/users/123").willReturn(okJson("{\"name\": \"Alice\"}")));
```

**Difference**: WireMock helps you mock external services. junit-test-categories enforces that you do mock them (in small tests). These are **complementary**:
- Use **WireMock** to mock HTTP dependencies
- Use **junit-test-categories** to ensure small tests use mocks

## The Distribution Target Philosophy

junit-test-categories enforces not just individual test behavior but also the overall test distribution:

- **80% small tests** (hermetic, fast, reliable)
- **15% medium tests** (integration, localhost allowed)
- **5% large/xlarge tests** (system tests, full access)

### Why enforce distribution?

**The Testing Pyramid**: Teams naturally drift toward larger tests because they're "easier" to write:
- No need for mocks or fakes
- Can test "the real thing"
- Less upfront design

But larger tests are slower and less reliable. Enforcing distribution keeps teams honest.

**Economics**: If 80% of tests run in under 1 second each, your test suite stays fast even as it grows. If most tests are large, CI becomes a bottleneck.

### Tolerance bands

The targets have tolerance bands to be practical:
- Small: 80% (+/-5%) = 75-85%
- Medium: 15% (+/-5%) = 10-20%
- Large/XLarge: 5% (+/-3%) = 2-8%

These allow normal variation while preventing drift.

## Actionable Error Messages

When junit-test-categories blocks something, it tells you exactly how to fix it:

```
[TC001] Network Access Violation
Test:     UserServiceTest.fetchesUserProfile (UserServiceTest.java:42)
Category: SMALL

What happened:
  Attempted network connection to api.example.com:443

To fix (choose one):
  1. Mock the HTTP client using Mockito or WireMock
  2. Use dependency injection to provide a fake HTTP client
  3. Change test category to @MediumTest (if network is required)

Documentation: https://junit-test-categories.dev/errors/TC001
```

This philosophy of **helpful errors** is intentional:
1. Don't just say "no" - explain why
2. Provide multiple solutions - one might fit better
3. Link to documentation - for deeper learning

## Summary

junit-test-categories is opinionated by design. It embodies the belief that:

1. **Test reliability is non-negotiable** - flaky tests destroy developer trust
2. **Categories should mean something** - a small test is hermetic or it's not small
3. **Strictness enables speed** - hermetic tests can run in parallel, always
4. **Gradual adoption, then enforcement** - modes are for migration, not bypass
5. **Help, don't just block** - every error should include remediation guidance

This philosophy produces test suites that are fast, reliable, and meaningful.

## References

- [Software Engineering at Google - Testing Chapter](https://abseil.io/resources/swe-book/html/ch11.html)
- [pytest-test-categories Design Philosophy](https://pytest-test-categories.readthedocs.io/en/latest/architecture/design-philosophy.html)
- [Google Testing Blog](https://testing.googleblog.com/)
