package sausedemotests;

import core.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class LoginTests extends BaseTest {


    @Test
    public void userAuthenticated_when_validCredentialsProvided(){

        // Add Assert
        WebElement inventoryPageTitle = driver.findElement(By.xpath("//div[@class='app_logo']"));
        wait.until(ExpectedConditions.visibilityOf(inventoryPageTitle));

        Assertions.assertEquals("Swag Labs", inventoryPageTitle.getText(), "The login is not successful");

//        refreshSauceDemoWebsite();
    }

    @Test
    public void productAddedToShoppingCar_when_addToCart(){
        // Add Backpack and T-shirt to shopping cart
        addProductsToShoppingCart(backpackTitle, shirtTitle);

        driver.findElement(By.className("shopping_cart_link")).click();

        // Click on shopping Cart
        driver.findElement(By.className("shopping_cart_link")).click();

        // Assert Items and Totals
        var items = driver.findElements(By.className("inventory_item_name"));

        Assertions.assertEquals(2, items.size(), "Items count not as expected");

        Assertions.assertEquals(backpackTitle, items.get(0).getText(), "Item title not as expected");
        Assertions.assertEquals(shirtTitle, items.get(1).getText(), "Item title not as expected");

//        refreshSauceDemoWebsite();
    }

    @Test
    public void userDetailsAdded_when_checkoutWithValidInformation(){
        // Add Backpack and T-shirt to shopping cart
        addProductsToShoppingCart(backpackTitle, shirtTitle);

        // Click on shopping Cart
        driver.findElement(By.className("shopping_cart_link")).click();

        // Fill Contact Details
        driver.findElement(By.id("checkout")).click();

        fillShippingDetails("FirstName", "LastName", "zip Code");
        driver.findElement(By.id("continue")).click();

        // Assert Details in next step
        WebElement checkoutOverview = driver.findElement(By.xpath("//span[@class='title']"));
        Assertions.assertEquals("Checkout: Overview", checkoutOverview.getText(),
                "Checkout is not successful");

        var items = driver.findElements(By.className("inventory_item_name"));
        var total = driver.findElement(By.className("summary_total_label")).getText();
        double expectedPrice = 15.99 + 29.99 + 3.68;

        // Fix a problem with the decimal separator in order to compare price correctly
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("###,##0.00", symbols);

        var expectedTotal = "Total: $" + decimalFormat.format(expectedPrice);

        Assertions.assertEquals(2, items.size(), "Items count not as expected");

        Assertions.assertEquals(backpackTitle, items.get(0).getText(), "Item title not as expected");
        Assertions.assertEquals(shirtTitle, items.get(1).getText(), "Item title not as expected");
        Assertions.assertEquals(expectedTotal, total, "Items total price not as expected");

//        refreshSauceDemoWebsite();
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
