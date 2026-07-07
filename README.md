# BookFlow

BookFlow is a small library loan management service. Members can loan books, return them, and rate the ones they've read. This was built for Nordea's technical assessment.

## Stack

Java 17, Spring Boot 3.5, Spring WebFlux for the web layer, Spring Data JDBC for persistence, H2 as an in-memory database, and Maven.

One thing worth flagging up front: I used Spring Data JDBC instead of JPA/Hibernate. The assignment only requires WebFlux for the web layer, so persistence was mine to choose, and I picked JDBC on purpose. It doesn't have lazy-loading proxies, an entity manager, or any of the "magic" that makes JPA powerful but also harder to reason about when something goes wrong. For a project this size, I'd rather have code I can fully explain than a framework doing things behind my back.

Since Spring Data JDBC is blocking and WebFlux is reactive, every controller wraps its service calls like this:

```java
Mono.fromCallable(() -> someService.doSomething())
    .subscribeOn(Schedulers.boundedElastic())
```

That pushes the actual blocking JDBC call onto a thread pool meant for blocking work, instead of tying up Netty's event loop. It's a pragmatic middle ground: I get to keep persistence simple without giving up the reactive web layer the assignment asks for.

## Running it

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`. `schema.sql` and `data.sql` run automatically on every startup, so there's always a fresh, seeded database ready to go — 6 books, 4 members, a handful of loans (some active, some already returned), and a few ratings.

```bash
./mvnw test
```

runs everything: a unit test for `LoanService` and an integration test that spins up the real app and hits it over HTTP.

## API

Every endpoint is under `/api`. There's no real login system here — the assignment explicitly allows simulating the active user, so I pass it as an `X-Member-Id` header on every request. If I were building this for real, this header would be replaced by whatever the actual authentication layer resolves the current user to be (a JWT claim, a session, etc.) — the header is a stand-in, not a design I'd defend for production.

| Method | Path | Header | Body | What it does |
|---|---|---|---|---|
| GET | `/api/books` | — | — | Lists every book, with inventory and its average rating |
| GET | `/api/books/{bookId}` | — | — | One book's details |
| GET | `/api/loans` | `X-Member-Id` | — | The active member's current (not yet returned) loans |
| POST | `/api/loans` | `X-Member-Id` | `{ "bookId": 1 }` | Loans a book |
| POST | `/api/loans/{loanId}/return` | `X-Member-Id` | — | Returns a book |
| POST | `/api/books/{bookId}/ratings` | `X-Member-Id` | `{ "score": 5, "comment": "..." }` | Rates a book (score 1-5, comment optional) |

## Design decisions

A few choices I made that I think are worth explaining rather than leaving implicit:

**Foreign keys are plain `Long` fields, not object relationships.** `Loan` has a `bookId` and a `memberId`, not a `Book book` and a `Member member`. It's simpler, and it sidesteps the lazy-loading questions that come with real object relationships — I'd rather load what I need explicitly than wonder whether a field is going to trigger a hidden query.

**Only the fields that actually change after creation have setters.** `Book.availableCopies` is the only mutable field on `Book`; everything else is set once in the constructor and left alone. Same idea on `Loan` — instead of a generic setter, there's a `markReturned(LocalDateTime)` method, because "returning a loan" is a specific thing that happens, not an arbitrary field update.

**A member can only have one rating per book.** There's a unique constraint on `(book_id, member_id)`, and re-rating a book updates the existing row instead of creating a second one. I think this matches how ratings actually work in most apps people are used to.

**`LoanService.returnBook` checks ownership before it checks whether the loan was already returned.** If someone tries to return a loan that isn't theirs, they get rejected immediately — they don't get to find out, as a side effect, whether that loan happens to already be returned. Small thing, but it's a deliberate ordering choice.

**Inventory changes happen after the thing that caused them succeeds, not before.** Loaning a book decrements `availableCopies` before creating the `Loan` row, so a failed inventory check never leaves a half-created loan behind. Returning a book increments `availableCopies` only after the loan itself has been saved as returned.

**No `@Transactional` anywhere, and that's a real gap, not an oversight.** `loanBook` and `returnBook` both do two separate writes (inventory + loan) that aren't wrapped in one atomic transaction. Given more time, I'd add `@Transactional` to both methods so a crash mid-operation can't leave the database in an inconsistent state.

**Response DTOs only exist where they're actually needed.** `BookResponse` exists because the book listing needs an average rating, which isn't a field on `Book` at all — it's computed from every `Rating` row for that book. `Loan` and `Rating` get returned directly from the API, because wrapping them in a near-identical DTO would just be extra code with nothing to show for it.

## A couple of bugs I hit while building this, worth mentioning

**H2 folds table names to uppercase by default, and I fought that for a bit.** My entities had `@Table("books")` written in lowercase, which Spring Data JDBC uses exactly as written — no case conversion. But `schema.sql` created the table unquoted, so H2 stored it as `BOOKS`. Every query failed with "table not found" until I realized the mismatch was in casing, not naming. My first fix attempt (forcing H2 to fold to lowercase instead) actually broke something else — Spring Data JDBC always renders auto-derived column names in uppercase regardless of that setting, so I'd just moved the mismatch from tables to columns. The real fix was uppercasing the `@Table` annotations to match H2's actual default.

**Two test classes each spinning up their own Spring context can quietly corrupt each other's data.** Both of my `@SpringBootTest` classes pointed at the same named in-memory database, so the second one to start tried to re-insert the same seed data into a database the first one had already populated, and hit a unique constraint violation on `members.email`. The fix was to let each test context get its own randomly-named database instead of sharing one fixed name.

## What I'd do with more time

- Add `@Transactional` to the loan/return flows
- A proper auth layer instead of the `X-Member-Id` header
- The optional Angular dashboard
- More test coverage — right now there's exactly the one unit test and one integration test the assignment asks for, not a full suite
