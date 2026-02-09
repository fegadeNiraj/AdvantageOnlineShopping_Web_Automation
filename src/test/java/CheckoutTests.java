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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

public class CheckoutTests extends BaseTest
{

    @Test(retryAnalyzer = RetryAnalyzer.class, priority = 1)
    public void TC_CHECKOUT_01_validateProductCheckout()
            throws IOException, ParseException {

        HomePage homePage = new HomePage(driver);
        LoginPage loginPage = new LoginPage(driver);
        ProductPage productPage = new ProductPage(driver);
        CheckoutPage checkoutPage = new CheckoutPage(driver);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // ---------- Launch & Login ----------
        homePage.launchHomePage();
        js.executeScript("arguments[0].click();", homePage.getUserIcon());
        WaitUtils.waitForElementToBeInvisible(driver);

        Map<String, String> userLoginDetails = JsonReader.getJsonMap("existingUser");
        loginPage.login(
                userLoginDetails.get("userName"),
                userLoginDetails.get("password")
        );

        wait.until(d -> !loginPage.getLoggedInUserName().isEmpty());

        // ---------- Navigate & Select Product ----------
        wait.until(ExpectedConditions.visibilityOf(productPage.laptops));
        js.executeScript("arguments[0].scrollIntoView(true);", productPage.laptops);
        js.executeScript("arguments[0].click();", productPage.laptops);
        WaitUtils.waitForElementToBeInvisible(driver);

        wait.until(ExpectedConditions.visibilityOf(productPage.laptopToBeSelected));
        js.executeScript("arguments[0].scrollIntoView(true);", productPage.laptopToBeSelected);
        js.executeScript("arguments[0].click();", productPage.laptopToBeSelected);

        String selectedProduct =
                productPage.laptopToBeSelected.getText().trim().toUpperCase();

        wait.until(ExpectedConditions.visibilityOf(productPage.laptopToBeSelectedDescription));
        js.executeScript("arguments[0].click();", productPage.selectGreyColor);

        // ---------- Cart Count Validation ----------
        String cartCountBeforeStr = (String) js.executeScript(
                "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
        );
        int cartCountBefore = cartCountBeforeStr.isEmpty()
                ? 0
                : Integer.parseInt(cartCountBeforeStr);

        js.executeScript("arguments[0].click();", productPage.addToCartButton);

        wait.until(driver -> {
            String updatedStr = (String) js.executeScript(
                    "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
            );
            return updatedStr != null
                    && !updatedStr.isEmpty()
                    && Integer.parseInt(updatedStr) == cartCountBefore + 1;
        });

        // ---------- Checkout ----------
        js.executeScript("arguments[0].click();", checkoutPage.cartIcon);
        wait.until(ExpectedConditions.elementToBeClickable(checkoutPage.checkoutButton));
        js.executeScript("arguments[0].click();", checkoutPage.checkoutButton);

        wait.until(ExpectedConditions.elementToBeClickable(checkoutPage.nextButton));
        js.executeScript("arguments[0].click();", checkoutPage.nextButton);

        // ---------- Payment ----------
        Map<String, String> safepayDetails = JsonReader.getJsonMap("safepayDetails");

        checkoutPage.inputSafepayUsername.clear();
        checkoutPage.inputSafepayUsername.sendKeys(safepayDetails.get("username"));

        checkoutPage.inputSafepayPassword.clear();
        checkoutPage.inputSafepayPassword.sendKeys(safepayDetails.get("password"));

        js.executeScript("arguments[0].click();", checkoutPage.payNowButton);

        // ---------- Order Validation ----------
        wait.until(ExpectedConditions.visibilityOf(checkoutPage.orderSuccessMessage));
        wait.until(ExpectedConditions.visibilityOf(checkoutPage.orderTrackingDetails));
        wait.until(ExpectedConditions.visibilityOf(checkoutPage.orderNumberDetails));

        softAssert.assertTrue(
                checkoutPage.orderSuccessMessage.isDisplayed(),
                "Order success message is not displayed"
        );
        softAssert.assertFalse(
                checkoutPage.orderTrackingDetails.getText().isEmpty(),
                "Order tracking details are missing"
        );
        softAssert.assertFalse(
                checkoutPage.orderNumberDetails.getText().isEmpty(),
                "Order number details are missing"
        );

        System.out.println("TC_CHECKOUT_01_validateProductCheckout passed successfully");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, priority = 2)
    public void TC_CHECKOUT_02_validateProductCheckoutWithoutLogin()
            throws IOException {

        HomePage homePage = new HomePage(driver);
        ProductPage productPage = new ProductPage(driver);
        CheckoutPage checkoutPage = new CheckoutPage(driver);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // ---------- Launch ----------
        homePage.launchHomePage();
        WaitUtils.waitForElementToBeInvisible(driver);

        // ---------- Navigate & Select Product ----------
        wait.until(ExpectedConditions.visibilityOf(productPage.laptops));
        js.executeScript("arguments[0].scrollIntoView(true);", productPage.laptops);
        js.executeScript("arguments[0].click();", productPage.laptops);
        WaitUtils.waitForElementToBeInvisible(driver);

        wait.until(ExpectedConditions.visibilityOf(productPage.laptopToBeSelected));
        js.executeScript("arguments[0].scrollIntoView(true);", productPage.laptopToBeSelected);
        js.executeScript("arguments[0].click();", productPage.laptopToBeSelected);

        String selectedProduct =
                productPage.laptopToBeSelected.getText().trim().toUpperCase();

        wait.until(ExpectedConditions.visibilityOf(productPage.laptopToBeSelectedDescription));
        js.executeScript("arguments[0].click();", productPage.selectGreyColor);

        // ---------- Cart Count ----------
        String cartCountBeforeStr = (String) js.executeScript(
                "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
        );
        softAssert.assertNotNull(cartCountBeforeStr, "Initial cart count is null");

        int cartCountBefore = cartCountBeforeStr.isEmpty()
                ? 0
                : Integer.parseInt(cartCountBeforeStr);

        js.executeScript("arguments[0].click();", productPage.addToCartButton);

        wait.until(driver -> {
            String updatedStr = (String) js.executeScript(
                    "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
            );
            return updatedStr != null
                    && !updatedStr.isEmpty()
                    && Integer.parseInt(updatedStr) == cartCountBefore + 1;
        });

        // ---------- Attempt Checkout ----------
        js.executeScript("arguments[0].click();", checkoutPage.cartIcon);
        wait.until(ExpectedConditions.elementToBeClickable(checkoutPage.checkoutButton));
        js.executeScript("arguments[0].click();", checkoutPage.checkoutButton);

        // ---------- Validation ----------
        wait.until(ExpectedConditions.visibilityOf(checkoutPage.loginAlertOnCheckout));
        wait.until(ExpectedConditions.visibilityOf(checkoutPage.registerAlertOnCheckout));

        softAssert.assertTrue(
                checkoutPage.loginAlertOnCheckoutMessage.isDisplayed(),
                "Login alert not displayed"
        );
        softAssert.assertTrue(
                checkoutPage.registerAlertOnCheckoutMessage.isDisplayed(),
                "Register alert not displayed"
        );

        System.out.println("TC_CHECKOUT_02_validateProductCheckoutWithoutLogin passed successfully");
    }

}
