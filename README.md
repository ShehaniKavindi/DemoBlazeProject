DemoBlaze Selenium Java Automation Project
===========================================
Unit: HF2W - Software Engineering II (Software Testing, QA and Maintenance)
Component A - Selenium Java Automation Project

PROJECT PURPOSE
---------------
Beginner-level automated smoke/regression suite for https://www.demoblaze.com/
covering: home page smoke check, product selection, add-to-cart with alert
handling, cart management (add/remove/count via findElements + loop), and
checkout validation (invalid empty submit, then valid submit).

ENVIRONMENT
-----------
- Java JDK 17+
- Apache Maven 3.9+
- Selenium WebDriver 4.21.0
- TestNG 7.10.2
- Google Chrome (latest)
- IDE: IntelliJ IDEA or Eclipse

PROJECT STRUCTURE
------------------
pom.xml                                    - Maven dependencies (Selenium + TestNG)
testng.xml                                 - TestNG suite definition
src/test/java/tests/BaseTest.java          - shared WebDriver setup/teardown
src/test/java/tests/DemoBlazeTests.java    - the 5 required @Test methods (TC01-TC05)
screenshots/                               - execution evidence (add your own screenshots here)

HOW TO RUN
----------
Option A - IDE:
  1. Open this folder as a Maven project in IntelliJ/Eclipse.
  2. Let Maven download dependencies (first time only).
  3. Right-click testng.xml -> Run.

Option B - Command line (from the project root):
  mvn test

TEST CASES IMPLEMENTED
-----------------------
TC01 - Home Page Smoke Test
  Verifies a non-empty page title and that the "PRODUCT STORE" heading is displayed.

TC02 - Product Selection
  Opens Phones category, selects Samsung galaxy s6, verifies the product
  heading, and prints the price to the console.

TC03 - Add to Cart
  Adds Samsung galaxy s6, waits for the JavaScript alert, prints its text,
  and accepts it.

TC04 - Cart Management
  Adds Samsung galaxy s6 and Nokia lumia 1520, counts and prints cart rows
  using findElements() + a loop, removes Nokia lumia 1520, verifies exactly
  one row remains and that it is Samsung galaxy s6, then prints the cart total.

TC05 - Checkout Validation
  From a cart containing Samsung galaxy s6: first clicks Purchase with all
  fields empty and verifies the resulting alert mentions the missing
  Name/Card details; then fills the form with fictitious test data and
  verifies the "Thank you for your purchase!" success message.

FICTITIOUS CHECKOUT DATA USED (no real personal/payment data)
---------------------------------------------------------------
Name: Test Student | Country: Sri Lanka | City: Colombo
Card: 4111111111111111 (test data only) | Month: 12 | Year: 2027

ASSUMPTIONS & KNOWN LIMITATIONS
---------------------------------
- DemoBlaze is a public demo site; its markup can change without notice.
  Locators were verified against the current live structure at the time of
  writing, but should be re-checked (right-click -> Inspect) if a test fails
  on exam day.
- The "invalid checkout" scenario relies on DemoBlaze's own client-side
  check, which raises a JavaScript alert mentioning the missing Name/Card
  fields rather than an inline form error message.
- No login/registration flow is included, as it is outside the assignment
  scope (guest checkout only).

AUTHENTICITY NOTE
------------------
This code was generated with AI assistance as a learning/starting-point
resource. Before submission and the viva voce, review every method until
you can explain: why each locator was chosen, what each explicit wait is
waiting for, what each assertion checks, and how the cleanup guarantees
the browser closes. You will be asked to modify and re-run this code live.
