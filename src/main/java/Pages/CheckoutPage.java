package Pages;

import Constant.Constant;
import Util.JsonReader;
import Util.PropertyReader;
import Util.WaitUtils;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class CheckoutPage {

    public WebDriver driver;

    public CheckoutPage(WebDriver driver)
    {
        this.driver = driver;
        PageFactory.initElements(driver,this);
    }

    @FindBy(id = "shoppingCartLink")
    public WebElement cartIcon;

    @FindBy(id = "checkOutButton")
    public WebElement checkoutButton;

    @FindBy(id = "next_btn")
    public WebElement nextButton;

    @FindBy(name = "safepay_username")
    public WebElement inputSafepayUsername;

    @FindBy(name = "safepay_password")
    public WebElement inputSafepayPassword;

    @FindBy(id = "pay_now_btn_SAFEPAY")
    public WebElement payNowButton;

    @FindBy(xpath = "//span[@translate='Thank_you_for_buying_with_Advantage']")
    public WebElement orderSuccessMessage;

    @FindBy(id = "trackingNumberLabel")
    public WebElement orderTrackingDetails;

    @FindBy(id = "orderNumberLabel")
    public WebElement orderNumberDetails;

    @FindBy(xpath = "//label[@translate='Your_shopping_cart_is_empty']")
    public WebElement emptyCartMessage;

    @FindBy(className = "noUserSection")
    public WebElement loginAlertOnCheckout;

    @FindBy(xpath = "//label[@translate='Already_have_an_account']")
    public WebElement loginAlertOnCheckoutMessage;

    @FindBy(id = "newClient")
    public WebElement registerAlertOnCheckout;

    @FindBy(xpath = "//label[@translate='New_client']")
    public WebElement registerAlertOnCheckoutMessage;
}
