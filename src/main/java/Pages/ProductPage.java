package Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class ProductPage {

    public WebDriver driver;

    public ProductPage(WebDriver driver)
    {
        this.driver = driver;
        PageFactory.initElements(driver,this);
    }

    @FindBy(id = "laptopsImg")
    public WebElement laptops;

    @FindBy(css = "div.categoryRight ul")
    public List<WebElement> laptopsList;

    @FindBy(xpath = "//a[normalize-space()='HP ZBook 17 G2 Mobile Workstation']")
    public WebElement laptopToBeSelected;

    @FindBy(id = "Description")
    public WebElement laptopToBeSelectedDescription;

    @FindBy(xpath = "//span[@title='BLACK']")
    public WebElement selectBlackColor;

    @FindBy(xpath = "//span[@title='GRAY']")
    public WebElement selectGreyColor;

    @FindBy(name = "save_to_cart")
    public WebElement addToCartButton;

    @FindBy(css = "#shoppingCartLink .cart")
    public WebElement cartValue;

    @FindBy(name = "quantity")
    public WebElement productQuantity;

    @FindBy(css = "div.minus")
    public WebElement decreaseQuantity;

    @FindBy(css = "div.plus")
    public WebElement increaseQuantity;

}
