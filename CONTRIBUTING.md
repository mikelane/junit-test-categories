# Contributing to junit-test-categories

Thank you for your interest in contributing to junit-test-categories! This document provides guidelines for contributing to the project.

## Code of Conduct

This project follows the [Contributor Covenant Code of Conduct](https://www.contributor-covenant.org/version/2/1/code_of_conduct/). By participating, you are expected to uphold this code.

## Getting Started

### Prerequisites

- Java 11 or higher (LTS versions: 11, 17, 21)
- Maven 3.6 or higher
- Git

### Setting Up the Development Environment

```bash
# Clone the repository
git clone https://github.com/mikelane/junit-test-categories.git
cd junit-test-categories

# Build the project
mvn clean install

# Run tests
mvn test
```

## How to Contribute

### Reporting Bugs

1. **Search existing issues** to avoid duplicates
2. **Create a new issue** with:
   - Clear, descriptive title
   - Steps to reproduce
   - Expected vs actual behavior
   - Java version, Maven version, OS
   - Minimal code example if possible

### Suggesting Features

1. **Check the roadmap** ([ROADMAP.md](ROADMAP.md)) to see if it's planned
2. **Check existing issues** to avoid duplicates
3. **Create a new issue** with:
   - Clear description of the feature
   - Use case / problem it solves
   - Proposed solution (if you have one)
   - Whether you're willing to implement it

### Contributing Code

#### 1. Create an Issue First

**All work must have a GitHub issue before starting.** This ensures:
- The work is aligned with project goals
- No duplicate effort
- Design decisions are discussed before implementation

#### 2. Fork and Branch

```bash
# Fork the repository on GitHub, then:
git clone https://github.com/YOUR-USERNAME/junit-test-categories.git
cd junit-test-categories
git remote add upstream https://github.com/mikelane/junit-test-categories.git

# Create a feature branch
git checkout -b feature/issue-123-description
```

#### 3. Follow TDD

This project follows strict Test-Driven Development:

1. **Write a failing test first**
2. **Write minimal code to pass**
3. **Refactor while keeping tests green**

```java
// 1. RED - Write a failing test
@Test
void detectsSmallTestAnnotation() {
    // This test should fail initially
    TestSize size = TestSizeDetector.detect(SmallTestExample.class);
    assertThat(size).isEqualTo(TestSize.SMALL);
}

// 2. GREEN - Write minimal implementation
// 3. REFACTOR - Clean up while tests pass
```

#### 4. Code Style

- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use meaningful names over comments
- Keep methods small and focused
- Run `mvn spotless:apply` before committing

#### 5. Commit Messages

Use conventional commit format:

```
feat: add network interception for small tests

- Implement Socket.connect() interception via ByteBuddy
- Add NetworkViolationException with actionable message
- Add tests for localhost detection

Fixes #123
```

Prefixes:
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation only
- `test:` - Adding or updating tests
- `refactor:` - Code change that neither fixes a bug nor adds a feature
- `chore:` - Build, CI, or tooling changes

**Do NOT include:**
- Co-authored-by lines
- Attribution lines
- Emojis

#### 6. Open a Pull Request

1. Push your branch to your fork
2. Open a PR against `main`
3. Fill out the PR template
4. Link to the issue using "Fixes #123"
5. Wait for review

### Pull Request Guidelines

- **One PR per issue** - Keep changes focused
- **Keep PRs small** - Easier to review, faster to merge
- **Update documentation** - If you change behavior, update docs
- **Add tests** - All new code needs tests
- **Maintain 100% coverage** - Don't reduce coverage

## Development Guidelines

### Project Structure

```
junit-test-categories/
├── annotations/     # @SmallTest, @MediumTest, etc.
├── core/            # TestSize, violations, enforcement
├── agent/           # ByteBuddy instrumentation
├── junit5/          # JUnit 5 Extension
├── maven-plugin/    # Maven integration
├── gradle-plugin/   # Gradle integration
└── docs/            # Documentation
```

### Testing Philosophy

This project enforces the same testing philosophy it provides:

- **Small tests**: Pure unit tests, no I/O
- **Medium tests**: Integration tests with local resources
- **Large tests**: End-to-end tests (rare)

Run the full test suite:

```bash
mvn test
```

### Documentation

- Update documentation in the **same commit** as code changes
- Use clear, concise language
- Include code examples
- Keep the README focused; detailed docs go in `docs/`

## Design Principles

Before contributing, please read and understand:

- [PRD.md](PRD.md) - Product requirements
- [docs/architecture/design-philosophy.md](docs/architecture/design-philosophy.md) - Why we make certain choices

### Key Principles to Uphold

1. **No escape hatches** - Don't add per-test override markers
2. **Fixed constraints** - Don't make time limits or distribution targets configurable
3. **Actionable errors** - Every error message must include remediation guidance
4. **Feature parity** - Match [pytest-test-categories](https://github.com/mikelane/pytest-test-categories) behavior

## Getting Help

- **Questions**: Open a GitHub Discussion
- **Bugs**: Open a GitHub Issue
- **Security**: See [SECURITY.md](SECURITY.md)

## Recognition

Contributors will be recognized in:
- GitHub contributors list
- Release notes for significant contributions
- README acknowledgments for major features

Thank you for contributing to junit-test-categories!
