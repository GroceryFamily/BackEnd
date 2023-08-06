package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.cache.Cache;
import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import io.github.antivoland.sfc.FileCache;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.function.Consumer;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.using;
import static java.lang.String.format;

// todo: think about robots.txt
//  https://en.wikipedia.org/wiki/Robots.txt
//  https://github.com/google/robotstxt-java
//  https://developers.google.com/search/docs/crawling-indexing/robots/robots_txt
public abstract class Scraper {
    private final GroceryDadConfig.Scraper config;
    private final WebDriver driver;
    private final ProductAPIClient client;

    protected Scraper(GroceryDadConfig.Scraper config, WebDriver driver, ProductAPIClient client) {
        this.config = config;
        this.driver = driver;
        this.client = client;
    }

    protected final void sleep() {
        Selenide.sleep((long) (config.sleepDelay.toMillis() * (1 + Math.random())));
    }

    public final void scrap() {
        Configuration.timeout = config.timeout.toMillis();
        using(driver, () -> {
            open(config.uri);
            waitUntilPageLoads();
            acceptOrRejectCookies();
            switchToEnglish();
            var categoryTree = buildCategoryTree();
            categoryTree.print();
            config.categories.forEach(categories -> {
                FileCache<Product> cache = cache(categories); // todo: do we need cache at all?
                scrap(categories, product -> cache.save(product.code, product));
                cache.list().forEach(client::update);
            });
        });
    }

    protected abstract void acceptOrRejectCookies();

    protected abstract void switchToEnglish();

    protected abstract CategoryTree buildCategoryTree();

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

    public static Scraper create(GroceryDadConfig.Scraper config, WebDriver driver, ProductAPIClient client) {
        return switch (config.namespace) {
            case Namespace.BARBORA -> new BarboraScraper(config, driver, client);
            case Namespace.PRISMA -> new PrismaScraper(config, driver, client);
            case Namespace.RIMI -> new RimiScraper(config, driver, client);
            default -> throw new UnsupportedOperationException(format("Unrecognized namespace '%s'", config.namespace));
        };
    }
}