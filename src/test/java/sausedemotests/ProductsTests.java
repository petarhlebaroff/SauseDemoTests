package sausedemotests;

import core.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;


public class ProductsTests extends BaseTest {


    @Test
    public void productAddedToShoppingCart_when_addToCart(){
        addProductsToShoppingCart(backpackTitle, shirtTitle);

        driver.findElement(By.className("shopping_cart_link")).click();

        // Assert Items and Totals
        var items = driver.findElements(By.className("inventory_item_name"));

        Assertions.assertEquals(2, items.size(), "Items count not as expected");

        Assertions.assertEquals(backpackTitle, items.get(0).getText(), "Item title not as expected");
        Assertions.assertEquals(shirtTitle, items.get(1).getText(), "Item title not as expected");
    }

    @Test
    public void userDetailsAdded_when_checkoutWithValidInformation(){
        addProductsToShoppingCart(backpackTitle, shirtTitle);

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

        // Fix a problem with the comma separator in order to compare price correctly
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
    public void orderCompleted_when_addProduct_and_checkout_withConfirm(){
        // Add Backpack and T-shirt to shopping cart
        addProductsToShoppingCart(backpackTitle, shirtTitle);

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