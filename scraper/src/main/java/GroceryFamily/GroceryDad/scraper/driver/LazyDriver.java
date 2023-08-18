package GroceryFamily.GroceryDad.scraper.driver;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;

public class LazyDriver {
    private final GroceryDadConfig.Platform.Live config;
    private SelenideDriver driver;

    public LazyDriver(GroceryDadConfig.Platform.Live config) {
        this.config = config;
    }

    public SelenideDriver get() {
        if (driver == null) {
            driver = new SelenideDriver(selenideConfig(config));
        }
        return driver;
    }

    private static SelenideConfig selenideConfig(GroceryDadConfig.Platform.Live config) {
        return new SelenideConfig()
                .browserSize("1920x1080")
                .timeout(config.waitTimeout.toMillis())
                .pageLoadTimeout(config.waitTimeout.toMillis());
    }

    public void destroy() {
        if (driver != null) driver.close();
    }
}