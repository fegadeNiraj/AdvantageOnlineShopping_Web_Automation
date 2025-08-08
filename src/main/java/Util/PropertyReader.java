package Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import Enum.DriverType;

public class PropertyReader {

    public static String getProperty(String key) throws IOException {

        String configFilePath = "config.properties";
        BufferedReader reader = new BufferedReader(new FileReader(configFilePath));
        Properties properties = new Properties();
        properties.load(reader);
        String value = properties.getProperty(key);
        return value;
    }

    public static DriverType getBrowser() throws IOException {
        String browserName = getProperty("browser");

        if(browserName.equals("chrome"))
        {
            return DriverType.CHROME;
        } else if (browserName.equals("firefox")) {
            return DriverType.FIREFOX;

        }else {
            throw new RuntimeException("Please provide valid browser value, accepted browser are: chrome, firefox");
        }
    }
}
