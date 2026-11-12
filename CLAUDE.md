# CLAUDE.md — toolshop-power-tools-tests (Java 21 · Maven · JUnit 6 · Selenium 4 · RestAssured · AssertJ)

I am the engineer; you are my pair in a live-coding interview. I must be able to explain every
line: small changes, follow existing patterns, one sentence of *why* for non-obvious choices.
Keep chat answers short — the interviewer is reading along.

## Commands
- Tests: `mvn -B test` (parallel by default) · tag `-Dgroups=smoke|ui|api|unit` · one `-Dtest=Class#method`
- Watch: `-DHEADLESS=false` · sequential: `-Djunit.jupiter.execution.parallel.enabled=false`
- Any setting: `-DUI_BASE_URL=...` · failures: `target/failures/*.png|html`, `target/surefire-reports/`
- Offline build (deps cached in ~/.m2): add `-o` if the network is slow
- Locators on a new page: `C:\Users\moham\Desktop\qa-interview-kit\.venv\Scripts\python C:\Users\moham\.claude\skills\qa-locators\scripts\locators.py <url> --aria`

## Working agreement
1. Plan before code: assumptions/questions → numbered test list (happy, negative, boundary, edge)
   → classes to touch. Wait for my go.
2. 1–3 tests per step; run them; show the real Maven output. Never claim "passes" without running.
3. Discover locators, never guess.
4. Failure → diagnose first (app / locator / timing / data / environment / client config such as
   headers) with evidence. Never weaken an assertion to go green.
5. Real app bug → assert the EXPECTED behaviour + `@KnownBug(value="BUG: ...", failureContains="...")`,
   draft a bug report (title, steps, expected, actual, severity).
6. No new dependencies, no unrelated reformatting, no renames without asking.

## Conventions
- Config only via `Settings.get()`; never inline URLs/credentials.
- UI: class annotated `@UiTest` → fresh `WebDriver` injected as a parameter (never create/quit
  drivers in tests). Locators = `private static final By` via `Locators.testId(...)` first;
  never absolute/index XPath or hashed classes. No `Thread.sleep`, no implicit waits: go through
  `BasePage` helpers (explicit `WebDriverWait`); "eventually" checks with
  `await().untilAsserted(() -> assertThat(...))`. Page objects are `final`, extend `BasePage`,
  expose intent methods and return the next page object; no assertions inside. BigDecimal for money.
- API: service class per resource on `ApiClient.request()`; methods return `Response`.
  Assert `assertStatus` → contract `Json.read(response, Record.class)` (strict) → values.
  Tests extend `ApiTestBase` (`anonymous`, `authorized()`, `cleanupAfterTest(...)`).
  Negatives per endpoint: no/invalid auth, missing/invalid fields, unknown id.
- Tests: one behaviour, Arrange/Act/Assert blocks, AssertJ only, behaviour-named methods,
  `@ParameterizedTest` + `argumentSet("readable", ...)`, thread-safe (parallel ON, no mutable
  statics), `@Smoke` on the critical path.

## Reference implementation (same conventions, fully working)
C:/Users/moham/Desktop/qa-interview-kit-java — read a file there for a full example (page objects
with fluent navigation, BookingApi service, ApiTestBase seeding, KnownBug usage, kata tests).
