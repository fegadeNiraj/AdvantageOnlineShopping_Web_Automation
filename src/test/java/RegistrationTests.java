import Constant.Constant;
import Core.BaseTest;
import Pages.HomePage;
import Pages.RegistrationPage;
import Util.DataProviders;
import Util.JsonReader;
import Util.RetryAnalyzer;
import Util.WaitUtils;
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

public class RegistrationTests extends BaseTest {

    public static String registeredUserName;
    public static String registeredUserPassword;

    @Test(retryAnalyzer = RetryAnalyzer.class, priority = 1)
    public void TC_REG_01_validateUserRegistration() throws IOException, InterruptedException, ParseException {

        HomePage homePage = new HomePage(driver);
        RegistrationPage registrationPage = new RegistrationPage(driver);
        Faker faker = new Faker();

        homePage.launchHomePage();
        homePage.userIcon.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        WaitUtils.waitForElementToBeInvisible(driver);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", homePage.createNewAccount);
        js.executeScript("arguments[0].click();", homePage.createNewAccount);

        wait.until(ExpectedConditions.visibilityOf(registrationPage.createNewAccountForm));

        Map<String, String> registerUserMap = JsonReader.getJsonMap("registerUser");

        String randomUsername = registerUserMap.get("userName") + System.currentTimeMillis() % 100000;
        registeredUserName = randomUsername;

        registrationPage.inputUserName.sendKeys(randomUsername);
        registrationPage.inputEmailID.sendKeys(faker.internet().emailAddress());
        registrationPage.inputPassword.sendKeys(registerUserMap.get("password"));

        String passwordToBeConfirmed = registrationPage.inputPassword.getAttribute("value");
        registeredUserPassword = passwordToBeConfirmed;
        registrationPage.inputConfirmPassword.sendKeys(passwordToBeConfirmed);

        registrationPage.inputFirstName.sendKeys(registerUserMap.get("firstName"));
        registrationPage.inputLastName.sendKeys(registerUserMap.get("lastName"));
        registrationPage.inputPhoneNumber.sendKeys(registerUserMap.get("phoneNumber"));

        Select countryList = new Select(registrationPage.inputCountryName);
        wait.until(driver1 -> {
            for (WebElement option : countryList.getOptions()) {
                if (option.getText().trim().equals(registerUserMap.get("countryName"))) {
                    return true;
                }
            }
            return false;
        });
        countryList.selectByVisibleText(registerUserMap.get("countryName"));

        registrationPage.inputCityName.sendKeys(registerUserMap.get("cityName"));
        registrationPage.inputAddress.sendKeys(registerUserMap.get("address"));
        registrationPage.inputStateName.sendKeys(registerUserMap.get("stateName"));
        registrationPage.inputPostalCode.sendKeys(registerUserMap.get("postalCode"));

        registrationPage.userRegisterAgreeCheckbox.click();
        registrationPage.userRegisterButton.click();

        wait.until(ExpectedConditions.visibilityOf(registrationPage.registeredUserName));
        String actualRegisteredUserName = registrationPage.registeredUserName.getText();
        String expectedUserName = randomUsername;

        softAssert.assertEquals(actualRegisteredUserName, expectedUserName, "User is not registered correctly");

        System.out.println("TC_REG_01_validateUserRegistration passed successfully");
    }

    @Test(dependsOnMethods = "TC_REG_01_validateUserRegistration", retryAnalyzer = RetryAnalyzer.class, priority = 2)
    public void TC_REG_02_validateRegistrationWithExistingUsername() throws IOException, ParseException, InterruptedException {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Faker fakeData = new Faker();

        HomePage homePage = new HomePage(driver);
        RegistrationPage registrationPage = new RegistrationPage(driver);

        homePage.launchHomePage();
        WaitUtils.waitForElementToBeInvisible(driver);

        homePage.userIcon.click();
        WaitUtils.waitForElementToBeInvisible(driver);

        js.executeScript("arguments[0].scrollIntoView(true)", homePage.createNewAccount);
        js.executeScript("arguments[0].click()", homePage.createNewAccount);

        String registerPageUrl = driver.getCurrentUrl();

        wait.until(ExpectedConditions.visibilityOf(registrationPage.createNewAccountForm));

        registrationPage.inputUserName.sendKeys(registeredUserName);
        registrationPage.inputEmailID.sendKeys(fakeData.internet().emailAddress());
        registrationPage.inputPassword.sendKeys(registeredUserPassword);

        String passwordToBeConfirmed = registrationPage.inputPassword.getAttribute("value");
        softAssert.assertNotNull(passwordToBeConfirmed, "Password confirmation should not be null");
        registrationPage.inputConfirmPassword.sendKeys(passwordToBeConfirmed);

        registrationPage.userRegisterAgreeCheckbox.click();
        registrationPage.userRegisterButton.click();

        wait.until(ExpectedConditions.textToBePresentInElement(registrationPage.userAlreadyExistsMessage, Constant.USERNAME_ALREADYEXISTS_ERRORMESSAGE));
        softAssert.assertEquals(registrationPage.userAlreadyExistsMessage.getText(), Constant.USERNAME_ALREADYEXISTS_ERRORMESSAGE,
                "Expected error message 'User name already exists' was not displayed.");
        softAssert.assertEquals(driver.getCurrentUrl(), registerPageUrl,
                "User is not on the registration page. URL mismatch after registration failure.");

        System.out.println("TC_REG_02_validateRegistrationWithExistingUsername passed successfully");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, priority = 3)
    public void TC_REG_03_validateRequiredErrorsOnEmptyFields() throws IOException, InterruptedException {

        HomePage homePage = new HomePage(driver);
        RegistrationPage registrationPage = new RegistrationPage(driver);

        homePage.launchHomePage();

        homePage.userIcon.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WaitUtils.waitForElementToBeInvisible(driver);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", homePage.createNewAccount);
        js.executeScript("arguments[0].click();", homePage.createNewAccount);

        wait.until(ExpectedConditions.visibilityOf(registrationPage.createNewAccountForm));

        registrationPage.inputUserName.click();
        registrationPage.inputUserName.sendKeys(Keys.TAB);

        registrationPage.inputEmailID.click();
        registrationPage.inputEmailID.sendKeys(Keys.TAB);

        registrationPage.inputPassword.click();
        registrationPage.inputPassword.sendKeys(Keys.TAB);

        registrationPage.inputConfirmPassword.click();
        registrationPage.inputConfirmPassword.sendKeys(Keys.TAB);

        @SuppressWarnings("unchecked")
        List<String> actualMessages = (List<String>) js.executeScript(
                "let labels = document.querySelectorAll('label.invalid');" +
                        "let messages = [];" +
                        "for (let lbl of labels) { if (lbl.innerText) messages.push(lbl.innerText.trim()); }" +
                        "return messages;"
        );

        for (String errorMessage : Constant.VALIDATION_ERROR_MESSAGES) {
            softAssert.assertTrue(actualMessages.contains(errorMessage),
                    "Missing expected validation message: " + errorMessage);
        }
        System.out.println("TC_REG_03_validateRequiredErrorsOnEmptyFields passed successfully");
    }

    @Test(dataProvider = "invalidEmailID", dataProviderClass = DataProviders.class, priority = 4, retryAnalyzer = RetryAnalyzer.class)
    public void TC_REG_04_validateRequiredValidationsOnInvalidEmailFormat(String emailID) throws IOException {

        HomePage homePage = new HomePage(driver);
        RegistrationPage registrationPage = new RegistrationPage(driver);

        homePage.launchHomePage();

        homePage.userIcon.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WaitUtils.waitForElementToBeInvisible(driver);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", homePage.createNewAccount);
        js.executeScript("arguments[0].click();", homePage.createNewAccount);

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
