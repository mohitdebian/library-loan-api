# Library Loan API

A Spring Boot REST API for managing library books, loans, and reservations with dynamic daily fines, borrower limits, and a FIFO waitlist.

## Setup Instructions

1. **Clone the repository** (if not already done).
2. **Requirements**: Java 17, Maven (or use the included wrapper).
3. **Run the Application**:
   ```bash
   ./mvnw spring-boot:run
   ```
   For Windows, use:
   ```cmd
   .\mvnw.cmd spring-boot:run
   ```

4. The application will start on `http://localhost:8080`.
5. Access H2 Console at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:librarydb`, User: `sa`, Password: `password`).

## API Endpoints & Curl Examples

### 1. Add a Book
```bash
curl -X POST http://localhost:8080/api/books \
-H "Content-Type: application/json" \
-d '{"title": "The Great Gatsby", "author": "F. Scott Fitzgerald", "copiesAvailable": 1, "fineRatePerDay": 20.0}'
```

### 2. Borrow a Book
```bash
curl -X POST http://localhost:8080/api/borrow \
-H "Content-Type: application/json" \
-d '{"bookId": 1, "borrowerId": "user1"}'
```

### 3. Reserve a Book
(Only when `copiesAvailable` is 0)
```bash
curl -X POST http://localhost:8080/api/reserve \
-H "Content-Type: application/json" \
-d '{"bookId": 1, "borrowerId": "user2"}'
```

### 4. Return a Book
(Returns the book and calculates fine. If there is a waitlist, notifies the next user and keeps the book locked for them)
```bash
curl -X POST http://localhost:8080/api/return/1
```

### 5. Get Overdue Loans
```bash
curl -X GET http://localhost:8080/api/loans/overdue
```

## Features
- **Dynamic Fines**: Calculates fines per day based on the `fineRatePerDay` property of the `Book`.
- **FIFO Waitlist**: Users can reserve unavailable books. When a book is returned, the next user in the waitlist is notified and the book is locked for them.
- **Borrower Limits**: Max 3 active loans and 3 active reservations per borrower.
- **H2 In-Memory Database**: For easy local development and testing.

