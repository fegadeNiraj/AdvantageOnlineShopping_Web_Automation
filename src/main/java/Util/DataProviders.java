package Util;

import org.testng.annotations.DataProvider;

public class DataProviders
{
    @DataProvider(name = "invalidEmailID")
    public static Object[][] invalidEmailID(){
        return new Object[][]{
                {"username.domain.com"},
                {"@domain.com"}
        };
    }
}
