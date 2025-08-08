package Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class AccountPage
{
    public WebDriver driver;

    public AccountPage(WebDriver driver)
    {
        this.driver = driver;
        PageFactory.initElements(driver,this);
    }

    @FindBy(xpath = "//label[@role='link'][normalize-space()='My account']")
    public WebElement myAccountButton;

    @FindBy(xpath = "//label[@role='link'][normalize-space()='My orders']")
    public WebElement myOrdersButton;


    @FindBy(id = "myAccountContainer")
    public WebElement myAccountContainer;

    @FindBy(css = "h3.blueLink a.floatRigth")
    public WebElement accountEditButton;

    @FindBy(id = "form")
    public WebElement accountDetailsForm;

    @FindBy(name = "cityAccountDetails")
    public WebElement cityDetails;

    @FindBy(id = "save_btn")
    public WebElement saveButton;

    @FindBy(css = "label.noPadding")
    public WebElement updateSuccessfulMessage;

    @FindBy(xpath = "//label[normalize-space()=' - No orders - ']")
    public WebElement emptyOrders;

    @FindBy(css = "div.myOrderSection tbody tr")
    public List<WebElement> ordersList;

    @FindBy(id = "confBtn_1")
    public WebElement confirmCancelOrder;

    @FindBy(id = "confBtn_2")
    public WebElement declineCancelOrder;
}
