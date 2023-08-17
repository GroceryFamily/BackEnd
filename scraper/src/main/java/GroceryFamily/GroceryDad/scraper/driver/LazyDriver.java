package GroceryFamily.GroceryDad.scraper.driver;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class LazyDriver {
    private final GroceryDadConfig.Scraper config;
    private SelenideDriver driver;

    public LazyDriver(GroceryDadConfig.Scraper config) {
        this.config = config;
    }

    public SelenideDriver get() {
        if (driver == null) {
//            new SelenideConfig()
            driver = new SelenideDriver(selenideConfig());
        }
        return driver;
    }

    private SelenideConfig selenideConfig() {
        var selenideConfig = new SelenideConfig();
        selenideConfig.timeout(config.live.timeout.toMillis());
        return selenideConfig;
    }

    private ChromeDriver chromeDriver() {
        var chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--window-size=1920,1080");
        var chromeDriver = new ChromeDriver(chromeOptions);
        chromeDriver.manage().timeouts().pageLoadTimeout(config.live.timeout);
        chromeDriver.manage().timeouts().scriptTimeout(config.live.timeout);
        return chromeDriver;
    }

    public void destroy() {
        if (driver != null) driver.close();
    }
}