# Toolshop Power Tools – test automation

UI and API tests for the **Power Tools** category of the Toolshop demo
(<https://practicesoftwaretesting.com/category/power-tools>): product listing, sorting and filters.

**Stack:** Java 21 · Maven · JUnit 6 · Selenium 4 (Chrome) · RestAssured · Jackson · AssertJ · Awaitility

## Run

```bash
mvn -B test                         # everything (runs in parallel)
mvn -B test -Dgroups=smoke          # critical path only
mvn -B test -Dgroups=api            # API only (~5 s)
mvn -B test -Dgroups=ui             # UI only (headless Chrome)
mvn -B test -Dtest=PowerToolsListingTest -DHEADLESS=false   # watch the browser
```

Configuration comes from `-DKEY=value`, then environment variables, then `.env` (copy `.env.example`).
No credentials are needed: the catalogue is public.

| Key | Value |
|---|---|
| `UI_BASE_URL` | `https://practicesoftwaretesting.com` |
| `API_BASE_URL` | `https://api.practicesoftwaretesting.com` |
| `TEST_ID_ATTRIBUTE` | `data-test` |
| `BROWSER` / `HEADLESS` | `chrome` / `true` |

Results: `target/surefire-reports/`; screenshot + HTML of any failed UI test: `target/failures/`.
Latest run and the bug found: [TEST_REPORT.md](TEST_REPORT.md).

## CI

GitHub Actions ([tests.yml](.github/workflows/tests.yml)) runs the **API tests only**: API smoke
first (fail fast), then the API regression, and uploads the reports.

The UI tests are excluded on purpose. The shop's web host is behind Cloudflare bot protection, and
GitHub-hosted runners get a "Verify you are human" page instead of the app (confirmed from the
screenshot and page source the UI test saved in CI). The API host is not behind that protection.
Getting past the check would mean evading the site's bot protection, so the UI suite runs locally
instead: `mvn -B test -Dgroups=ui`.

To bring UI tests back into CI: run the open-source Toolshop app inside the workflow (Docker) and
point `UI_BASE_URL` / `API_BASE_URL` at it, or use a self-hosted runner the site does not challenge.

## What is covered

| ID | Layer | Test | Class |
|---|---|---|---|
| A1 | API | Power Tools returns 8 products, strict contract, all in Power Tools subcategories | `PowerToolsProductsApiTest` |
| A2 | API | Subcategory filter (Sander) returns only sanders | `PowerToolsProductsApiTest` |
| A3 | API | Sort by price ascending / descending | `PowerToolsProductsApiTest` |
| A4 | API | Brand filter returns only that brand | `PowerToolsProductsApiTest` |
| A6 | API | Unknown category id → empty page; unsupported sort → 400 (**known bug: 500**) | `PowerToolsProductsNegativeApiTest` |
| U1 | UI | Page title and 8 cards, each with a name and a positive price | `PowerToolsListingTest` |
| U2 | UI | Sort by price Low→High / High→Low reorders the grid | `PowerToolsListingTest` |
| U4 | UI | Sander filter shows exactly the 3 sanders | `PowerToolsListingTest` |
| U7 | UI | Category + brand filters combine (Drill + MightyCraft → Cordless Drill 12V) | `PowerToolsListingTest` |
| U8 | UI | Eco-friendly only / empty Grinder subcategory → "There are no products found." | `PowerToolsListingTest` |

Designed but not automated yet: A5 (category tree contract), A7 (pagination), U3 (sort by name),
U5 (two subcategories = union), U6 (parent checkbox selects all subcategories).

## Layout

```
src/test/java/com/qakit/
  framework/
    api/          ApiClient (immutable RestAssured spec), ProductsApi (service), Json (strict), models/
    ui/pages/     BasePage (explicit waits), CategoryPage, SortOption
    junit/        @UiTest (driver per test, screenshot on failure), @ApiTest, @Smoke, @KnownBug
    config/       Settings (-D > env > .env)
  tests/api/      PowerToolsProductsApiTest, PowerToolsProductsNegativeApiTest
  tests/ui/       PowerToolsListingTest
```

## Design decisions

- **Pyramid:** filter and sort *rules* are proven at API level (fast, exact); UI tests cover what
  the user sees on the same page.
- **Contract checks:** responses are parsed into Java records with a strict Jackson mapper, so a
  new, missing or wrongly-typed field fails the test with the body in the message.
- **No hard-coded generated ids:** category and brand ids (ULIDs) are resolved by slug/name via the
  API; UI checkboxes are located by their visible label + `data-test` prefix.
- **No sleeps:** explicit `WebDriverWait` in page objects, `await().untilAsserted(...)` for
  "eventually" checks while Angular re-renders the grid.
- **Known bugs stay visible:** `@KnownBug` asserts the *expected* behaviour and only skips when the
  failure matches the documented symptom (`but was: 500`); any other failure is reported, and the
  test fails once the bug is fixed so the annotation gets removed.
- **Test data:** the shop is a shared public demo; expected counts/names come from its seed data
  (8 power tools). If the seed changes, update the constants or switch the UI tests to use the API
  as the oracle.
