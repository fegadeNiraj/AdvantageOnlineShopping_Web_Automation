package Core;

import Util.PropertyReader;
import org.openqa.selenium.WebDriver;
import Enum.DriverType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DriverManager
{
    public WebDriver driver;
    public DriverType driverType;

    public WebDriver getDriver() throws IOException {
        if(driver==null)
        {
            initDriver();
        }
        return driver;
    }

    public WebDriver initDriver() throws IOException {
        driverType = PropertyReader.getBrowser();
        switch (driverType)
        {
            case CHROME :
                System.setProperty("webdriver.chrome.driver",PropertyReader.getProperty("chromeDriverPath"));
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-save-password-bubble");
                options.addArguments("--headless=new");
                options.addArguments("window-size=1920,1080");
                options.addArguments("--disable-gpu");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");

                Map<String, Object> prefs = new HashMap<>();
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                options.setExperimentalOption("prefs", prefs);

                driver = new ChromeDriver(options);
                break;

            default :
                System.setProperty("webdriver.chrome.driver",PropertyReader.getProperty("chromeDriverPath"));
                driver = new ChromeDriver();
                break;
        }
        return driver;
    }

    public void quitDriver()
    {
        if(driver!= null)
        {
            driver.quit();
            driver = null;
        }
    }
}
