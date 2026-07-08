```markdown
# library-loan-api Development Patterns

> Auto-generated skill from repository analysis

## Overview
This skill teaches the core development patterns and conventions used in the `library-loan-api` Java codebase. You'll learn about file naming, import/export styles, commit message habits, and how to write and run tests. The repository is a Java-based API for managing library loans, with no specific framework detected.

## Coding Conventions

### File Naming
- **Pattern:** PascalCase
- **Example:**  
  `LibraryLoanService.java`  
  `UserRepository.java`

### Import Style
- **Pattern:** Relative imports  
- **Example:**
  ```java
  import library.loan.User;
  import library.loan.services.LoanService;
  ```

### Export Style
- **Pattern:** Named exports (public classes and methods)
- **Example:**
  ```java
  public class LoanService {
      public void issueLoan(Book book, User user) { ... }
  }
  ```

### Commit Messages
- **Pattern:** Freeform, no strict prefixes  
- **Average Length:** ~35 characters  
- **Example:**  
  `Add book return endpoint`  
  `Fix loan duration calculation`

## Workflows

### Adding a New Feature
**Trigger:** When implementing a new feature or endpoint  
**Command:** `/add-feature`

1. Create a new Java file using PascalCase.
2. Use relative imports for dependencies.
3. Export new classes/methods as `public`.
4. Write or update corresponding test files (see Testing Patterns).
5. Commit changes with a clear, concise message.

### Fixing a Bug
**Trigger:** When resolving a bug or issue  
**Command:** `/fix-bug`

1. Locate the relevant Java file(s).
2. Apply the fix, following coding conventions.
3. Update or add tests to cover the fix.
4. Commit with a descriptive message about the bug fix.

### Writing and Running Tests
**Trigger:** When adding or updating tests  
**Command:** `/run-tests`

1. Create or update test files matching the `*.test.*` pattern.
2. Use the same coding conventions as production code.
3. Run tests using the project's preferred method (framework not specified; check project documentation or use standard Java test runners).

## Testing Patterns

- **Framework:** Unknown (not detected)
- **File Pattern:** `*.test.*` (e.g., `LoanService.test.java`)
- **Style:** Mirror production code structure and conventions.
- **Example:**
  ```java
  public class LoanServiceTest {
      @Test
      public void testIssueLoan() {
          // test logic here
      }
  }
  ```

## Commands
| Command       | Purpose                                   |
|---------------|-------------------------------------------|
| /add-feature  | Start workflow for adding a new feature   |
| /fix-bug      | Start workflow for fixing a bug           |
| /run-tests    | Run all test files in the codebase        |
```
