# ADR-004: Developer Experience Principles

## Status

Accepted

## Date

2024-12-14

## Context

junit-test-categories will be adopted by development teams who are already busy shipping features. The library must earn its place in their toolchain by being:

1. Easy to adopt
2. Easy to understand
3. Easy to debug when things go wrong

Poor DX leads to abandonment. Confusing behavior leads to distrust. We must be intentional about developer experience from day one.

## Decision

**Developer Experience (DX) is a first-class design concern, equal to correctness and performance.**

We adopt these principles:

### Principle 1: Explicit Over Implicit (Pragmatically)

**The user should always know what to expect.**

- Annotations clearly state what they do: `@SmallTest` not `@Fast`
- Configuration options have obvious names and behavior
- Side effects are documented and predictable
- No "spooky action at a distance"

**Pragmatic exceptions allowed when:**
- The implicit behavior is universally expected (e.g., JUnit auto-discovery)
- Making it explicit would create significant friction
- The behavior is thoroughly documented

### Principle 2: Fail Fast, Fail Clearly

**When something goes wrong, tell the user immediately and tell them how to fix it.**

Bad:
```
java.lang.AssertionError
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at ... (50 more lines of stack trace)
```

Good:
```
[TC001] Network Access Violation
======================================================================
Test: com.example.UserServiceTest.fetchesUser
Category: SMALL

What happened:
  Attempted network connection to api.example.com:443

Why it matters:
  Small tests must be hermetic - no network access allowed.

To fix this (choose one):
  * Mock the HTTP client using Mockito
  * Change to @MediumTest if network access is required

See: https://junit-test-categories.dev/errors/TC001
======================================================================
```

Every error message must include:
1. **What happened** - The specific violation
2. **Why it matters** - Educational context
3. **How to fix it** - Actionable remediation steps
4. **Where to learn more** - Link to documentation

### Principle 3: Progressive Disclosure

**Simple things should be simple. Complex things should be possible.**

Layer 1 - Zero config (just works):
```java
@SmallTest
class MyTest { }
```

Layer 2 - Simple configuration:
```xml
<hermeticityMode>STRICT</hermeticityMode>
```

Layer 3 - Advanced configuration (when needed):
```xml
<smallTarget>70</smallTarget>
<tolerance>10</tolerance>
```

Users should never need Layer 3 to get value. Most users should never leave Layer 1.

### Principle 4: No Surprises

**Behavior should match expectations formed by the API.**

- `@SmallTest` always means the same constraints everywhere
- Configuration in one place doesn't silently affect behavior elsewhere
- Upgrading versions doesn't change behavior without clear migration notes
- Default values are safe and sensible

### Principle 5: Debuggability

**When users need to understand what's happening, give them the tools.**

- Verbose/debug mode that explains every decision
- Clear logging at appropriate levels
- Test report shows why each test was categorized as it was
- Stack traces point to user code, not library internals

### Principle 6: Graceful Degradation

**Partial adoption should work. Gradual migration should be supported.**

- Tests without annotations still run (with configurable warnings)
- `WARN` mode lets teams see violations without breaking builds
- Teams can enable hermeticity before distribution validation
- Existing test suites don't explode on first integration

### Principle 7: Minimal Footprint

**Don't pollute the user's environment.**

- Annotations module has ZERO dependencies
- No global state that could conflict with other tools
- Clean integration with existing test infrastructure
- Easy to remove if teams decide it's not for them

## Consequences

### Positive

- Lower adoption friction
- Higher retention after initial adoption
- Fewer support requests about confusing behavior
- Better word-of-mouth recommendations

### Negative

- More effort required for error message design
- Documentation burden is higher
- Some features may be rejected for DX reasons even if technically sound
- May need to say "no" to feature requests that complicate the API

### Implementation Guidelines

1. **Every error message gets reviewed for DX** - Is it actionable? Is it educational?
2. **Every new config option needs justification** - Does it earn its complexity?
3. **Every API change considers migration** - How do existing users upgrade?
4. **Documentation is not optional** - If it's not documented, it doesn't ship

## Examples

### Good DX: Clear Feedback on Missing Annotation

```
[TC-WARN] Missing Test Size Annotation
======================================================================
Test: com.example.CalculatorTest.addsNumbers

This test has no size annotation (@SmallTest, @MediumTest, etc.).

What this means:
  - No hermeticity constraints are enforced
  - No time limit is applied
  - This test won't count toward distribution targets

To fix this:
  * Add @SmallTest if this is a fast, hermetic unit test
  * Add @MediumTest if this test needs localhost or filesystem access
  * Add @LargeTest if this test needs external services

See: https://junit-test-categories.dev/guide/choosing-test-size
======================================================================
```

### Good DX: Verbose Mode Output

```
[test-categories] Starting test: UserValidatorTest.validatesEmail
[test-categories] Detected annotation: @SmallTest
[test-categories] Constraints applied:
[test-categories]   - Network: BLOCKED
[test-categories]   - Filesystem: BLOCKED
[test-categories]   - Database: BLOCKED
[test-categories]   - Subprocess: BLOCKED
[test-categories]   - Sleep: BLOCKED
[test-categories]   - Time limit: 1000ms
[test-categories] Test completed in 12ms [PASS]
```

### Good DX: Distribution Report

```
╔══════════════════════════════════════════════════════════════════╗
║                    Test Distribution Report                       ║
╠══════════════════════════════════════════════════════════════════╣
║ Category  │ Count │ Actual │ Target │ Status                     ║
╠═══════════╪═══════╪════════╪════════╪════════════════════════════╣
║ Small     │   156 │  78.0% │  80.0% │ ✓ Within tolerance (±5%)  ║
║ Medium    │    32 │  16.0% │  15.0% │ ✓ Within tolerance (±5%)  ║
║ Large     │    10 │   5.0% │   5.0% │ ✓ On target               ║
║ XLarge    │     2 │   1.0% │   -    │ ✓ (counted with Large)    ║
╠═══════════╪═══════╪════════╪════════╪════════════════════════════╣
║ Total     │   200 │ 100.0% │        │ ✓ Distribution healthy    ║
╚══════════════════════════════════════════════════════════════════╝
```

## Related Decisions

- ADR-003: Separate Enforcement Modes (supports gradual adoption)
- ADR-002: Distribution Configurability (supports progressive disclosure)
