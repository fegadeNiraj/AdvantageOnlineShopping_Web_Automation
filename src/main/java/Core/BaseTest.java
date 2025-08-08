package Core;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.asserts.SoftAssert;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class BaseTest
{
    public WebDriver driver;
    public DriverManager driverManager = new DriverManager();
    public SoftAssert softAssert;

    @BeforeMethod
    public void beforeMethod() throws IOException {
        driver = driverManager.getDriver();
        softAssert = new SoftAssert();
    }

    @AfterMethod
    public void afterMethod(ITestResult result) throws IOException {
        softAssert.assertAll();
        if (ITestResult.FAILURE == result.getStatus()) {
            try {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment("Screenshot on Failure", new ByteArrayInputStream(screenshot));
            } catch (Exception e) {
                System.err.println("Failed to capture screenshot: " + e.getMessage());
            }

            try {
                String pageSource = driver.getPageSource();
                Allure.addAttachment("Page Source", "text/html", pageSource, ".html");
            } catch (Exception e) {
                System.err.println("Failed to capture page source: " + e.getMessage());
            }
        }
        driverManager.quitDriver();
    }
}
