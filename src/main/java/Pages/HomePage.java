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

public class HomePage {
    public WebDriver driver;

    public HomePage(WebDriver driver)
    {
        this.driver = driver;
        PageFactory.initElements(driver,this);
    }

    @FindBy(xpath = "//header[@ng-show='welcome']")
    public WebElement homePageHeader;

    @FindBy(id = "hrefUserIcon")
    public WebElement userIcon;

    @FindBy(xpath = "//a[normalize-space()='CREATE NEW ACCOUNT']")
    public WebElement createNewAccount;

    public void launchHomePage() throws IOException {
        driver.get(PropertyReader.getProperty("baseUrl"));
        driver.manage().window().maximize();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(homePageHeader));

    }
}
