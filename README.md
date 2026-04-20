# REST Assured API Test Framework

[![API Tests](https://github.com/PiyushSinghal-QA/rest-assured-api-tests/actions/workflows/api-tests.yml/badge.svg)](https://github.com/PiyushSinghal-QA/rest-assured-api-tests/actions/workflows/api-tests.yml)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9-blue.svg)](https://maven.apache.org/)
[![REST Assured](https://img.shields.io/badge/REST%20Assured-5.5-green.svg)](https://rest-assured.io/)
[![TestNG](https://img.shields.io/badge/TestNG-7.10-red.svg)](https://testng.org/)

A production-grade **REST API test automation framework** built with **Java 17**, **REST Assured**, **TestNG**, and **Maven** — with Allure reporting, JSON schema validation, data-driven testing, and GitHub Actions CI/CD.

This framework is a reference implementation demonstrating how to build scalable, maintainable API test automation for modern REST services.

---

## Key Features

- **REST Assured 5.x** — The industry standard for Java-based API testing
- **POJO-based requests/responses** — Strongly-typed, no raw JSON strings in tests
- **JSON Schema validation** — Contract testing catches breaking API changes automatically
- **TestNG data providers** — Parameterized negative test scenarios
- **Allure reporting** — Rich, interactive HTML reports with request/response capture
- **Environment configuration** — Dev, staging, prod configs via properties files
- **Thread-safe parallel execution** — Tests run in parallel at class level
- **Full CRUD coverage** — GET, POST, PUT, PATCH, DELETE all demonstrated
- **Authentication flow testing** — Login, register, negative auth scenarios
- **Response time assertions** — Catches performance regressions
- **CI/CD ready** — GitHub Actions workflow with Allure publishing
- **Clean logging** — SLF4J + Logback for structured test output

---

## Project Structure

```
rest-assured-api-tests/
├── .github/workflows/
│   └── api-tests.yml                    # CI/CD pipeline
├── src/test/
│   ├── java/com/piyushsinghal/api/
│   │   ├── base/
│   │   │   └── BaseTest.java            # Global setup, request spec, filters
│   │   ├── config/
│   │   │   └── ConfigManager.java       # Environment-aware config loader
│   │   ├── models/
│   │   │   ├── User.java                # POJO with Lombok
│   │   │   ├── UserListResponse.java
│   │   │   ├── LoginRequest.java
│   │   │   └── LoginResponse.java
│   │   ├── tests/
│   │   │   ├── GetUsersTest.java        # GET endpoints + schema validation
│   │   │   ├── CreateUserTest.java      # POST endpoints
│   │   │   ├── UpdateDeleteUserTest.java # PUT, PATCH, DELETE
│   │   │   └── AuthenticationTest.java   # Login, register, negative auth
│   │   └── utils/
│   │       ├── ApiEndpoints.java         # Centralized endpoint constants
│   │       └── TestDataProvider.java     # Data-driven test inputs
│   └── resources/
│       ├── config-dev.properties         # Dev environment config
│       ├── config-staging.properties     # Staging environment config
│       ├── testng.xml                    # Test suite definition
│       ├── logback.xml                   # Logging config
│       └── schemas/
│           └── single-user-schema.json   # JSON schema for contract testing
├── .gitignore
├── pom.xml                               # Maven dependencies
└── README.md
```

---

## Quick Start

### Prerequisites
- Java 17 or higher (`java --version`)
- Maven 3.8+ (`mvn --version`)

### Installation and execution

```bash
# Clone the repository
git clone https://github.com/PiyushSinghal-QA/rest-assured-api-tests.git
cd rest-assured-api-tests

# Run the full test suite
mvn clean test

# Run against staging environment
mvn clean test -DTEST_ENV=staging

# Override the base URL from the command line (useful for CI)
mvn clean test -Dbase.url=https://staging.myapi.com

# Run a single test class
mvn test -Dtest=GetUsersTest

# Run a single test method
mvn test -Dtest=GetUsersTest#shouldReturnSingleUserById

# Generate and open Allure report
mvn allure:serve
```

---

## Architecture Highlights

### POJO-based payloads instead of raw JSON

Tests stay clean and type-safe — no string concatenation, no JSON typos:

```java
// ✅ Clean, type-safe, IDE-auto-completable
User newUser = User.builder()
        .name("Piyush Singhal")
        .job("SDET")
        .build();

given().body(newUser).when().post("/api/users").then().statusCode(201);
```

vs. the common anti-pattern:

```java
// ❌ Fragile, hard to refactor, no type safety
String payload = "{\"name\":\"Piyush\",\"job\":\"SDET\"}";
```

### JSON Schema validation — contract testing done right

A single assertion validates the entire response structure:

```java
given()
    .pathParam("id", 1)
    .when().get("/api/users/{id}")
    .then()
    .statusCode(200)
    .body(matchesJsonSchemaInClasspath("schemas/single-user-schema.json"));
```

If the backend changes a field's type, adds a required field, or removes one — this test fails immediately. Way more powerful than asserting fields one by one.

### Data-driven negative testing with TestNG

The same test method runs against multiple scenarios:

```java
@Test(dataProvider = "invalidUserIds", dataProviderClass = TestDataProvider.class)
public void shouldReturn404ForInvalidUserId(int invalidId, String scenarioName) {
    given().pathParam("id", invalidId)
        .when().get("/api/users/{id}")
        .then().statusCode(404);
}
```

### Environment-aware configuration

Three layers of precedence, checked in order:
1. System properties (`-Dbase.url=...`) — highest priority, great for CI
2. Environment-specific file (`config-staging.properties`)
3. Default file (`config-dev.properties`)

Switch environments without changing code:

```bash
mvn test -DTEST_ENV=staging                        # Use staging config
mvn test -Dbase.url=https://prod.api.com           # Override just one value
```

### Allure reporting

Every request, response, header, and payload is captured automatically via the `AllureRestAssured` filter. Tests are organized by Epic → Feature → Story using annotations:

```java
@Epic("User Management API")
@Feature("Create Users")
public class CreateUserTest extends BaseTest {
    @Test
    @Story("Create user with complete data")
    @Severity(SeverityLevel.BLOCKER)
    public void shouldCreateUserWithValidData() { ... }
}
```

---

## Test Coverage

| Endpoint Category | Tests | Types Covered |
|-------------------|-------|---------------|
| GET /users        | 5     | List, single, schema validation, negative IDs, pagination |
| POST /users       | 3     | Full payload, minimal payload, empty body |
| PUT /users/{id}   | 1     | Full update |
| PATCH /users/{id} | 1     | Partial update |
| DELETE /users/{id}| 1     | Successful deletion |
| POST /login       | 3     | Valid login, missing password, missing email |
| POST /register    | 2     | Valid registration, missing password |

**Total:** 16 tests covering positive paths, negative scenarios, validation, contract testing, and performance assertions.

---

## Design Decisions

### Why REST Assured over HttpClient or OkHttp?
REST Assured was built for testing. Its fluent BDD-style syntax (`given().when().then()`) makes tests self-documenting, and it handles JSON serialization, path parameters, and response parsing out of the box.

### Why TestNG over JUnit?
Better data-driven testing support (`@DataProvider`), flexible parallel execution configuration at the suite/class/method level, and better grouping/tagging for complex test organization.

### Why Lombok?
A typical POJO needs getters, setters, `equals`, `hashCode`, `toString`, and constructors — that's 50+ lines of boilerplate for a 5-field class. Lombok generates it all from a single `@Data` annotation. Less boilerplate means fewer bugs and faster refactoring.

### Why Allure over default Surefire reports?
Surefire reports are functional but ugly. Allure produces interactive HTML reports with request/response capture, severity labels, step-by-step execution traces, and historical trend tracking. It's what clients actually want to see.

### Why environment-specific properties files?
Real projects have dev, staging, and prod with different URLs, credentials, and timeouts. Hard-coding URLs in tests is an anti-pattern — this setup makes environment switching a single CLI flag.

---

## What This Framework Demonstrates

This project showcases practical skills clients look for in a senior SDET:

- Building a production-grade Java API test framework from scratch
- Designing for scalability — adding a new endpoint takes 5 minutes, not 50
- Writing maintainable, type-safe API tests with proper POJO modeling
- Implementing contract testing via JSON schema validation
- Data-driven testing for efficient negative case coverage
- CI/CD integration with meaningful reports and artifacts
- Environment configuration strategy that matches real-world deployments
- Clean separation of concerns — config, data, models, tests, utilities

---

## About the Author

**Piyush Singhal** — SDET with 5+ years in test automation across SaaS, ad-tech, and enterprise networking products.

- [LinkedIn](https://www.linkedin.com/in/nitb-piyush-singhal/)
- [Upwork Profile](https://www.upwork.com/freelancers/~01d6783f97ce092c7f)
- ISTQB Certified (Foundation Level + Generative AI Testing)

## Related Projects

- [Playwright Automation Framework](https://github.com/PiyushSinghal-QA/playwright-automation-framework) — UI automation counterpart built with TypeScript + Playwright

---

## License

MIT — Free to use as a reference for your own projects.
