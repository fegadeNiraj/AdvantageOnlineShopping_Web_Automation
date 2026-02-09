import Core.BaseTest;
import Pages.*;
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

public class AccountTests extends BaseTest {

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void TC_ACCOUNT_01_validateUpdateAccountDetails()
            throws IOException, ParseException {

        HomePage homePage = new HomePage(driver);
        LoginPage loginPage = new LoginPage(driver);
        AccountPage accountPage = new AccountPage(driver);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
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

        wait.until(ExpectedConditions.visibilityOf(loginPage.loggedInUserName));

        // ---------- Navigate to My Account ----------
        js.executeScript("arguments[0].click();", homePage.getUserIcon());
        wait.until(ExpectedConditions.visibilityOf(accountPage.myAccountButton));
        js.executeScript("arguments[0].click();", accountPage.myAccountButton);

        wait.until(ExpectedConditions.visibilityOf(accountPage.myAccountContainer));

        // ---------- Edit Account ----------
        js.executeScript("arguments[0].click();", accountPage.accountEditButton);
        wait.until(ExpectedConditions.visibilityOf(accountPage.accountDetailsForm));

        accountPage.cityDetails.clear();
        accountPage.cityDetails.sendKeys(userLoginDetails.get("city"));

        js.executeScript("arguments[0].click();", accountPage.saveButton);

        // ---------- Validation ----------
        wait.until(ExpectedConditions.visibilityOf(accountPage.updateSuccessfulMessage));
        softAssert.assertTrue(
                accountPage.updateSuccessfulMessage.isDisplayed(),
                "Account was not updated successfully"
        );

        wait.until(ExpectedConditions.visibilityOf(accountPage.myAccountContainer));
        softAssert.assertTrue(
                accountPage.myAccountContainer.isDisplayed(),
                "Failed to redirect to My Account page"
        );

        System.out.println("TC_ACCOUNT_01_validateUpdateAccountDetails passed successfully");
    }

}
