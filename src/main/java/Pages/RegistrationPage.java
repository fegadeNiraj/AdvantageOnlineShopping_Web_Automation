package Pages;

import Util.PropertyReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;

public class RegistrationPage
{
    WebDriver driver;

    public RegistrationPage(WebDriver driver)
    {
        this.driver = driver;
        PageFactory.initElements(driver,this);
    }



    //User Registration Form Details
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

}
