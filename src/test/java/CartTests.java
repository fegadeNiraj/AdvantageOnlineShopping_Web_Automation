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
    public void TC_CART_01_validateAddProductToCart() throws IOException, ParseException, InterruptedException {
        HomePage homePage = new HomePage(driver);
        LoginPage loginPage = new LoginPage(driver);
        ProductPage productPage = new ProductPage(driver);
        CheckoutPage checkoutPage = new CheckoutPage(driver);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        homePage.launchHomePage();

        js.executeScript("arguments[0].scrollIntoView(true);", homePage.getUserIcon());
        js.executeScript("arguments[0].click();", homePage.getUserIcon());
        WaitUtils.waitForElementToBeInvisible(driver);

        Map<String, String> userLoginDetails = JsonReader.getJsonMap("existingUser");

        js.executeScript("arguments[0].scrollIntoView(true);", loginPage.inputLoginFormUserName);
        loginPage.inputLoginFormUserName.sendKeys(userLoginDetails.get("userName"));

        js.executeScript("arguments[0].scrollIntoView(true);", loginPage.inputLoginFormPassword);
        loginPage.inputLoginFormPassword.sendKeys(userLoginDetails.get("password"));

        js.executeScript("arguments[0].scrollIntoView(true);", loginPage.loginFormSignInButton);
        js.executeScript("arguments[0].click();", loginPage.loginFormSignInButton);

        wait.until(ExpectedConditions.visibilityOf(loginPage.loggedInUserName));

        js.executeScript("arguments[0].scrollIntoView(true);", productPage.laptops);
        js.executeScript("arguments[0].click();", productPage.laptops);

        WaitUtils.waitForElementToBeInvisible(driver);
        wait.until(ExpectedConditions.visibilityOfAllElements(productPage.laptopsList));

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

        // Final verification
        String cartCountAfterStr = (String) js.executeScript(
                "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
        );
        softAssert.assertNotNull(cartCountAfterStr, "Updated cart count is null");
        int cartCountAfter = Integer.parseInt(cartCountAfterStr);
        softAssert.assertEquals(cartCountAfter, cartCountBefore + 1, "Cart count should increase by 1");

        System.out.println("TC_CART_01_validateAddProductToCart passed successfully");
    }


    @Test(retryAnalyzer = RetryAnalyzer.class, priority = 2)
    public void TC_CART_02_validateRemoveProductFromCart() throws IOException, ParseException, InterruptedException {
        HomePage homePage = new HomePage(driver);
        LoginPage loginPage = new LoginPage(driver);
        ProductPage productPage = new ProductPage(driver);
        CheckoutPage checkoutPage = new CheckoutPage(driver);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Launch home page and login
        homePage.launchHomePage();

        js.executeScript("arguments[0].scrollIntoView(true);", homePage.getUserIcon());
        js.executeScript("arguments[0].click();", homePage.getUserIcon());
        WaitUtils.waitForElementToBeInvisible(driver);

        Map<String, String> userLoginDetails = JsonReader.getJsonMap("existingUser");
        loginPage.inputLoginFormUserName.sendKeys(userLoginDetails.get("userName"));
        loginPage.inputLoginFormPassword.sendKeys(userLoginDetails.get("password"));

        js.executeScript("arguments[0].scrollIntoView(true);", loginPage.loginFormSignInButton);
        js.executeScript("arguments[0].click();", loginPage.loginFormSignInButton);

        wait.until(ExpectedConditions.visibilityOf(loginPage.loggedInUserName));

        // Navigate to laptops
        wait.until(ExpectedConditions.visibilityOf(productPage.laptops));
        js.executeScript("arguments[0].scrollIntoView(true);", productPage.laptops);
        js.executeScript("arguments[0].click();", productPage.laptops);

        WaitUtils.waitForElementToBeInvisible(driver);
        wait.until(ExpectedConditions.visibilityOf(productPage.laptopToBeSelected));
        js.executeScript("arguments[0].scrollIntoView(true);", productPage.laptopToBeSelected);
        js.executeScript("arguments[0].click();", productPage.laptopToBeSelected);

        String selectedProduct = productPage.laptopToBeSelected.getText().trim().toUpperCase();

        wait.until(ExpectedConditions.visibilityOf(productPage.laptopToBeSelectedDescription));
        wait.until(ExpectedConditions.elementToBeClickable(productPage.selectGreyColor));
        js.executeScript("arguments[0].scrollIntoView(true);", productPage.selectGreyColor);
        js.executeScript("arguments[0].click();", productPage.selectGreyColor);

        // Get initial cart count
        String cartCountStr = (String) js.executeScript(
                "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
        );

        softAssert.assertNotNull(cartCountStr, "Initial cart count is null");
        int cartCountBefore = cartCountStr.isEmpty() ? 0 : Integer.parseInt(cartCountStr);

        wait.until(ExpectedConditions.elementToBeClickable(productPage.addToCartButton));
        js.executeScript("arguments[0].scrollIntoView(true);", productPage.addToCartButton);
        js.executeScript("arguments[0].click();", productPage.addToCartButton);

        // Wait for cart count to increase by 1
        wait.until(driver -> {
            String updatedStr = (String) js.executeScript(
                    "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
            );
            if (updatedStr == null || updatedStr.isEmpty()) return false;
            int updatedCount = Integer.parseInt(updatedStr);
            return updatedCount == cartCountBefore + 1;
        });

        wait.until(ExpectedConditions.elementToBeClickable(checkoutPage.cartIcon));
        js.executeScript("arguments[0].scrollIntoView(true);", checkoutPage.cartIcon);
        js.executeScript("arguments[0].click();", checkoutPage.cartIcon);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#shoppingCart")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#shoppingCart tbody tr")));

        List<WebElement> cartRows = driver.findElements(By.cssSelector("#shoppingCart tbody tr"));

        for (WebElement row : cartRows) {
            WebElement productNameElement = row.findElement(By.cssSelector("label.productName"));
            String productName = productNameElement.getText().trim();

            if (productName.equals(selectedProduct)) {
                WebElement removeButton = row.findElement(By.cssSelector("a.remove"));
                js.executeScript("arguments[0].scrollIntoView(true);", removeButton);
                js.executeScript("arguments[0].click();", removeButton);
                break;
            }
        }

        wait.until(ExpectedConditions.visibilityOf(checkoutPage.emptyCartMessage));
        wait.until(ExpectedConditions.textToBePresentInElement(
                checkoutPage.emptyCartMessage, Constant.EMPTY_CART_MESSAGE));

        softAssert.assertTrue(
                checkoutPage.emptyCartMessage.getText().trim().equals(Constant.EMPTY_CART_MESSAGE),
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

        homePage.launchHomePage();

        js.executeScript("arguments[0].scrollIntoView(true);", homePage.getUserIcon());
        js.executeScript("arguments[0].click();", homePage.getUserIcon());
        WaitUtils.waitForElementToBeInvisible(driver);

        Map<String, String> userLoginDetails = JsonReader.getJsonMap("existingUser");
        loginPage.inputLoginFormUserName.sendKeys(userLoginDetails.get("userName"));
        loginPage.inputLoginFormPassword.sendKeys(userLoginDetails.get("password"));

        js.executeScript("arguments[0].scrollIntoView(true);", loginPage.loginFormSignInButton);
        js.executeScript("arguments[0].click();", loginPage.loginFormSignInButton);

        wait.until(ExpectedConditions.visibilityOf(loginPage.loggedInUserName));

        wait.until(ExpectedConditions.visibilityOf(productPage.laptops));
        js.executeScript("arguments[0].scrollIntoView(true);", productPage.laptops);
        js.executeScript("arguments[0].click();", productPage.laptops);

        WaitUtils.waitForElementToBeInvisible(driver);

        wait.until(ExpectedConditions.visibilityOf(productPage.laptopToBeSelected));
        js.executeScript("arguments[0].scrollIntoView(true);", productPage.laptopToBeSelected);
        js.executeScript("arguments[0].click();", productPage.laptopToBeSelected);

        String selectedProduct = productPage.laptopToBeSelected.getText().trim().toUpperCase();

        wait.until(ExpectedConditions.visibilityOf(productPage.laptopToBeSelectedDescription));
        wait.until(ExpectedConditions.elementToBeClickable(productPage.selectGreyColor));
        js.executeScript("arguments[0].scrollIntoView(true);", productPage.selectGreyColor);
        js.executeScript("arguments[0].click();", productPage.selectGreyColor);

        // Get initial cart count
        String cartCountStr = (String) js.executeScript(
                "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
        );

        softAssert.assertNotNull(cartCountStr, "Initial cart count is null");
        int cartCountBefore = cartCountStr.isEmpty() ? 0 : Integer.parseInt(cartCountStr);

        wait.until(ExpectedConditions.elementToBeClickable(productPage.addToCartButton));
        js.executeScript("arguments[0].scrollIntoView(true);", productPage.addToCartButton);
        js.executeScript("arguments[0].click();", productPage.addToCartButton);

        // Wait for cart count to increase by 1
        wait.until(driver -> {
            String updatedStr = (String) js.executeScript(
                    "return document.querySelector('#shoppingCartLink .cart').textContent.trim();"
            );
            if (updatedStr == null || updatedStr.isEmpty()) return false;
            int updatedCount = Integer.parseInt(updatedStr);
            return updatedCount == cartCountBefore + 1;
        });

        wait.until(ExpectedConditions.elementToBeClickable(checkoutPage.cartIcon));
        js.executeScript("arguments[0].scrollIntoView(true);", checkoutPage.cartIcon);
        js.executeScript("arguments[0].click();", checkoutPage.cartIcon);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#shoppingCart")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#shoppingCart tbody tr")));

        List<WebElement> cartRows = driver.findElements(By.cssSelector("#shoppingCart tbody tr"));
        int initialQuantity = 0;

        for (WebElement row : cartRows) {
            WebElement productNameElement = row.findElement(By.cssSelector("label.productName"));
            WebElement productQuantityElement = row.findElement(By.cssSelector("td.smollCell.quantityMobile label.ng-binding"));

            String productQuantityString = productQuantityElement.getText().trim();
            initialQuantity = Integer.parseInt(productQuantityString);

            String productName = productNameElement.getText().trim();
            if (productName.equals(selectedProduct)) {
                WebElement editButton = row.findElement(By.cssSelector("a.edit"));
                js.executeScript("arguments[0].scrollIntoView(true);", editButton);
                js.executeScript("arguments[0].click();", editButton);
                break;
            }
        }

        wait.until(ExpectedConditions.visibilityOf(productPage.increaseQuantity));
        js.executeScript("arguments[0].scrollIntoView(true);", productPage.increaseQuantity);
        js.executeScript("arguments[0].click();", productPage.increaseQuantity);

        wait.until(ExpectedConditions.elementToBeClickable(productPage.addToCartButton));
        js.executeScript("arguments[0].scrollIntoView(true);", productPage.addToCartButton);
        js.executeScript("arguments[0].click();", productPage.addToCartButton);

        WaitUtils.waitForElementToBeInvisible(driver);

        wait.until(ExpectedConditions.elementToBeClickable(checkoutPage.cartIcon));
        js.executeScript("arguments[0].scrollIntoView(true);", checkoutPage.cartIcon);
        js.executeScript("arguments[0].click();", checkoutPage.cartIcon);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#shoppingCart")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#shoppingCart tbody tr")));

        List<WebElement> cartRowsAfterUpdate = driver.findElements(By.cssSelector("#shoppingCart tbody tr"));

        for (WebElement row : cartRowsAfterUpdate) {
            WebElement productQuantityElement = row.findElement(By.cssSelector("td.smollCell.quantityMobile label.ng-binding"));
            String productQuantityString = productQuantityElement.getText().trim();
            int productQuantityAfterUpdate = Integer.parseInt(productQuantityString);

            softAssert.assertEquals(
                    productQuantityAfterUpdate,
                    initialQuantity + 1,
                    "Failed to update the product quantity"
            );
        }

        System.out.println("TC_CART_03_validateUpdateProductFromCart passed successfully");
    }

}
