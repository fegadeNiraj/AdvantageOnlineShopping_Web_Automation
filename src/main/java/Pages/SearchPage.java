package Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SearchPage
{
    public WebDriver driver;

    public SearchPage(WebDriver driver)
    {
        this.driver = driver;
        PageFactory.initElements(driver,this);
    }


    @FindBy(xpath = "//a[@title='SEARCH']")
    public WebElement searchIcon;

    @FindBy(id = "autoComplete")
    public WebElement searchBar;

    @FindBy(css = "div.cell.categoryRight ul")
    public WebElement searchResultContainer;

    @FindBy(xpath = "//label[contains(text(),'No search results found.')]")
    public WebElement searchResultError;

}
