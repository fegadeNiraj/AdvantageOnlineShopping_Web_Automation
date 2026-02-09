import Constant.Constant;
import Core.BaseTest;
import Pages.HomePage;
import Pages.RegistrationPage;
import Util.*;
import com.github.javafaker.Faker;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.List;

import static Util.TestContext.registeredUserPassword;

public class RegistrationTests extends BaseTest {


    @Test(retryAnalyzer = RetryAnalyzer.class, priority = 1)
    public void TC_REG_01_validateUserRegistration()
            throws IOException, ParseException {

        HomePage homePage = new HomePage(driver);
        RegistrationPage registrationPage = new RegistrationPage(driver);
        Faker faker = new Faker();

        homePage.launchHomePage();
        homePage.getUserIcon().click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        WaitUtils.waitForElementToBeInvisible(driver);

        homePage.openCreateNewAccount();
        wait.until(ExpectedConditions.visibilityOf(registrationPage.createNewAccountForm));

        Map<String, String> registerUserMap = JsonReader.getJsonMap("registerUser");

        String randomUsername =
                registerUserMap.get("userName") + System.currentTimeMillis() % 100000;
        String password = registerUserMap.get("password");

        TestContext.registeredUserName = randomUsername;
        TestContext.registeredUserPassword = password;

        registrationPage.fillBasicDetails(
                randomUsername,
                faker.internet().emailAddress(),
                password,
                registerUserMap.get("firstName"),
                registerUserMap.get("lastName"),
                registerUserMap.get("phoneNumber")
        );

        registrationPage.selectCountry(registerUserMap.get("countryName"), wait);

        registrationPage.fillAddressDetails(
                registerUserMap.get("cityName"),
                registerUserMap.get("address"),
                registerUserMap.get("stateName"),
                registerUserMap.get("postalCode")
        );

        registrationPage.submitRegistration();

        wait.until(ExpectedConditions.visibilityOf(registrationPage.registeredUserName));
        String actualRegisteredUserName = registrationPage.getRegisteredUserName();

        softAssert.assertEquals(
                actualRegisteredUserName,
                randomUsername,
                "User is not registered correctly"
        );

        System.out.println("TC_REG_01_validateUserRegistration passed successfully");
    }

    @Test(
            dependsOnMethods = "TC_REG_01_validateUserRegistration",
            retryAnalyzer = RetryAnalyzer.class,
            priority = 2
    )
    public void TC_REG_02_validateRegistrationWithExistingUsername()
            throws IOException {

        HomePage homePage = new HomePage(driver);
        RegistrationPage registrationPage = new RegistrationPage(driver);
        Faker faker = new Faker();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        homePage.launchHomePage();
        WaitUtils.waitForElementToBeInvisible(driver);

        homePage.getUserIcon().click();
        WaitUtils.waitForElementToBeInvisible(driver);

        homePage.openCreateNewAccount();
        String registerPageUrl = driver.getCurrentUrl();

        wait.until(ExpectedConditions.visibilityOf(registrationPage.createNewAccountForm));

        registrationPage.fillBasicDetails(
                TestContext.registeredUserName,
                faker.internet().emailAddress(),
                TestContext.registeredUserPassword,
                "",     // first name not required
                "",     // last name not required
                ""      // phone not required
        );

        registrationPage.submitRegistration();

        wait.until(
                ExpectedConditions.textToBePresentInElement(
                        registrationPage.userAlreadyExistsMessage,
                        Constant.USERNAME_ALREADYEXISTS_ERRORMESSAGE
                )
        );

        softAssert.assertEquals(
                registrationPage.userAlreadyExistsMessage.getText(),
                Constant.USERNAME_ALREADYEXISTS_ERRORMESSAGE,
                "Expected error message 'User name already exists' was not displayed."
        );

        softAssert.assertEquals(
                driver.getCurrentUrl(),
                registerPageUrl,
                "User is not on the registration page. URL mismatch after registration failure."
        );

        System.out.println(
                "TC_REG_02_validateRegistrationWithExistingUsername passed successfully"
        );
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, priority = 3)
    public void TC_REG_03_validateRequiredErrorsOnEmptyFields()
            throws IOException {

        HomePage homePage = new HomePage(driver);
        RegistrationPage registrationPage = new RegistrationPage(driver);

        homePage.launchHomePage();
        homePage.getUserIcon().click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WaitUtils.waitForElementToBeInvisible(driver);

        homePage.openCreateNewAccount();

        wait.until(ExpectedConditions.visibilityOf(registrationPage.inputUserName));

        registrationPage.inputUserName.click();
        registrationPage.inputUserName.sendKeys(Keys.TAB);

        registrationPage.inputEmailID.click();
        registrationPage.inputEmailID.sendKeys(Keys.TAB);

        registrationPage.inputPassword.click();
        registrationPage.inputPassword.sendKeys(Keys.TAB);

        registrationPage.inputConfirmPassword.click();
        registrationPage.inputConfirmPassword.sendKeys(Keys.TAB);

        JavascriptExecutor js = (JavascriptExecutor) driver;

        @SuppressWarnings("unchecked")
        List<String> actualMessages = (List<String>) js.executeScript(
                "let labels = document.querySelectorAll('label.invalid');" +
                        "let messages = [];" +
                        "for (let lbl of labels) {" +
                        "  if (lbl.innerText) messages.push(lbl.innerText.trim());" +
                        "}" +
                        "return messages;"
        );

        for (String expectedMessage : Constant.VALIDATION_ERROR_MESSAGES) {
            softAssert.assertTrue(
                    actualMessages.contains(expectedMessage),
                    "Missing expected validation message: " + expectedMessage
            );
        }

        System.out.println(
                "TC_REG_03_validateRequiredErrorsOnEmptyFields passed successfully"
        );
    }

    @Test(dataProvider = "invalidEmailID", dataProviderClass = DataProviders.class, priority = 4, retryAnalyzer = RetryAnalyzer.class)
    public void TC_REG_04_validateRequiredValidationsOnInvalidEmailFormat(String emailID) throws IOException {

        HomePage homePage = new HomePage(driver);
        RegistrationPage registrationPage = new RegistrationPage(driver);

        homePage.launchHomePage();

        homePage.getUserIcon().click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WaitUtils.waitForElementToBeInvisible(driver);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", homePage.getCreateNewAccount());
        js.executeScript("arguments[0].click();", homePage.getCreateNewAccount());

        wait.until(ExpectedConditions.visibilityOf(registrationPage.createNewAccountForm));

        registrationPage.inputEmailID.sendKeys(emailID);
        registrationPage.inputEmailID.sendKeys(Keys.TAB);

        String actualErrorMessage = (String) js.executeScript(
                "let label = document.querySelector('label.animated.invalid').innerText;" +
                        "return label;"
        );

        softAssert.assertEquals(actualErrorMessage, Constant.INVALID_EMAIL_FORMAT_ERROR_MESSAGE,
                "Missing expected validation message for Email ID");
        System.out.println("TC_REG_04_validateRequiredValidationsOnInvalidEmailFormat passed successfully");
    }
}
