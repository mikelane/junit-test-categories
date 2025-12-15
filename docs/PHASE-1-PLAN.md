# Phase 1: MVP - Annotations and Timing

## Overview

Phase 1 delivers the **minimum viable product**: test size annotations with timing enforcement. No hermeticity (ByteBuddy), no distribution validation—just the foundational annotations and time limits.

**Target Version:** 0.1.0

**Goal:** Teams can mark tests with `@SmallTest`, `@MediumTest`, `@LargeTest`, `@XLargeTest` and get automatic timeout enforcement.

---

## What Ships in Phase 1

| Feature | Included |
|---------|----------|
| `@SmallTest` annotation | ✅ |
| `@MediumTest` annotation | ✅ |
| `@LargeTest` annotation | ✅ |
| `@XLargeTest` annotation | ✅ |
| Time limit enforcement (1s/5m/15m/15m) | ✅ |
| Clear timeout error messages | ✅ |
| JUnit 5 Extension | ✅ |
| Maven plugin (basic wiring) | ✅ |
| Network blocking | ❌ Phase 2 |
| Filesystem blocking | ❌ Phase 3 |
| Distribution validation | ❌ Phase 5 |

---

## User Experience (DX-First)

### Simple Case: Just Works

```java
import io.junit.categories.SmallTest;
import org.junit.jupiter.api.Test;

@SmallTest
class CalculatorTest {

    @Test
    void addsNumbers() {
        assertThat(Calculator.add(2, 2)).isEqualTo(4);
    }
}
```

That's it. The test now has a 1-second timeout. No configuration needed.

### Timeout Violation (Clear Error Message)

When a small test exceeds 1 second:

```
[TC006] Timing Exceeded
======================================================================
Test: com.example.CalculatorTest.computesFactorial
Category: SMALL
Time Limit: 1.00 seconds

What happened:
  Test execution time: 2.34 seconds

Why it matters:
  Small tests must complete within 1 second to ensure fast feedback.
  Slow tests indicate they may be doing too much work.

To fix this (choose one):
  * Reduce the scope of the test (test smaller units)
  * Use faster test doubles instead of real implementations
  * Change to @MediumTest if the test legitimately needs more time

See: https://junit-test-categories.dev/errors/TC006
======================================================================
```

### Verbose Mode (Debuggability)

With `-Dtest.categories.verbose=true`:

```
[test-categories] Test: CalculatorTest.addsNumbers
[test-categories]   Annotation: @SmallTest
[test-categories]   Time limit: 1000ms
[test-categories]   Completed in: 12ms [PASS]

[test-categories] Test: CalculatorTest.computesFactorial
[test-categories]   Annotation: @SmallTest
[test-categories]   Time limit: 1000ms
[test-categories]   Completed in: 2340ms [FAIL - TC006]
```

---

## Implementation Tasks

### 1. Annotations Module (`annotations`)

**Zero dependencies.** Users can adopt annotations without pulling in the framework.

| Task | Description |
|------|-------------|
| 1.1 | Create `@SmallTest` annotation |
| 1.2 | Create `@MediumTest` annotation |
| 1.3 | Create `@LargeTest` annotation |
| 1.4 | Create `@XLargeTest` annotation |
| 1.5 | Add Javadoc with usage examples |
| 1.6 | Ensure annotations are `@Inherited` for class-level use |

#### Annotation Design

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ExtendWith(TestCategoriesExtension.class)
@Tag("small")
public @interface SmallTest {
}
```

Key decisions:
- `@Inherited` - Class-level annotation applies to all methods
- `@ExtendWith` - Auto-registers the JUnit 5 extension (zero config)
- `@Tag` - Enables filtering with `--include-tag small`

---

### 2. Core Module (`core`)

| Task | Description |
|------|-------------|
| 2.1 | Create `TestSize` enum (SMALL, MEDIUM, LARGE, XLARGE) |
| 2.2 | Define time limits per size (immutable) |
| 2.3 | Create `TimingViolation` exception |
| 2.4 | Create `ErrorMessages` utility with formatted messages |
| 2.5 | Create `TestTimer` interface (for testability) |
| 2.6 | Create `WallClockTimer` implementation |
| 2.7 | Create `TestContext` to hold current test metadata |

#### TestSize Enum

```java
public enum TestSize {
    SMALL(Duration.ofSeconds(1)),
    MEDIUM(Duration.ofMinutes(5)),
    LARGE(Duration.ofMinutes(15)),
    XLARGE(Duration.ofMinutes(15));

    private final Duration timeLimit;

    public Duration getTimeLimit() {
        return timeLimit;
    }

    public static Optional<TestSize> fromAnnotation(AnnotatedElement element) {
        // Detect which annotation is present
    }
}
```

#### Error Message Format

```java
public final class ErrorMessages {

    public static String timingExceeded(
            String testName,
            TestSize size,
            Duration limit,
            Duration actual) {
        return """
            [TC006] Timing Exceeded
            ======================================================================
            Test: %s
            Category: %s
            Time Limit: %.2f seconds

            What happened:
              Test execution time: %.2f seconds

            Why it matters:
              %s tests must complete within %s to ensure fast feedback.
              Slow tests indicate they may be doing too much work.

            To fix this (choose one):
              * Reduce the scope of the test (test smaller units)
              * Use faster test doubles instead of real implementations
              * Change to @%sTest if the test legitimately needs more time

            See: https://junit-test-categories.dev/errors/TC006
            ======================================================================
            """.formatted(
                testName,
                size.name(),
                limit.toMillis() / 1000.0,
                actual.toMillis() / 1000.0,
                size.name().charAt(0) + size.name().substring(1).toLowerCase(),
                formatDuration(limit),
                getNextSize(size).name().charAt(0) + getNextSize(size).name().substring(1).toLowerCase()
            );
    }
}
```

---

### 3. JUnit 5 Module (`junit5`)

| Task | Description |
|------|-------------|
| 3.1 | Create `TestCategoriesExtension` implementing JUnit callbacks |
| 3.2 | Implement `BeforeTestExecutionCallback` to start timer |
| 3.3 | Implement `AfterTestExecutionCallback` to check timing |
| 3.4 | Detect test size from class or method annotation |
| 3.5 | Handle inheritance (method overrides class) |
| 3.6 | Add verbose logging support |
| 3.7 | Handle tests without annotations (configurable warning) |

#### Extension Implementation

```java
public class TestCategoriesExtension implements
        BeforeTestExecutionCallback,
        AfterTestExecutionCallback {

    private static final ExtensionContext.Namespace NAMESPACE =
        ExtensionContext.Namespace.create(TestCategoriesExtension.class);

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        TestSize size = detectTestSize(context).orElse(null);
        if (size != null) {
            context.getStore(NAMESPACE).put("startTime", System.nanoTime());
            context.getStore(NAMESPACE).put("testSize", size);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        TestSize size = context.getStore(NAMESPACE).get("testSize", TestSize.class);
        if (size == null) return;

        long startTime = context.getStore(NAMESPACE).get("startTime", Long.class);
        Duration elapsed = Duration.ofNanos(System.nanoTime() - startTime);

        if (elapsed.compareTo(size.getTimeLimit()) > 0) {
            throw new TimingViolationException(
                ErrorMessages.timingExceeded(
                    context.getDisplayName(),
                    size,
                    size.getTimeLimit(),
                    elapsed
                )
            );
        }
    }

    private Optional<TestSize> detectTestSize(ExtensionContext context) {
        // Check method first, then class (method wins)
        return context.getTestMethod()
            .flatMap(TestSize::fromAnnotation)
            .or(() -> context.getTestClass().flatMap(TestSize::fromAnnotation));
    }
}
```

---

### 4. Maven Plugin (`maven-plugin`)

| Task | Description |
|------|-------------|
| 4.1 | Update placeholder Mojo with real configuration |
| 4.2 | Add `verbose` configuration option |
| 4.3 | Add `warnOnMissingAnnotation` configuration option |
| 4.4 | Wire Surefire to include the extension |
| 4.5 | Document usage in README |

#### Plugin Configuration

```xml
<plugin>
    <groupId>io.junit.categories</groupId>
    <artifactId>junit-test-categories-maven-plugin</artifactId>
    <version>0.1.0</version>
    <configuration>
        <!-- Log detailed info about each test -->
        <verbose>false</verbose>
        <!-- Warn when tests have no size annotation -->
        <warnOnMissingAnnotation>true</warnOnMissingAnnotation>
    </configuration>
</plugin>
```

---

## Test Strategy (TDD)

Every feature is implemented test-first. Example test progression:

### Test 1: Annotation exists and is detectable

```java
@Test
void smallTestAnnotationIsDetectable() {
    assertThat(SmallTest.class).isAnnotation();
    assertThat(SmallTest.class.getAnnotation(Retention.class).value())
        .isEqualTo(RetentionPolicy.RUNTIME);
}
```

### Test 2: TestSize enum has correct time limits

```java
@Test
void smallTestHasOneSecondLimit() {
    assertThat(TestSize.SMALL.getTimeLimit())
        .isEqualTo(Duration.ofSeconds(1));
}
```

### Test 3: Extension detects annotation

```java
@SmallTest
class AnnotatedTest {
    @Test
    void exampleTest() { }
}

@Test
void extensionDetectsSmallTestAnnotation() {
    // Integration test with JUnit Platform TestKit
}
```

### Test 4: Timing violation produces correct error

```java
@Test
void timingViolationMessageIncludesAllRequiredElements() {
    String message = ErrorMessages.timingExceeded(
        "MyTest.slowMethod",
        TestSize.SMALL,
        Duration.ofSeconds(1),
        Duration.ofMillis(2340)
    );

    assertThat(message)
        .contains("[TC006]")
        .contains("MyTest.slowMethod")
        .contains("SMALL")
        .contains("2.34 seconds")
        .contains("@MediumTest")
        .contains("junit-test-categories.dev/errors/TC006");
}
```

---

## File Structure After Phase 1

```
annotations/src/main/java/io/junit/categories/
├── SmallTest.java
├── MediumTest.java
├── LargeTest.java
├── XLargeTest.java
└── package-info.java

core/src/main/java/io/junit/categories/core/
├── TestSize.java
├── TimingViolationException.java
├── ErrorMessages.java
├── TestTimer.java
├── WallClockTimer.java
└── package-info.java

junit5/src/main/java/io/junit/categories/junit5/
├── TestCategoriesExtension.java
└── package-info.java

maven-plugin/src/main/java/io/junit/categories/maven/
├── TestCategoriesMojo.java
└── package-info.java
```

---

## Acceptance Criteria

Phase 1 is complete when:

- [ ] All four annotations exist and work at class and method level
- [ ] Method-level annotation overrides class-level
- [ ] Tests exceeding time limits fail with TC006 error
- [ ] Error messages include all required elements (what, why, fix, link)
- [ ] Verbose mode logs timing for each test
- [ ] `./mvnw clean verify` passes
- [ ] Tests have >80% code coverage
- [ ] README updated with usage instructions
- [ ] Published to GitHub Packages

---

## Out of Scope (Deferred)

| Feature | Deferred To |
|---------|-------------|
| Network blocking | Phase 2 |
| Filesystem blocking | Phase 3 |
| Database blocking | Phase 3 |
| Subprocess blocking | Phase 3 |
| Sleep blocking | Phase 3 |
| Enforcement modes (OFF/WARN/STRICT) | Phase 4 |
| Distribution validation | Phase 5 |
| JSON reporting | Phase 6 |
| Gradle plugin | Phase 7 |

---

## Dependencies

Phase 1 requires only:

| Module | Dependencies |
|--------|--------------|
| `annotations` | None (zero deps) |
| `core` | `annotations` |
| `junit5` | `core`, JUnit Jupiter API |
| `maven-plugin` | `junit5`, Maven Plugin API |

**No ByteBuddy yet** - that comes in Phase 2.

---

## Risks and Mitigations

| Risk | Mitigation |
|------|------------|
| JUnit 5 Extension timing accuracy | Use `System.nanoTime()`, document that timing is wall-clock |
| Parallel test execution | Use `ExtensionContext.Store` (scoped per test) |
| Annotation inheritance edge cases | Comprehensive tests for inheritance scenarios |
| User confusion without hermeticity | Clear docs that hermeticity is Phase 2 |

---

## Next Steps After Phase 1

Phase 2 adds **network interception** using ByteBuddy:
- Java agent module
- `Socket.connect()` interception
- `URL.openConnection()` interception
- Localhost-only for `@MediumTest`
