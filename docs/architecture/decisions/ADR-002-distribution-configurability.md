# ADR-002: Distribution Target Configurability

## Status

Accepted

## Date

2024-12-14

## Context

The test pyramid distribution targets define the ideal ratio of test sizes:

- Small tests: 80%
- Medium tests: 15%
- Large + XLarge tests: 5%

These defaults come from Google's testing best practices. The question is whether teams should be able to customize these targets.

The Python reference implementation (`pytest-test-categories`) allows configuration of:
- Distribution targets per size
- Tolerance window (variance allowed around targets)

## Options Considered

### Option A: Fixed, Non-Configurable Targets

**Pros:**
- Consistent meaning across all projects
- No "configuration drift" where teams weaken constraints
- Simpler implementation

**Cons:**
- Difficult for teams with legacy test suites to adopt
- Too rigid for projects with legitimate reasons for different ratios

### Option B: Fully Configurable Targets

**Pros:**
- Maximum flexibility for adoption
- Teams can start with current ratios and gradually improve
- Matches Python reference implementation

**Cons:**
- Risk of teams setting targets so loose they're meaningless
- More complex configuration

### Option C: Configurable Targets with Tolerance Window (Chosen)

**Pros:**
- Flexibility for gradual adoption
- Tolerance window allows temporary deviation while maintaining pressure
- Feature parity with Python reference
- Teams can customize the "acceptable range" around targets

**Cons:**
- Slightly more complex than fixed targets

## Decision

**Option C: Allow configuration of both distribution targets and tolerance window.**

### Default Configuration

```xml
<configuration>
    <!-- Target percentages (must sum to 100%) -->
    <smallTarget>80</smallTarget>
    <mediumTarget>15</mediumTarget>
    <largeTarget>5</largeTarget>  <!-- Includes XLarge -->

    <!-- Tolerance: tests pass if within target ± tolerance -->
    <tolerance>5</tolerance>
</configuration>
```

With defaults, small tests must be between 75-85% to pass in STRICT mode.

### Validation Rules

1. Targets must sum to 100%
2. Each target must be 0-100
3. Tolerance must be 0-50 (>50 makes validation meaningless)
4. Small target must be >= medium target (enforce pyramid shape)
5. Medium target must be >= large target

## Rationale

1. **Gradual adoption**: Teams with 50/30/20 distributions can start with those targets and improve over time
2. **Tolerance window**: Allows natural variation without failing builds for minor deviations
3. **Feature parity**: Matches Python reference implementation behavior
4. **Flexibility with guardrails**: Validation rules prevent nonsensical configurations

## Consequences

### Positive

- Easier adoption for teams with existing test suites
- Teams can define their own improvement journey
- Clear path from current state to ideal pyramid

### Negative

- Teams could set weak targets and never improve
- More configuration options to document

### Mitigations

- Documentation will strongly recommend defaults
- `WARN` mode will always compare against ideal (80/15/5) regardless of configured targets
- Consider a "strict defaults" mode that ignores custom targets
