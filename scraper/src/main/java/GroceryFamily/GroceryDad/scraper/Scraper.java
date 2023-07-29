package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.GroceryDad.scraper.cache.Cache;
import com.codeborne.selenide.Configuration;
import io.github.antivoland.sfc.FileCache;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static com.codeborne.selenide.Selenide.using;

public abstract class Scraper {
    final GroceryDadConfig.Scraper config;
    private final WebDriver driver;

    Scraper(GroceryDadConfig.Scraper config, WebDriver driver) {
        this.config = config;
        this.driver = driver;
    }

    public final void scrap() {
        Configuration.timeout = config.timeout.toMillis();
        using(driver, () -> config.categories.forEach(this::scrap));
    }

    abstract void scrap(List<String> categories);

    protected final FileCache<Product> cache(List<String> categories) {
        return Cache.factory(config.cache.directory).get(categories);
    }

    protected final void waitUntilPageLoads() {
        new WebDriverWait(driver, config.timeout).until(Scraper::pageIsReady);
    }

    static boolean pageIsReady(WebDriver driver) {
        return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
    }

    public static Scraper create(GroceryDadConfig.Scraper config, WebDriver driver) {
        return switch (config.source) {
            case BARBORA -> new BarboraScraper(config, driver);
            case PRISMA -> new PrismaScraper(config, driver);
            case RIMI -> new RimiScraper(config, driver);
        };
    }
}