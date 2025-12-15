# Error Codes Reference

This document describes all error codes produced by junit-test-categories.

## Error Code Format

All errors follow this format:

```
[TCXXX] Error Title
======================================================================
Test: com.example.MyTest.testMethod
Category: SMALL

What happened:
  <Description of what was detected>

Why it matters:
  <Explanation of why this violates test category constraints>

To fix this (choose one):
  * <Option 1>
  * <Option 2>
  * <Option 3>

See: https://junit-test-categories.dev/errors/TCXXX
======================================================================
```

## Hermeticity Violations

### TC001 - Network Access Violation

**Severity**: Error (in STRICT mode)

**Triggered when**: A small test attempts to make a network connection.

**Example**:
```
[TC001] Network Access Violation
======================================================================
Test: com.example.UserServiceTest.fetchesUserFromApi
Category: SMALL

What happened:
  Attempted network connection to api.example.com:443

Why it matters:
  Small tests must be hermetic and cannot access the network.
  Network calls make tests slow and non-deterministic.

To fix this (choose one):
  * Mock the HTTP client using Mockito
  * Use a fake implementation via dependency injection
  * Change test category to @MediumTest (if network access is required)

See: https://junit-test-categories.dev/errors/TC001
======================================================================
```

---

### TC002 - Filesystem Access Violation

**Severity**: Error (in STRICT mode)

**Triggered when**: A small test attempts to access the filesystem.

**Example**:
```
[TC002] Filesystem Access Violation
======================================================================
Test: com.example.ConfigLoaderTest.readsConfigFile
Category: SMALL

What happened:
  Attempted filesystem access: /etc/app/config.yml

Why it matters:
  Small tests must be hermetic and cannot access the filesystem.
  File operations make tests environment-dependent.

To fix this (choose one):
  * Use in-memory streams instead of file streams
  * Mock the filesystem layer
  * Change test category to @MediumTest (if file access is required)

See: https://junit-test-categories.dev/errors/TC002
======================================================================
```

---

### TC003 - Subprocess Spawn Violation

**Severity**: Error (in STRICT mode)

**Triggered when**: A small test attempts to spawn a subprocess.

**Example**:
```
[TC003] Subprocess Spawn Violation
======================================================================
Test: com.example.GitServiceTest.getsCurrentBranch
Category: SMALL

What happened:
  Attempted to spawn subprocess: git rev-parse --abbrev-ref HEAD

Why it matters:
  Small tests must be hermetic and cannot spawn subprocesses.
  Subprocess calls are slow and environment-dependent.

To fix this (choose one):
  * Mock the process execution layer
  * Use a fake implementation for testing
  * Change test category to @MediumTest (if subprocess is required)

See: https://junit-test-categories.dev/errors/TC003
======================================================================
```

---

### TC004 - Database Access Violation

**Severity**: Error (in STRICT mode)

**Triggered when**: A small test attempts to connect to a database.

**Example**:
```
[TC004] Database Access Violation
======================================================================
Test: com.example.UserRepositoryTest.savesUser
Category: SMALL

What happened:
  Attempted database connection: jdbc:postgresql://localhost:5432/mydb

Why it matters:
  Small tests must be hermetic and cannot access databases.
  Database operations are slow and require external services.

To fix this (choose one):
  * Use an in-memory repository implementation for testing
  * Mock the repository layer
  * Change test category to @MediumTest (if database access is required)

See: https://junit-test-categories.dev/errors/TC004
======================================================================
```

---

### TC005 - Sleep Call Violation

**Severity**: Error (in STRICT mode)

**Triggered when**: A small test calls Thread.sleep() or similar.

**Example**:
```
[TC005] Sleep Call Violation
======================================================================
Test: com.example.RetryServiceTest.retriesOnFailure
Category: SMALL

What happened:
  Attempted to sleep for 1000ms

Why it matters:
  Small tests must complete quickly and cannot use sleep calls.
  Sleep calls make tests slow and often indicate a design issue.

To fix this (choose one):
  * Use a Clock abstraction that can be controlled in tests
  * Inject delays as dependencies that can be mocked
  * Change test category to @MediumTest (if timing is essential)

See: https://junit-test-categories.dev/errors/TC005
======================================================================
```

---

## Timing Violations

### TC006 - Timing Exceeded

**Severity**: Error (in STRICT mode)

**Triggered when**: A test exceeds its time limit.

| Category | Time Limit |
|----------|------------|
| Small | 1 second |
| Medium | 5 minutes |
| Large | 15 minutes |
| XLarge | 15 minutes |

**Example**:
```
[TC006] Timing Exceeded
======================================================================
Test: com.example.DataProcessorTest.processesLargeDataset
Category: SMALL

What happened:
  Test took 2.34 seconds (limit: 1.00 seconds)

Why it matters:
  Small tests must complete within 1 second.
  Slow tests indicate they may be doing too much.

To fix this (choose one):
  * Reduce the scope of the test (test smaller units)
  * Use faster test doubles instead of real implementations
  * Change test category to @MediumTest (if the test legitimately needs more time)

See: https://junit-test-categories.dev/errors/TC006
======================================================================
```

---

## Distribution Violations

### TC007 - Distribution Warning

**Severity**: Warning or Error (depending on mode)

**Triggered when**: The test suite distribution doesn't match targets.

**Example**:
```
[TC007] Distribution Warning
======================================================================
Test Suite: com.example

Distribution:
  Small:  45 tests (45.0%) - Target: 80% ± 5%  [VIOLATION]
  Medium: 35 tests (35.0%) - Target: 15% ± 5%  [VIOLATION]
  Large:  20 tests (20.0%) - Target: 5% ± 5%   [VIOLATION]

Why it matters:
  A healthy test pyramid has mostly small tests (fast, reliable)
  with fewer medium and large tests (slower, more brittle).

To fix this:
  * Convert medium/large tests to small tests where possible
  * Extract testable units from integration tests
  * Review if all large tests are truly necessary

See: https://junit-test-categories.dev/errors/TC007
======================================================================
```

---

## See Also

- [Google Testing Blog: Test Sizes](https://testing.googleblog.com/2010/12/test-sizes.html)
- [Software Engineering at Google](https://abseil.io/resources/swe-book)
