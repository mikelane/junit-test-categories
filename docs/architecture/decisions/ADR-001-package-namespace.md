# ADR-001: Package Namespace

## Status

Accepted

## Date

2024-12-14

## Context

We need to choose a Java package namespace for all modules in the junit-test-categories library. This decision affects:

- Maven coordinates (groupId)
- All import statements in user code
- Developer experience and discoverability
- Future publishing to Maven Central

The namespace should be:
- Professional and memorable
- Clear about the library's purpose
- Distinct from official JUnit packages

## Options Considered

### Option A: `io.testcategories`

**Pros:**
- Short and memorable
- Professional appearance
- Matches potential documentation URL (testcategories.io)

**Cons:**
- Generic domain that may not be available
- Doesn't explicitly mention JUnit

### Option B: `dev.hermetic`

**Pros:**
- Describes the core concept (hermetic testing)
- Available namespace

**Cons:**
- Less discoverable for users searching for "test categories"
- Doesn't mention JUnit

### Option C: `io.junit.categories`

**Pros:**
- Clear association with JUnit
- Intuitive for Java developers
- Professional namespace

**Cons:**
- Could potentially be confused with official JUnit packages (`org.junit.*`)

### Option D: `dev.testpyramid`

**Pros:**
- Describes the distribution validation goal

**Cons:**
- Doesn't capture the hermeticity aspect
- Less intuitive

## Decision

**Option C: `io.junit.categories`**

Maven coordinates:
```xml
<groupId>io.junit.categories</groupId>
<artifactId>junit-test-categories-*</artifactId>
```

Example import:
```java
import io.junit.categories.SmallTest;
```

## Rationale

1. **Clear purpose**: The namespace immediately communicates this is for JUnit test categorization
2. **Distinct from official**: Uses `io.junit` not `org.junit`, clearly separate from official packages
3. **Professional**: The `io.*` namespace is established for professional open source projects
4. **Intuitive**: Developers familiar with JUnit will find it easy to remember

## Consequences

### Positive

- Clear, intuitive imports for users
- Easy to discover and remember
- Professional appearance for Maven Central publishing

### Negative

- Need to be clear in documentation that this is not an official JUnit project
- The `io.junit` namespace may seem presumptuous, but it's a common pattern

### Neutral

- Documentation URL can be `junit-test-categories.dev` or similar
