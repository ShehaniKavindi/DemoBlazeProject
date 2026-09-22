package tests;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class DemoBlazeTests extends BaseTest {

    // ---- Reusable test data (class-level constants, used across tests) ----
    private static final String SAMSUNG_S6   = "Samsung galaxy s6";
    private static final String NOKIA_1520   = "Nokia lumia 1520";

    private static final String TEST_NAME    = "Test Student";
    private static final String TEST_COUNTRY = "Sri Lanka";
    private static final String TEST_CITY    = "Colombo";
    private static final String TEST_CARD    = "4111111111111111"; // fictitious test card only
    private static final String TEST_MONTH   = "12";
    private static final String TEST_YEAR    = "2027";

    // =====================================================================
    // TC01 - Home Page Smoke Test
    // Verify a non-empty title and displayed PRODUCT STORE heading.
    // =====================================================================
    @Test
    public void tc01_homePageSmokeTest() {
        String title = driver.getTitle();
        Assert.assertFalse(title.isEmpty(), "Page title should not be empty");

        WebElement brand = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("nava")));

        Assert.assertTrue(brand.isDisplayed(), "PRODUCT STORE heading should be displayed");
        Assert.assertTrue(brand.getText().contains("PRODUCT STORE"),
                "Heading should contain 'PRODUCT STORE'");
    }

    // =====================================================================
    // TC02 - Product Selection
    // Open Phones, select Samsung galaxy s6, verify heading, print price.
    // =====================================================================
    @Test
    public void tc02_productSelection() {
        navigateToProduct(SAMSUNG_S6);

        WebElement heading = driver.findElement(By.className("name"));
        Assert.assertEquals(heading.getText(), SAMSUNG_S6,
                "Product heading should match the selected product");

        WebElement priceEl = driver.findElement(By.className("price-container"));
        System.out.println("Samsung galaxy s6 price: " + priceEl.getText());
        Assert.assertTrue(priceEl.getText().contains("$"), "Price should contain a $ amount");
    }

    // =====================================================================
    // TC03 - Add to Cart
    // Add Samsung galaxy s6, wait for the JS alert, print its text, accept it.
    // =====================================================================
    @Test
    public void tc03_addToCartAndHandleAlert() {
        navigateToProduct(SAMSUNG_S6);

        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Add to cart"))).click();

        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        System.out.println("Add-to-cart alert text: " + alertText);

        Assert.assertFalse(alertText.isEmpty(), "Alert text should not be empty");
        alert.accept();
    }

    // =====================================================================
    // TC04 - Cart Management
    // Add Samsung galaxy s6 and Nokia lumia 1520, count and print cart rows,
    // remove Nokia, verify one row remains, verify Samsung remains, print total.
    // =====================================================================
    @Test
    public void tc04_cartManagement() {
        navigateToProduct(SAMSUNG_S6);
        addCurrentProductToCart();

        navigateToProduct(NOKIA_1520);
        addCurrentProductToCart();

        goToCart();
        wait.until(d -> d.findElements(By.cssSelector("#tbodyid tr")).size() >= 2);

        // findElements() + loop over a collection of WebElements
        List<WebElement> rows = driver.findElements(By.cssSelector("#tbodyid tr"));
        Assert.assertEquals(rows.size(), 2, "Cart should contain 2 rows before removal");

        for (WebElement row : rows) {
            String name = row.findElement(By.xpath("./td[2]")).getText();
            String price = row.findElement(By.xpath("./td[3]")).getText();
            System.out.println("Cart item -> " + name + " : $" + price);
        }

        // Remove the Nokia lumia 1520 row
        for (WebElement row : driver.findElements(By.cssSelector("#tbodyid tr"))) {
            if (row.getText().contains(NOKIA_1520)) {
                row.findElement(By.linkText("Delete")).click();
                break;
            }
        }

        // Wait for the cart table to update to a single row
        wait.until(d -> d.findElements(By.cssSelector("#tbodyid tr")).size() == 1);

        List<WebElement> remaining = driver.findElements(By.cssSelector("#tbodyid tr"));
        Assert.assertEquals(remaining.size(), 1, "One row should remain after removing Nokia lumia 1520");
        Assert.assertTrue(remaining.get(0).getText().contains(SAMSUNG_S6),
                "Remaining row should be Samsung galaxy s6");

        String total = driver.findElement(By.id("totalp")).getText();
        System.out.println("Cart total: $" + total);
        Assert.assertFalse(total.isEmpty(), "Cart total should not be empty");
    }

    // =====================================================================
    // TC05 - Checkout Validation
    // From a cart containing Samsung galaxy s6:
    //   1) verify the invalid result for missing Name/Card details
    //   2) submit the form using fictitious data and verify success
    // =====================================================================
    @Test
    public void tc05_checkoutValidation() {
        navigateToProduct(SAMSUNG_S6);
        addCurrentProductToCart();
        goToCart();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Place Order']"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("orderModal")));

        // --- Invalid scenario: click Purchase with all fields empty ---
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Purchase']"))).click();

        wait.until(ExpectedConditions.alertIsPresent());
        Alert invalidAlert = driver.switchTo().alert();
        String invalidAlertText = invalidAlert.getText();
        System.out.println("Invalid checkout alert: " + invalidAlertText);

        Assert.assertTrue(
                invalidAlertText.toLowerCase().contains("name")
                        && invalidAlertText.toLowerCase().contains("card"),
                "Alert should mention missing Name and Card");
        invalidAlert.accept();

        // --- Valid scenario: fill fictitious data and submit ---
        driver.findElement(By.id("name")).sendKeys(TEST_NAME);
        driver.findElement(By.id("country")).sendKeys(TEST_COUNTRY);
        driver.findElement(By.id("city")).sendKeys(TEST_CITY);
        driver.findElement(By.id("card")).sendKeys(TEST_CARD);
        driver.findElement(By.id("month")).sendKeys(TEST_MONTH);
        driver.findElement(By.id("year")).sendKeys(TEST_YEAR);

        driver.findElement(By.xpath("//button[text()='Purchase']")).click();

        WebElement successHeading = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".sweet-alert h2")));
        Assert.assertEquals(successHeading.getText(), "Thank you for your purchase!",
                "Purchase success message should be shown");

        driver.findElement(By.xpath("//button[text()='OK']")).click();
    }

    // =====================================================================
    // Reusable helper methods (kept private - not @Test methods themselves,
    // but used by several tests above to avoid duplicating the same steps)
    // =====================================================================

    /** Opens the Phones category and clicks the given product's link. */
    private void navigateToProduct(String productName) {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Phones"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText(productName))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("name")));
    }

    /** Clicks "Add to cart" on the currently open product page and accepts the resulting alert. */
    private void addCurrentProductToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Add to cart"))).click();
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
    }

    /** Navigates to the cart page and waits for the cart table to be present. */
    private void goToCart() {
        driver.findElement(By.id("cartur")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tbodyid")));
    }
}
