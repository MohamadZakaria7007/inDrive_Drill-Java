# Test report – Toolshop Power Tools

| | |
|---|---|
| Date | 2026-11-12 |
| Target | https://practicesoftwaretesting.com/category/power-tools (Toolshop v5.0), API https://api.practicesoftwaretesting.com |
| Environment | Windows 11, Java 21.0.2, Maven 3.9.6, Chrome 157 (headless, 1366×900) |
| Command | `mvn -B test` |

## Summary

| Total | Passed | Failed | Known bug (expected failure) |
|---|---|---|---|
| 14 | 13 | 0 | 1 |

## Results

| ID | Test | Result | Time (s) |
|---|---|---|---|
| A1 | `PowerToolsProductsApiTest.powerToolsCategoryReturnsOnlyPowerToolProducts` | Passed | 1.5 |
| A2 | `PowerToolsProductsApiTest.subcategoryFilterReturnsOnlyThatSubcategory` | Passed | 0.7 |
| A3 | `PowerToolsProductsApiTest.sortByPriceOrdersAllPowerTools` – price low to high | Passed | 0.3 |
| A3 | `PowerToolsProductsApiTest.sortByPriceOrdersAllPowerTools` – price high to low | Passed | 0.5 |
| A4 | `PowerToolsProductsApiTest.brandFilterReturnsOnlyThatBrand` | Passed | 0.8 |
| A6 | `PowerToolsProductsNegativeApiTest.unknownCategoryIdReturnsEmptyPage` | Passed | 1.5 |
| A6 | `PowerToolsProductsNegativeApiTest.unsupportedSortValueIsRejectedAsBadRequest` | **Known bug** (BUG-1) | – |
| U1 | `PowerToolsListingTest.listingShowsTitleAndEveryPowerToolWithNameAndPrice` | Passed | 3.5 |
| U2 | `PowerToolsListingTest.sortByPriceReordersAllProducts` – price low to high | Passed | 4.6 |
| U2 | `PowerToolsListingTest.sortByPriceReordersAllProducts` – price high to low | Passed | 3.6 |
| U4 | `PowerToolsListingTest.subcategoryFilterShowsOnlyThatSubcategory` | Passed | 4.1 |
| U7 | `PowerToolsListingTest.categoryAndBrandFiltersCombineToNarrowTheList` | Passed | 4.6 |
| U8 | `PowerToolsListingTest.filterWithNoMatchesShowsEmptyState` – eco-friendly only | Passed | 3.7 |
| U8 | `PowerToolsListingTest.filterWithNoMatchesShowsEmptyState` – empty subcategory Grinder | Passed | 2.8 |

Raw JUnit XML: `target/surefire-reports/` (uploaded as the `test-report` artifact in CI).

## Bugs found

### BUG-1 – Products API: unsupported `sort` value returns 500 instead of 400

- **Severity / priority:** S3 / P2. No data impact and the UI only sends valid values, but any
  client typo or crafted URL gets an unhandled server error instead of a clear validation message
  (also noisy in error monitoring).
- **Environment:** https://api.practicesoftwaretesting.com, anonymous, 2026-11-12
- **Steps:** 1. `GET /products?by_category_slug=power-tools&sort=bogus,sideways`
- **Expected:** `400 Bad Request` (or `422`) naming the allowed sort fields
  (`name`, `price`, `co2_rating` with `asc`/`desc`).
- **Actual:** `500` with body `{"message":"Something went wrong"}`.
- **Notes:** reproducible on every request. Automated as
  `PowerToolsProductsNegativeApiTest.unsupportedSortValueIsRejectedAsBadRequest` (`@KnownBug`,
  matches `but was: 500`).

## Observations (not bugs)

- No Power Tools product is eco-friendly, so the "Sustainability" filter always shows the empty state
  on this page. Worth checking with the product owner whether that is intended seed data.
- `GET /products?by_category=<parent Power Tools id>` returns 0 items: filtering by the parent id
  does not include subcategories. The UI works around it (checking the parent checks all children),
  but other API clients may be surprised.

## Not covered

Product detail, cart, compare, login (out of agreed scope); sort by name/CO₂, pagination, category
tree contract (designed, not automated); accessibility, performance, cross-browser.
