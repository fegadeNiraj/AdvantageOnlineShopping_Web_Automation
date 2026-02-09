import Core.BaseTest;
import Pages.CheckoutPage;
import Pages.HomePage;
import Pages.LoginPage;
import Pages.ProductPage;
import Util.JsonReader;
import Util.RetryAnalyzer;
import Util.WaitUtils;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

public class CheckoutTests extends BaseTest
{

    @Test(retryAnalyzer = RetryAnalyzer.class,priority = 1)
    public void TC_CHECKOUT_01_validateProductCheckout() throws IOException, ParseException {
        HomePage homePage = new HomePage(driver);
        LoginPage loginPage = new LoginPage(driver);
        ProductPage productPage = new ProductPage(driver);
        CheckoutPage checkoutPage = new CheckoutPage(driver);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Step 1: Launch home page and open login form
        homePage.launchHomePage();
        js.executeScript("arguments[0].scrollIntoView(true);", homePage.getUserIcon());
        js.executeScript("arguments[0].click();", homePage.getUserIcon());
        WaitUtils.waitForElementToBeInvisible(driver);

        // Step 2: Login as existing user
        Map<String, String> userLoginDetails = JsonReader.getJsonMap("existingUser");
        loginPage.inputLoginFormUserName.sendKeys(userLoginDetails.get("userName"));
        loginPage.inputLoginFormPassword.sendKeys(userLoginDetails.get("password"));
        js.executeScript("arguments[0].scrollIntoView(true);", loginPage.loginFormSignInButton);
        js.executeScript("arguments[0].click();", loginPage.loginFormSignInButton);

        wait.until(ExpectedConditions.visibilityOf(loginPage.loggedInUserName));

        // Step 3: Navigate to Laptops category and select a product
        wait.until(ExpectedConditions.visibilityOf(productPage.laptops));
        js.executeScript("arguments[0].scrollIntoView(true);", productPage.laptops);
        js.executeScript("arguments[0].click();", productPage.laptops);

        WaitUtils.waitForElementToBeInvisible(driver);
        wait.until(ExpectedConditions.visibilityOf(productPage.laptopToBeSelected));
        js.executeScript("arguments[0].scrollIntoView(true);", productPage.laptopToBeSelected);
        js.executeScript("arguments[0].click();", productPage.laptopToBeSelected);

        // Step 4: Customize product (e.g., color selection)
        wait.until(ExpectedConditions.visibilityOf(productPage.laptopToBeSelectedDescription));
        wait.until(ExpectedConditions.elementToBeClickable(productPage.selectGreyColor));
        js.executeScript("arguments[0].scrollIntoView(true);", productPage.selectGreyColor);
        js.executeScript("arguments[0].click();", productPage.selectGreyColor);

        // Step 5: Validate initial cart count
        String cartCountStr = (String) js.executeScript(
                "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
        );
        softAssert.assertNotNull(cartCountStr, "Initial cart count is null before adding product");
        int cartCountBefore = cartCountStr.isEmpty() ? 0 : Integer.parseInt(cartCountStr);

        // Step 6: Add product to cart
        wait.until(ExpectedConditions.elementToBeClickable(productPage.addToCartButton));
        js.executeScript("arguments[0].scrollIntoView(true);", productPage.addToCartButton);
        js.executeScript("arguments[0].click();", productPage.addToCartButton);

        // Step 7: Wait for cart count to update
        wait.until(driver -> {
            String updatedStr = (String) js.executeScript(
                    "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
            );
            if (updatedStr == null || updatedStr.isEmpty()) return false;
            int updatedCount = Integer.parseInt(updatedStr);
            return updatedCount == cartCountBefore + 1;
        });

        // Step 8: Validate cart count after addition
        String cartCountAfterStr = (String) js.executeScript(
                "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
        );
        softAssert.assertNotNull(cartCountAfterStr, "Cart count is null after adding product");
        int cartCountAfter = Integer.parseInt(cartCountAfterStr);
        softAssert.assertEquals(cartCountAfter, cartCountBefore + 1, "Cart count did not increase by 1 after product addition");

        // Step 9: Proceed to checkout
        wait.until(ExpectedConditions.elementToBeClickable(checkoutPage.cartIcon));
        js.executeScript("arguments[0].scrollIntoView(true);", checkoutPage.cartIcon);
        js.executeScript("arguments[0].click();", checkoutPage.cartIcon);

        wait.until(ExpectedConditions.visibilityOf(checkoutPage.checkoutButton));
        wait.until(ExpectedConditions.elementToBeClickable(checkoutPage.checkoutButton));
        js.executeScript("arguments[0].scrollIntoView(true);", checkoutPage.checkoutButton);
        js.executeScript("arguments[0].click();", checkoutPage.checkoutButton);

        // Step 10: Enter payment details and place order
        wait.until(ExpectedConditions.visibilityOf(checkoutPage.nextButton));
        wait.until(ExpectedConditions.elementToBeClickable(checkoutPage.nextButton));
        js.executeScript("arguments[0].scrollIntoView(true);", checkoutPage.nextButton);
        js.executeScript("arguments[0].click();", checkoutPage.nextButton);

        Map<String, String> safepayDetails = JsonReader.getJsonMap("safepayDetails");

        wait.until(ExpectedConditions.visibilityOf(checkoutPage.inputSafepayUsername)).clear();
        checkoutPage.inputSafepayUsername.sendKeys(safepayDetails.get("username"));

        wait.until(ExpectedConditions.visibilityOf(checkoutPage.inputSafepayPassword)).clear();
        checkoutPage.inputSafepayPassword.sendKeys(safepayDetails.get("password"));

        wait.until(ExpectedConditions.visibilityOf(checkoutPage.payNowButton));
        wait.until(ExpectedConditions.elementToBeClickable(checkoutPage.payNowButton));
        js.executeScript("arguments[0].scrollIntoView(true);", checkoutPage.payNowButton);
        js.executeScript("arguments[0].click();", checkoutPage.payNowButton);

        // Step 11: Validate order confirmation
        wait.until(ExpectedConditions.visibilityOf(checkoutPage.orderSuccessMessage));
        wait.until(ExpectedConditions.visibilityOf(checkoutPage.orderTrackingDetails));
        wait.until(ExpectedConditions.visibilityOf(checkoutPage.orderNumberDetails));

        softAssert.assertTrue(checkoutPage.orderSuccessMessage.isDisplayed(), "Order success message is not displayed");
        softAssert.assertNotNull(checkoutPage.orderTrackingDetails.getText(), "Order tracking details are missing");
        softAssert.assertNotNull(checkoutPage.orderNumberDetails.getText(), "Order number details are missing");

        System.out.println("TC_CHECKOUT_01_validateProductCheckout passed successfully");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class,priority = 2)
    public void TC_CHECKOUT_02_validateProductCheckoutWithoutLogin() throws IOException {
        HomePage homePage = new HomePage(driver);
        ProductPage productPage = new ProductPage(driver);
        CheckoutPage checkoutPage = new CheckoutPage(driver);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        homePage.launchHomePage();

        // Step 3: Navigate to Laptops category and select a product
        wait.until(ExpectedConditions.visibilityOf(productPage.laptops));
        js.executeScript("arguments[0].scrollIntoView(true);", productPage.laptops);
        js.executeScript("arguments[0].click();", productPage.laptops);

        WaitUtils.waitForElementToBeInvisible(driver);
        wait.until(ExpectedConditions.visibilityOf(productPage.laptopToBeSelected));
        js.executeScript("arguments[0].scrollIntoView(true);", productPage.laptopToBeSelected);
        js.executeScript("arguments[0].click();", productPage.laptopToBeSelected);

        wait.until(ExpectedConditions.visibilityOf(productPage.laptopToBeSelectedDescription));

        js.executeScript("arguments[0].scrollIntoView(true);", productPage.selectGreyColor);
        js.executeScript("arguments[0].click();", productPage.selectGreyColor);

        // Get initial cart count
        String cartCountStr = (String) js.executeScript(
                "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
        );
        softAssert.assertNotNull(cartCountStr, "Initial cart count is null");

        int cartCountBefore = cartCountStr.isEmpty() ? 0 : Integer.parseInt(cartCountStr);

        js.executeScript("arguments[0].scrollIntoView(true);", productPage.addToCartButton);
        js.executeScript("arguments[0].click();", productPage.addToCartButton);

        // Wait until cart count increases by 1
        wait.until(driver -> {
            String updatedStr = (String) js.executeScript(
                    "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
            );
            if (updatedStr == null || updatedStr.isEmpty()) return false;
            int updatedCount = Integer.parseInt(updatedStr);
            return updatedCount == cartCountBefore + 1;
        });

        js.executeScript("arguments[0].scrollIntoView(true);", checkoutPage.cartIcon);
        js.executeScript("arguments[0].click();", checkoutPage.cartIcon);

        wait.until(ExpectedConditions.visibilityOf(checkoutPage.checkoutButton));
        js.executeScript("arguments[0].scrollIntoView(true);", checkoutPage.checkoutButton);
        js.executeScript("arguments[0].click();", checkoutPage.checkoutButton);

        wait.until(ExpectedConditions.visibilityOf(checkoutPage.loginAlertOnCheckout));
        wait.until(ExpectedConditions.visibilityOf(checkoutPage.registerAlertOnCheckout));

        softAssert.assertTrue(checkoutPage.loginAlertOnCheckoutMessage.isDisplayed());
        softAssert.assertTrue(checkoutPage.registerAlertOnCheckoutMessage.isDisplayed());

        System.out.println("TC_CHECKOUT_02_validateProductCheckoutWithoutLogin passed successfully");
    }


}
