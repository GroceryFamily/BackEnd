package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.cache.Cache;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.Configuration;
import io.github.antivoland.sfc.FileCache;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.using;

public abstract class Scraper { // todo: think about robots.txt
    final GroceryDadConfig.Scraper config;
    private final WebDriver driver;

    Scraper(GroceryDadConfig.Scraper config, WebDriver driver) {
        this.config = config;
        this.driver = driver;
    }

    public final void scrap() {
        Configuration.timeout = config.timeout.toMillis();
        using(driver, () -> {
            open(config.uri);
            waitUntilPageLoads();
            acceptOrRejectCookies();
            switchToEnglish();
            config.categories.forEach(categories -> {
                FileCache<Product> cache = cache(categories); // todo: enable/disable cache
                scrap(categories, cache); // todo: use product handler
                // todo: do something with cache
            });
        });
    }

    abstract void acceptOrRejectCookies();

    abstract void switchToEnglish();

    abstract void scrap(List<String> categories, FileCache<Product> cache);

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