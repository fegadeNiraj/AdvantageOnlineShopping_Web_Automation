import Constant.Constant;
import Core.BaseTest;
import Pages.HomePage;
import Pages.LoginPage;
import Util.JsonReader;
import Util.RetryAnalyzer;
import Util.WaitUtils;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

public class LoginTests extends BaseTest {

    @Test(dependsOnMethods = "RegistrationTests.TC_REG_01_validateUserRegistration",retryAnalyzer = RetryAnalyzer.class,priority = 1)
    public void TC_LOGIN_01_validateLoginUser() throws IOException, InterruptedException {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        HomePage homePage = new HomePage(driver);
        LoginPage loginPage = new LoginPage(driver);

        homePage.launchHomePage();
        wait.until(ExpectedConditions.elementToBeClickable(homePage.userIcon)).click();
        WaitUtils.waitForElementToBeInvisible(driver);

        loginPage.inputLoginFormUserName.sendKeys(RegistrationTests.registeredUserName);
        String userName = loginPage.inputLoginFormUserName.getAttribute("value");
        loginPage.inputLoginFormPassword.sendKeys(RegistrationTests.registeredUserPassword);

        loginPage.loginFormSignInButton.click();
        wait.until(ExpectedConditions.visibilityOf(loginPage.loggedInUserName));

        String loggedInUserName = loginPage.loggedInUserName.getText();
        String expectedLoggedInUserName = userName;

        softAssert.assertEquals(loggedInUserName,expectedLoggedInUserName,"User Login Failed");
        softAssert.assertEquals(driver.getCurrentUrl(), Constant.LOGGEDIN_PAGE_URL,"User is not on the Logged in page. URL mismatch after Login.");

        System.out.println("TC_LOGIN_01_validateLoginUser passed successfully");
    }

    @Test(dependsOnMethods = "RegistrationTests.TC_REG_01_validateUserRegistration", retryAnalyzer = RetryAnalyzer.class,priority = 2)
    public void TC_LOGIN_02_validateLoginUserWithInvalidPassword() throws IOException, InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        HomePage homePage = new HomePage(driver);
        LoginPage loginPage = new LoginPage(driver);

        homePage.launchHomePage();
        wait.until(ExpectedConditions.elementToBeClickable(homePage.userIcon)).click();
        WaitUtils.waitForElementToBeInvisible(driver);

        wait.until(ExpectedConditions.visibilityOf(loginPage.inputLoginFormUserName)).sendKeys(RegistrationTests.registeredUserName);
        String userName = loginPage.inputLoginFormUserName.getAttribute("value");
        wait.until(ExpectedConditions.visibilityOf(loginPage.inputLoginFormPassword)).sendKeys(Constant.USER_INCORRECT_LOGINPASSWORD);

        loginPage.loginFormSignInButton.click();
        wait.until(ExpectedConditions.textToBePresentInElement(loginPage.loginErrorMessage,Constant.INCORRECT_USERNAME_OR_PASSWORD_ERRORMESSAGE));

        softAssert.assertEquals(loginPage.loginErrorMessage.getText(),Constant.INCORRECT_USERNAME_OR_PASSWORD_ERRORMESSAGE,"Failed to get 'Incorrect user name or password' message");
        softAssert.assertEquals(driver.getCurrentUrl(),Constant.INCORRECT_LOGIN_URL,"Current URL did not match the expected incorrect login URL after failed login attempt.");

        System.out.println("TC_LOGIN_02_validateLoginUserWithInvalidPassword passed successfully");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class,priority = 3)
    public void TC_LOGOUT_01_validateLogoutUser() throws IOException, ParseException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        HomePage homePage = new HomePage(driver);
        LoginPage loginPage = new LoginPage(driver);

        homePage.launchHomePage();
        wait.until(ExpectedConditions.elementToBeClickable(homePage.userIcon)).click();
        WaitUtils.waitForElementToBeInvisible(driver);

        Map<String,String> userLoginDetails = JsonReader.getJsonMap("existingUser");
        String username = userLoginDetails.get("userName");
        String password = userLoginDetails.get("password");

        wait.until(ExpectedConditions.visibilityOf(loginPage.inputLoginFormUserName)).sendKeys(username);
        wait.until(ExpectedConditions.visibilityOf(loginPage.inputLoginFormPassword)).sendKeys(password);

        loginPage.loginFormSignInButton.click();
        wait.until(ExpectedConditions.visibilityOf(loginPage.loggedInUserName));
        wait.until(ExpectedConditions.elementToBeClickable(homePage.userIcon)).click();
        wait.until(ExpectedConditions.elementToBeClickable(loginPage.singOutButton)).click();
        wait.until(ExpectedConditions.invisibilityOf(loginPage.loggedInUserName));

        softAssert.assertTrue(loginPage.loggedInUserName.getText().trim().isEmpty(),"Something wrong... failed to logout user");
        System.out.println("TC_LOGOUT_01_validateLogoutUser passed successfully");

    }

    @Test(retryAnalyzer = RetryAnalyzer.class,priority = 4)
    public void TC_LOGIN_03_validateEmptyUsernameLoginFieldValidations() throws IOException, InterruptedException, ParseException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        HomePage homePage = new HomePage(driver);
        LoginPage loginPage = new LoginPage(driver);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        homePage.launchHomePage();
        wait.until(ExpectedConditions.elementToBeClickable(homePage.userIcon)).click();
        WaitUtils.waitForElementToBeInvisible(driver);
        Map<String,String> userLoginDetails = JsonReader.getJsonMap("existingUser");
        String password = userLoginDetails.get("password");
        wait.until(ExpectedConditions.visibilityOf(loginPage.inputLoginFormPassword)).sendKeys(password);
        loginPage.loginFormSignInButton.click();

        String errorMessageForUsername = (String) js.executeScript(
                "let el = document.querySelector(\"sec-view[a-hint='Username'] label.invalid\");" +
                        "return el ? el.innerText : null;");

        softAssert.assertEquals(errorMessageForUsername,Constant.VALIDATION_ERROR_MESSAGES_FOR_BLANK_LOGIN_USERNAME);
        System.out.println("TC_LOGIN_03_validateEmptyUsernameLoginFieldValidations passed successfully");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class,priority = 5)
    public void TC_LOGIN_04_validateEmptyPasswordLoginFieldValidations() throws IOException, InterruptedException, ParseException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        HomePage homePage = new HomePage(driver);
        LoginPage loginPage = new LoginPage(driver);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        homePage.launchHomePage();
        wait.until(ExpectedConditions.elementToBeClickable(homePage.userIcon)).click();
        WaitUtils.waitForElementToBeInvisible(driver);
        Map<String,String> userLoginDetails = JsonReader.getJsonMap("existingUser");
        String username = userLoginDetails.get("userName");
        wait.until(ExpectedConditions.visibilityOf(loginPage.inputLoginFormUserName)).sendKeys(username);
        loginPage.inputLoginFormUserName.sendKeys(Keys.TAB);
        loginPage.loginFormSignInButton.click();

        Thread.sleep(2000);
        String errorMessageForPassword = (String) js.executeScript(
                "let el = document.querySelector(\"sec-view[sec-model='loginUser.loginPassword'] label.invalid\");" +
                        "return el ? el.innerText : null;");

        softAssert.assertEquals(errorMessageForPassword,Constant.VALIDATION_ERROR_MESSAGES_FOR_BLANK_LOGIN_PASSWORD);
        System.out.println("TC_LOGIN_04_validateEmptyPasswordLoginFieldValidations passed successfully");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class,priority = 6)
    public void TC_LOGIN_05_validateLoginWithCaseSensitiveDetails() throws IOException, ParseException, InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        HomePage homePage = new HomePage(driver);
        LoginPage loginPage = new LoginPage(driver);

        homePage.launchHomePage();
        wait.until(ExpectedConditions.elementToBeClickable(homePage.userIcon)).click();
        WaitUtils.waitForElementToBeInvisible(driver);

        Map<String,String> userLoginDetails = JsonReader.getJsonMap("existingUser");
        String usernameUpper = userLoginDetails.get("userName").toUpperCase();
        String passwordUpper = userLoginDetails.get("password").toUpperCase();

        wait.until(ExpectedConditions.visibilityOf(loginPage.inputLoginFormUserName)).sendKeys(usernameUpper);
        wait.until(ExpectedConditions.visibilityOf(loginPage.inputLoginFormPassword)).sendKeys(passwordUpper);
        wait.until(ExpectedConditions.elementToBeClickable(loginPage.loginFormSignInButton)).click();
        wait.until(ExpectedConditions.textToBePresentInElement(loginPage.loginErrorMessage,Constant.INCORRECT_USERNAME_OR_PASSWORD_ERRORMESSAGE));

        softAssert.assertEquals(loginPage.loginErrorMessage.getText(),Constant.INCORRECT_USERNAME_OR_PASSWORD_ERRORMESSAGE,"Failed to get 'Incorrect user name or password' message");

        String usernameLower = usernameUpper.toLowerCase();
        String passwordLower = passwordUpper.toLowerCase();

        loginPage.inputLoginFormUserName.clear();
        loginPage.inputLoginFormPassword.clear();
        wait.until(ExpectedConditions.visibilityOf(loginPage.inputLoginFormUserName)).sendKeys(usernameLower);
        wait.until(ExpectedConditions.visibilityOf(loginPage.inputLoginFormPassword)).sendKeys(passwordLower);
        wait.until(ExpectedConditions.elementToBeClickable(loginPage.loginFormSignInButton)).click();

        wait.until(ExpectedConditions.textToBePresentInElement(loginPage.loginErrorMessage,Constant.INCORRECT_USERNAME_OR_PASSWORD_ERRORMESSAGE));

        softAssert.assertEquals(loginPage.loginErrorMessage.getText(),Constant.INCORRECT_USERNAME_OR_PASSWORD_ERRORMESSAGE,"Failed to get 'Incorrect user name or password' message");


        System.out.println("TC_LOGIN_05_validateLoginWithCaseSensitiveDetails passed successfully");

    }


}
