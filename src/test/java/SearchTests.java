import Constant.Constant;
import Core.BaseTest;
import Pages.HomePage;
import Pages.SearchPage;
import Util.RetryAnalyzer;
import Util.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class SearchTests extends BaseTest {

    @Test(retryAnalyzer = RetryAnalyzer.class, priority = 1)
    public void TC_SEARCH_01_validateSearchForValidProduct() throws IOException {
        HomePage homePage = new HomePage(driver);
        SearchPage searchPage = new SearchPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        homePage.launchHomePage();

        wait.until(ExpectedConditions.visibilityOf(searchPage.searchIcon));
        js.executeScript("arguments[0].scrollIntoView(true);", searchPage.searchIcon);
        js.executeScript("arguments[0].click();", searchPage.searchIcon);

        WaitUtils.waitForElementToBeInvisible(driver);

        wait.until(ExpectedConditions.visibilityOf(searchPage.searchBar)).sendKeys(Constant.SEARCH_PRODUCT);
        searchPage.searchBar.sendKeys(Keys.ENTER);

        wait.until(ExpectedConditions.visibilityOf(searchPage.searchResultContainer));

        List<WebElement> productsList = driver.findElements(By.cssSelector("div.cell.categoryRight ul > li"));
        softAssert.assertTrue(!productsList.isEmpty(), "No products found in the search results.");

        System.out.println("TC_SEARCH_01_validateSearchForValidProduct passed successfully");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, priority = 2)
    public void TC_SEARCH_02_validateSearchForInvalidProduct() throws IOException {
        HomePage homePage = new HomePage(driver);
        SearchPage searchPage = new SearchPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        homePage.launchHomePage();

        wait.until(ExpectedConditions.visibilityOf(searchPage.searchIcon));
        js.executeScript("arguments[0].scrollIntoView(true);", searchPage.searchIcon);
        js.executeScript("arguments[0].click();", searchPage.searchIcon);

        WaitUtils.waitForElementToBeInvisible(driver);

        wait.until(ExpectedConditions.visibilityOf(searchPage.searchBar)).sendKeys(Constant.SEARCH_PRODUCT_INVALID);
        searchPage.searchBar.sendKeys(Keys.ENTER);

        wait.until(ExpectedConditions.visibilityOf(searchPage.searchResultError));

        softAssert.assertTrue(
                searchPage.searchResultError.getText().contains(Constant.NO_SEARCH_RESULT_ERROR_MESSAGE),
                "Unable to get the expected message for invalid product search"
        );

        System.out.println("TC_SEARCH_02_validateSearchForInvalidProduct passed successfully");
    }
}
