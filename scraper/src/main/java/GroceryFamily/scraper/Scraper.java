package GroceryFamily.scraper;

import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.scraper.cache.Cache;
import io.github.antivoland.sfc.FileCache;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Selenide.using;

abstract class Scraper {
    protected final GroceryDadConfig.Scraper config;
    private final WebDriver driver;

    protected Scraper(GroceryDadConfig.Scraper config, WebDriver driver) {
        this.config = config;
        this.driver = driver;
    }

    final void scrap() {
        using(driver, () -> config.categories.forEach(this::scrap));
    }

    protected abstract void scrap(List<String> categories);

    protected final FileCache<Product> cache(List<String> categories) {
        return Cache.factory(config.cache.directory).get(categories);
    }

    protected final void waitUntilPageLoads() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(Scraper::pageIsReady);
    }

    static boolean pageIsReady(WebDriver driver) {
        return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
    }

    static Scraper create(GroceryDadConfig.Scraper config, WebDriver driver) {
        return switch (config.source) {
            case BARBORA -> new BarboraScraper(config, driver);
            case PRISMA -> new PrismaScraper(config, driver);
            case RIMI -> new RimiScraper(config, driver);
        };
    }
}