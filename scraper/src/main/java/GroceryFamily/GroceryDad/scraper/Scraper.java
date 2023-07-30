package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.cache.Cache;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.Configuration;
import io.github.antivoland.sfc.FileCache;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.function.Consumer;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.using;
import static java.lang.String.format;

public abstract class Scraper { // todo: think about robots.txt
    private final GroceryDadConfig.Scraper config;
    private final WebDriver driver;

    protected Scraper(GroceryDadConfig.Scraper config, WebDriver driver) {
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
                scrap(categories, product -> cache.save(product.code, product));
                // todo: do something with cache
            });
        });
    }

    protected abstract void acceptOrRejectCookies();

    protected abstract void switchToEnglish();

    protected abstract void scrap(List<String> categories, Consumer<Product> handler);

    private FileCache<Product> cache(List<String> categories) {
        return Cache.factory(config.cache.directory).get(categories);
    }

    private void waitUntilPageLoads() {
        new WebDriverWait(driver, config.timeout).until(Scraper::pageIsReady);
    }

    private static boolean pageIsReady(WebDriver driver) {
        return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
    }

    public static Scraper create(GroceryDadConfig.Scraper config, WebDriver driver) {
        return switch (config.namespace) {
            case Namespace.BARBORA -> new BarboraScraper(config, driver);
            case Namespace.PRISMA -> new PrismaScraper(config, driver);
            case Namespace.RIMI -> new RimiScraper(config, driver);
            default -> throw new UnsupportedOperationException(format("Unrecognized namespace '%s'", config.namespace));
        };
    }
}