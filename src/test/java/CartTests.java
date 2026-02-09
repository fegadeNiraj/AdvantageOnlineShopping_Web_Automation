import Constant.Constant;
import Core.BaseTest;
import Pages.CheckoutPage;
import Pages.HomePage;
import Pages.LoginPage;
import Pages.ProductPage;
import Util.JsonReader;
import Util.RetryAnalyzer;
import Util.WaitUtils;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class CartTests extends BaseTest {

    @Test(retryAnalyzer = RetryAnalyzer.class, priority = 1)
    public void TC_CART_01_validateAddProductToCart()
            throws IOException, ParseException, InterruptedException {

        HomePage homePage = new HomePage(driver);
        LoginPage loginPage = new LoginPage(driver);
        ProductPage productPage = new ProductPage(driver);

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

        // ---------- Navigate to Product ----------
        js.executeScript("arguments[0].click();", productPage.laptops);
        WaitUtils.waitForElementToBeInvisible(driver);
        wait.until(ExpectedConditions.visibilityOfAllElements(productPage.laptopsList));

        js.executeScript("arguments[0].click();", productPage.laptopToBeSelected);
        wait.until(ExpectedConditions.visibilityOf(productPage.laptopToBeSelectedDescription));

        js.executeScript("arguments[0].click();", productPage.selectGreyColor);

        // ---------- Capture cart count before ----------
        String cartCountBeforeStr = (String) js.executeScript(
                "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
        );
        int cartCountBefore = cartCountBeforeStr.isEmpty()
                ? 0
                : Integer.parseInt(cartCountBeforeStr);

        // ---------- Add to cart ----------
        js.executeScript("arguments[0].click();", productPage.addToCartButton);

        wait.until(driver -> {
            String updated = (String) js.executeScript(
                    "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
            );
            if (updated == null || updated.isEmpty()) return false;
            return Integer.parseInt(updated) == cartCountBefore + 1;
        });

        // ---------- Final assertion ----------
        String cartCountAfterStr = (String) js.executeScript(
                "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
        );
        int cartCountAfter = Integer.parseInt(cartCountAfterStr);

        softAssert.assertEquals(
                cartCountAfter,
                cartCountBefore + 1,
                "Cart count should increase by 1"
        );

        System.out.println("TC_CART_01_validateAddProductToCart passed successfully");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, priority = 2)
    public void TC_CART_02_validateRemoveProductFromCart()
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

        // ---------- Navigate & Add Product ----------
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

        String cartCountBeforeStr = (String) js.executeScript(
                "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
        );
        int cartCountBefore = cartCountBeforeStr.isEmpty()
                ? 0
                : Integer.parseInt(cartCountBeforeStr);

        js.executeScript("arguments[0].click();", productPage.addToCartButton);

        wait.until(driver -> {
            String updated = (String) js.executeScript(
                    "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
            );
            return updated != null
                    && !updated.isEmpty()
                    && Integer.parseInt(updated) == cartCountBefore + 1;
        });

        // ---------- Open Cart ----------
        js.executeScript("arguments[0].click();", checkoutPage.cartIcon);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#shoppingCart")));

        // ---------- Remove Product ----------
        List<WebElement> cartRows =
                driver.findElements(By.cssSelector("#shoppingCart tbody tr"));

        for (WebElement row : cartRows) {
            String productName =
                    row.findElement(By.cssSelector("label.productName"))
                            .getText()
                            .trim();

            if (productName.equals(selectedProduct)) {
                WebElement removeButton = row.findElement(By.cssSelector("a.remove"));
                js.executeScript("arguments[0].click();", removeButton);
                break;
            }
        }

        // ---------- Final Assertion ----------
        wait.until(ExpectedConditions.textToBePresentInElement(
                checkoutPage.emptyCartMessage,
                Constant.EMPTY_CART_MESSAGE
        ));

        softAssert.assertEquals(
                checkoutPage.emptyCartMessage.getText().trim(),
                Constant.EMPTY_CART_MESSAGE,
                "Expected the cart to be empty"
        );

        System.out.println("TC_CART_02_validateRemoveProductFromCart passed successfully");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, priority = 3)
    public void TC_CART_03_validateUpdateProductFromCart() throws IOException, ParseException {
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

        // ---------- Navigate & Add Product ----------
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

        String cartCountBeforeStr = (String) js.executeScript(
                "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
        );
        int cartCountBefore = cartCountBeforeStr.isEmpty()
                ? 0
                : Integer.parseInt(cartCountBeforeStr);

        js.executeScript("arguments[0].click();", productPage.addToCartButton);

        wait.until(driver -> {
            String updated = (String) js.executeScript(
                    "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
            );
            return updated != null
                    && !updated.isEmpty()
                    && Integer.parseInt(updated) == cartCountBefore + 1;
        });

        // ---------- Open Cart ----------
        js.executeScript("arguments[0].click();", checkoutPage.cartIcon);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#shoppingCart")));

        List<WebElement> cartRows =
                driver.findElements(By.cssSelector("#shoppingCart tbody tr"));

        int initialQuantity = 0;

        for (WebElement row : cartRows) {
            String productName =
                    row.findElement(By.cssSelector("label.productName"))
                            .getText()
                            .trim();

            if (productName.equals(selectedProduct)) {
                WebElement quantityLabel =
                        row.findElement(By.cssSelector("td.smollCell.quantityMobile label.ng-binding"));
                initialQuantity = Integer.parseInt(quantityLabel.getText().trim());

                WebElement editButton = row.findElement(By.cssSelector("a.edit"));
                js.executeScript("arguments[0].click();", editButton);
                break;
            }
        }

        // ---------- Update Quantity ----------
        wait.until(ExpectedConditions.visibilityOf(productPage.increaseQuantity));
        js.executeScript("arguments[0].click();", productPage.increaseQuantity);

        js.executeScript("arguments[0].click();", productPage.addToCartButton);
        WaitUtils.waitForElementToBeInvisible(driver);

        // ---------- Verify Updated Quantity ----------
        js.executeScript("arguments[0].click();", checkoutPage.cartIcon);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#shoppingCart")));

        List<WebElement> cartRowsAfterUpdate =
                driver.findElements(By.cssSelector("#shoppingCart tbody tr"));

        for (WebElement row : cartRowsAfterUpdate) {
            WebElement quantityLabel =
                    row.findElement(By.cssSelector("td.smollCell.quantityMobile label.ng-binding"));
            int updatedQuantity = Integer.parseInt(quantityLabel.getText().trim());

            softAssert.assertEquals(
                    updatedQuantity,
                    initialQuantity + 1,
                    "Failed to update the product quantity"
            );
        }

        System.out.println("TC_CART_03_validateUpdateProductFromCart passed successfully");
    }
}
