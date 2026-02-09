package Util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitUtils {
    public static void waitForElementToBeInvisible(WebDriver driver)
    {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        wait.until(driver1 -> {
            try {
                WebElement loader = driver1.findElement(By.cssSelector(".loader"));
                String display = loader.getCssValue("display");
                String opacity = loader.getCssValue("opacity");
                return display.equals("none") && opacity.equals("0");
            } catch (Exception e) {
                return true;
            }
        });
    }
}