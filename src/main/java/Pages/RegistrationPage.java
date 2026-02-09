package Pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RegistrationPage
{
    WebDriver driver;

    public RegistrationPage(WebDriver driver)
    {
        this.driver = driver;
        PageFactory.initElements(driver,this);
    }

    @FindBy(id = "form")
    public WebElement createNewAccountForm;

    @FindBy(name = "usernameRegisterPage")
    public WebElement inputUserName;

    @FindBy(name = "emailRegisterPage")
    public WebElement inputEmailID;

    @FindBy(name = "passwordRegisterPage")
    public WebElement inputPassword;

    @FindBy(name = "confirm_passwordRegisterPage")
    public WebElement inputConfirmPassword;

    @FindBy(name = "first_nameRegisterPage")
    public WebElement inputFirstName;

    @FindBy(name = "last_nameRegisterPage")
    public WebElement inputLastName;

    @FindBy(name = "phone_numberRegisterPage")
    public WebElement inputPhoneNumber;

    @FindBy(name = "countryListboxRegisterPage")
    public WebElement inputCountryName;

    @FindBy(name = "cityRegisterPage")
    public WebElement inputCityName;

    @FindBy(name = "addressRegisterPage")
    public WebElement inputAddress;

    @FindBy(name = "state_/_province_/_regionRegisterPage")
    public WebElement inputStateName;

    @FindBy(name = "postal_codeRegisterPage")
    public WebElement inputPostalCode;

    @FindBy(name = "i_agree")
    public WebElement userRegisterAgreeCheckbox;

    @FindBy(id = "register_btn")
    public WebElement userRegisterButton;

    @FindBy(css = ".hi-user.containMiniTitle.ng-binding")
    public WebElement registeredUserName;

    @FindBy(xpath = "//label[@data-ng-show='!registerSuccess']")
    public WebElement userAlreadyExistsMessage;

    public void fillBasicDetails(
            String username,
            String email,
            String password,
            String firstName,
            String lastName,
            String phoneNumber
    ) {
        inputUserName.sendKeys(username);
        inputEmailID.sendKeys(email);
        inputPassword.sendKeys(password);
        inputConfirmPassword.sendKeys(password);
        inputFirstName.sendKeys(firstName);
        inputLastName.sendKeys(lastName);
        inputPhoneNumber.sendKeys(phoneNumber);
    }

    public void selectCountry(String countryName, WebDriverWait wait) {
        Select select = new Select(inputCountryName);
        wait.until(d ->
                select.getOptions()
                        .stream()
                        .anyMatch(o -> o.getText().trim().equals(countryName))
        );
        select.selectByVisibleText(countryName);
    }

    public void fillAddressDetails(
            String city,
            String address,
            String state,
            String postalCode
    ) {
        inputCityName.sendKeys(city);
        inputAddress.sendKeys(address);
        inputStateName.sendKeys(state);
        inputPostalCode.sendKeys(postalCode);
    }

    public void submitRegistration() {
        userRegisterAgreeCheckbox.click();
        userRegisterButton.click();
    }

    public String getRegisteredUserName() {
        return registeredUserName.getText();
    }




}
