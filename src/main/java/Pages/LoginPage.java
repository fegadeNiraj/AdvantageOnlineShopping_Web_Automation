package Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {

    public WebDriver driver;

    public LoginPage(WebDriver driver)
    {
        this.driver = driver;
        PageFactory.initElements(driver,this);
    }


    @FindBy(name = "username")
    public WebElement inputLoginFormUserName;

    @FindBy(name = "password")
    public WebElement inputLoginFormPassword;

    @FindBy(id = "sign_in_btn")
    public WebElement loginFormSignInButton;

    @FindBy(css = ".hi-user.containMiniTitle.ng-binding")
    public WebElement loggedInUserName;

    @FindBy(id = "signInResultMessage")
    public WebElement loginErrorMessage;

    @FindBy(xpath = "//label[@role='link'][normalize-space()='Sign out']")
    public WebElement singOutButton;

}
