package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.cache.Cache;
import GroceryFamily.GroceryDad.scraper.tree.CategoryPermissionTree;
import GroceryFamily.GroceryDad.scraper.tree.CategoryTree;
import GroceryFamily.GroceryDad.scraper.tree.CategoryTreePath;
import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import GroceryFamily.GroceryElders.domain.Category;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import io.github.antivoland.sfc.FileCache;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Stack;
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
    private final CategoryPermissionTree categoryPermissions;

    protected Scraper(GroceryDadConfig.Scraper config, WebDriver driver, ProductAPIClient client) {
        this.config = config;
        this.driver = driver;
        this.client = client;
        this.categoryPermissions = buildCategoryPermissionTree(config);
    }

    protected final void sleep() {
        Selenide.sleep((long) (config.sleepDelay.toMillis() * (1 + Math.random())));
    }

    protected final boolean categoryAllowed(CategoryTreePath path) {
        return categoryPermissions.allowed(path);
    }

    public final void scrap() {
        Configuration.timeout = config.timeout.toMillis();
        using(driver, () -> {
            open(config.uri);
            waitUntilPageLoads();
            acceptOrRejectCookies();
            switchToEnglish();
            /* todo: remove
            var categoryTree = buildCategoryTree();
            categoryTree.print();
             */
            scrap(client::update);
            /* todo: remove
            config.categories.forEach(categories -> {
                FileCache<Product> cache = cache(categories); // todo: do we need cache at all?
                scrap(categories, product -> cache.save(product.code, product));
                cache.list().forEach(client::update);
            });
             */
        });
    }

    protected abstract void acceptOrRejectCookies();

    protected abstract void switchToEnglish();

    // todo: remove
    protected CategoryTree buildCategoryTree() {
        throw new UnsupportedOperationException("Method not supported");
    }

    ;

    protected abstract void scrap(Consumer<Product> handler);

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

    private static CategoryPermissionTree buildCategoryPermissionTree(GroceryDadConfig.Scraper config) {
        var tree = new CategoryPermissionTree();
        config.categories.forEach(tree::add);
        return tree;
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