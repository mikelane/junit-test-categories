# ADR-003: Separate Enforcement Modes

## Status

Accepted

## Date

2024-12-14

## Context

The library enforces two categories of constraints:

1. **Hermeticity**: Resource isolation (network, filesystem, database, subprocess, sleep)
2. **Distribution**: Test pyramid ratios (80/15/5)

Teams may need to adopt these constraints at different paces:

- A team might want strict hermeticity immediately (reproducible tests)
- But lenient distribution validation while they refactor their test suite

The Python reference implementation provides separate enforcement modes for each.

## Options Considered

### Option A: Single Combined Mode

```xml
<configuration>
    <enforcement>WARN</enforcement>  <!-- Applies to everything -->
</configuration>
```

**Pros:**
- Simple configuration
- Clear mental model

**Cons:**
- Forces all-or-nothing adoption
- Teams can't be strict on hermeticity while lenient on distribution

### Option B: Separate Modes (Chosen)

```xml
<configuration>
    <hermeticityMode>STRICT</hermeticityMode>
    <distributionMode>WARN</distributionMode>
</configuration>
```

**Pros:**
- Flexible adoption path
- Teams control their journey to full enforcement
- Matches Python reference implementation

**Cons:**
- More configuration options
- Slightly more complex mental model

## Decision

**Option B: Provide two separate enforcement mode settings.**

### Configuration

```xml
<configuration>
    <!-- Hermeticity enforcement for resource access -->
    <hermeticityMode>STRICT</hermeticityMode>

    <!-- Distribution enforcement for test pyramid -->
    <distributionMode>WARN</distributionMode>
</configuration>
```

### Mode Behaviors

#### Hermeticity Modes

| Mode | Violation Behavior | Test Result |
|------|-------------------|-------------|
| `OFF` | No interception | Pass |
| `WARN` | Log warning | Pass |
| `STRICT` | Throw exception | Fail |

#### Distribution Modes

| Mode | Violation Behavior | Build Result |
|------|-------------------|--------------|
| `OFF` | No validation | Pass |
| `WARN` | Log warning at end of suite | Pass |
| `STRICT` | Fail after test collection | Fail |

### Defaults

```xml
<hermeticityMode>OFF</hermeticityMode>
<distributionMode>OFF</distributionMode>
```

Both default to `OFF` for safe initial adoption. Teams opt-in to enforcement.

## Rationale

1. **Gradual adoption**: Teams can enable one type of enforcement at a time
2. **Common scenario**: Many teams want reproducible tests (hermeticity) immediately but need time to fix their pyramid
3. **Feature parity**: Matches Python reference implementation
4. **Explicit opt-in**: Defaults to OFF means no surprise failures

## Consequences

### Positive

- Teams can adopt hermeticity without fixing distribution first
- Clear separation of concerns
- Flexible migration path from legacy test suites

### Negative

- Two configuration options instead of one
- Need to document both modes clearly
- Risk of teams leaving distribution at OFF permanently

### Mitigations

- Documentation will recommend enabling both modes
- Consider "recommended configuration" examples for different adoption phases:
  - **Phase 1**: `hermeticityMode=WARN, distributionMode=OFF`
  - **Phase 2**: `hermeticityMode=STRICT, distributionMode=WARN`
  - **Phase 3**: `hermeticityMode=STRICT, distributionMode=STRICT`

## Related Decisions

- ADR-002: Distribution Target Configurability
