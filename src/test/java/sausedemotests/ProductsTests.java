package sausedemotests;

import core.BaseTest;
import org.example.BrowserTypes;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.util.List;
import java.util.Locale;


//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductsTests extends BaseTest {

    @BeforeEach
    public void beforeAllTests(){
        driver = startBrowser(BrowserTypes.CHROME);

        // Configure wait
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Navigate to Google.com
        driver.get("https://www.saucedemo.com/");

        authenticateWithUser("standard_user", "secret_sauce");
    }

    @Test
//    @Order(1)
    public void productAddedToShoppingCart_when_addToCart(){
        String backpackTitle = "Sauce Labs Backpack";
        String shirtTitle = "Sauce Labs Bolt T-Shirt";

        WebElement backpack = getProductByTitle(backpackTitle);
        backpack.findElement(By.className("btn_inventory")).click();

        var tshirt = getProductByTitle(shirtTitle);
        tshirt.findElement(By.className("btn_inventory")).click();

        driver.findElement(By.className("shopping_cart_link")).click();

        // Assert Items and Totals
        var items = driver.findElements(By.className("inventory_item_name"));

        Assertions.assertEquals(2, items.size(), "Items count not as expected");

        Assertions.assertEquals(backpackTitle, items.get(0).getText(), "Item title not as expected");
        Assertions.assertEquals(shirtTitle, items.get(1).getText(), "Item title not as expected");
    }

    @Test
//    @Order(2)
    public void userDetailsAdded_when_checkoutWithValidInformation(){
        String backpackTitle = "Sauce Labs Backpack";
        String shirtTitle = "Sauce Labs Bolt T-Shirt";

        WebElement backpack = getProductByTitle(backpackTitle);
        backpack.findElement(By.className("btn_inventory")).click();

        var tshirt = getProductByTitle(shirtTitle);
        tshirt.findElement(By.className("btn_inventory")).click();

        driver.findElement(By.className("shopping_cart_link")).click();

        // Assert Items and Totals
        driver.findElement(By.id("checkout")).click();

        // fill form
        fillShippingDetails("Fname", "lname", "zip");

        driver.findElement(By.id("continue")).click();

        var items = driver.findElements(By.className("inventory_item_name"));
        Assertions.assertEquals(2, items.size(), "Items count not as expected");

        var total = driver.findElement(By.className("summary_total_label")).getText();
        double expectedPrice = 29.99 + 15.99 + 3.68;

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("###,##0.00", symbols);

        var expectedTotal = "Total: $" + decimalFormat.format(expectedPrice);

        Assertions.assertEquals(2, items.size(), "Items count not as expected");
        Assertions.assertEquals(backpackTitle, items.get(0).getText(), "Item title not as expected");
        Assertions.assertEquals(shirtTitle, items.get(1).getText(), "Item title not as expected");
        Assertions.assertEquals(expectedTotal, total, "Items total price not as expected");
    }

    @Test
//    @Order(3)
    public void orderCompleted_when_addProduct_and_checkout_withConfirm(){
        // Add Backpack and T-shirt to shopping cart
        String backpackTitle = "Sauce Labs Backpack";
        String shirtTitle = "Sauce Labs Bolt T-Shirt";

        var backpack = getProductByTitle(backpackTitle);
        backpack.findElement(By.className("btn_inventory")).click();

        var tshirt = getProductByTitle(shirtTitle);
        tshirt.findElement(By.className("btn_inventory")).click();

        // Click on shopping Cart
        driver.findElement(By.className("shopping_cart_link")).click();

        // Fill Contact Details
        driver.findElement(By.id("checkout")).click();

        fillShippingDetails("FirstName", "LastName", "zip Code");
        driver.findElement(By.id("continue")).click();

        // Complete Order
        driver.findElement(By.id("finish")).click();

        // Assert Complete Message
        WebElement checkoutCompleteText = driver.findElement(By.xpath("//span[@class='title']"));
        Assertions.assertEquals("Checkout: Complete!", checkoutCompleteText.getText(),
                "Checkout is not successful");

        // Assert Items removed from Shopping Cart
        driver.findElement(By.className("shopping_cart_link")).click();

        List<WebElement> cartBadge = driver.findElements(By.className("shopping_cart_badge"));
        if (cartBadge.isEmpty()) {
            Assertions.assertTrue(true, "Shopping cart is empty");
        } else {
            Assertions.fail("Shopping cart is not empty");
        }
    }
}