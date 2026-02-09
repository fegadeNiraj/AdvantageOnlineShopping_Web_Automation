import Core.BaseTest;
import Pages.AccountPage;
import Pages.HomePage;
import Pages.LoginPage;
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

public class OrderTests extends BaseTest {

    @Test(
            retryAnalyzer = RetryAnalyzer.class,
            priority = 1
    )
    public void TC_ORDER_01_validatePastOrders() throws IOException, ParseException, InterruptedException {
        HomePage homePage = new HomePage(driver);
        LoginPage loginPage = new LoginPage(driver);
        AccountPage accountPage = new AccountPage(driver);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        homePage.launchHomePage();

        wait.until(ExpectedConditions.visibilityOf(homePage.getUserIcon()));
        js.executeScript("arguments[0].scrollIntoView(true);", homePage.getUserIcon());
        js.executeScript("arguments[0].click();", homePage.getUserIcon());

        WaitUtils.waitForElementToBeInvisible(driver);

        Map<String,String> userLoginDetails = JsonReader.getJsonMap("existingUser");
        loginPage.inputLoginFormUserName.sendKeys(userLoginDetails.get("userName"));
        loginPage.inputLoginFormPassword.sendKeys(userLoginDetails.get("password"));

        wait.until(ExpectedConditions.visibilityOf(loginPage.loginFormSignInButton));
        js.executeScript("arguments[0].scrollIntoView(true);", loginPage.loginFormSignInButton);
        js.executeScript("arguments[0].click();", loginPage.loginFormSignInButton);

        wait.until(ExpectedConditions.visibilityOf(loginPage.loggedInUserName));

        wait.until(ExpectedConditions.visibilityOf(homePage.getUserIcon()));
        js.executeScript("arguments[0].scrollIntoView(true);", homePage.getUserIcon());
        js.executeScript("arguments[0].click();", homePage.getUserIcon());

        wait.until(ExpectedConditions.visibilityOf(accountPage.myOrdersButton));
        js.executeScript("arguments[0].scrollIntoView(true);", accountPage.myOrdersButton);
        js.executeScript("arguments[0].click();", accountPage.myOrdersButton);

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfAllElements(accountPage.ordersList),
                ExpectedConditions.visibilityOf(accountPage.emptyOrders)
        ));

        if (accountPage.ordersList.isEmpty()) {
            softAssert.assertTrue(accountPage.emptyOrders.isDisplayed(), "Expected empty orders message.");
        } else {
            for (int i = 1; i < accountPage.ordersList.size(); i++) {
                WebElement orderRow = accountPage.ordersList.get(i);
                String orderNumber = orderRow.findElement(By.cssSelector("td")).getText();
                softAssert.assertFalse(orderRow.getText().isEmpty(), "Order row should not be empty.");
            }
        }

        System.out.println("TC_ORDER_01_validatePastOrders passed successfully");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class,priority = 2)
    public void TC_ORDER_02_validateCancelPastOrders() throws IOException, ParseException, InterruptedException {
        HomePage homePage = new HomePage(driver);
        LoginPage loginPage = new LoginPage(driver);
        AccountPage accountPage = new AccountPage(driver);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        homePage.launchHomePage();

        wait.until(ExpectedConditions.visibilityOf(homePage.getUserIcon()));
        js.executeScript("arguments[0].scrollIntoView(true);", homePage.getUserIcon());
        js.executeScript("arguments[0].click();", homePage.getUserIcon());

        WaitUtils.waitForElementToBeInvisible(driver);

        Map<String, String> userLoginDetails = JsonReader.getJsonMap("existingUser");
        loginPage.inputLoginFormUserName.sendKeys(userLoginDetails.get("userName"));
        loginPage.inputLoginFormPassword.sendKeys(userLoginDetails.get("password"));

        wait.until(ExpectedConditions.visibilityOf(loginPage.loginFormSignInButton));
        js.executeScript("arguments[0].scrollIntoView(true);", loginPage.loginFormSignInButton);
        js.executeScript("arguments[0].click();", loginPage.loginFormSignInButton);

        wait.until(ExpectedConditions.visibilityOf(loginPage.loggedInUserName));

        wait.until(ExpectedConditions.visibilityOf(homePage.getUserIcon()));
        js.executeScript("arguments[0].scrollIntoView(true);", homePage.getUserIcon());
        js.executeScript("arguments[0].click();", homePage.getUserIcon());

        wait.until(ExpectedConditions.visibilityOf(accountPage.myOrdersButton));
        js.executeScript("arguments[0].scrollIntoView(true);", accountPage.myOrdersButton);
        js.executeScript("arguments[0].click();", accountPage.myOrdersButton);

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfAllElements(accountPage.ordersList),
                ExpectedConditions.visibilityOf(accountPage.emptyOrders)
        ));

        if (!accountPage.ordersList.isEmpty()) {
            WebElement orderRow = accountPage.ordersList.get(1);
            String orderNumber = orderRow.findElement(By.cssSelector("td")).getText();
            WebElement removeButton = orderRow.findElement(By.cssSelector("a.remove"));

            js.executeScript("arguments[0].scrollIntoView(true);", removeButton);
            js.executeScript("arguments[0].click();", removeButton);

            wait.until(ExpectedConditions.visibilityOf(accountPage.confirmCancelOrder));
            js.executeScript("arguments[0].scrollIntoView(true);", accountPage.confirmCancelOrder);
            js.executeScript("arguments[0].click();", accountPage.confirmCancelOrder);

            Thread.sleep(5000);

            List<WebElement> updatedOrderList = accountPage.ordersList;
            boolean isOrderStillPresent = updatedOrderList.stream()
                    .anyMatch(row -> row.getText().contains(orderNumber));
            softAssert.assertFalse(isOrderStillPresent, "Order was not cancelled");
        } else {
            softAssert.fail("No orders found to cancel");
        }
    }
}
