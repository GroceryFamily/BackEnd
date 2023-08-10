package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.page.Page;
import GroceryFamily.GroceryDad.scraper.tree.CategoryPermissionTree;
import GroceryFamily.GroceryDad.scraper.tree.CategoryTreePath;
import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.Configuration;
import lombok.experimental.SuperBuilder;
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
@SuperBuilder()
public abstract class Scraper {
    private final GroceryDadConfig.Scraper config;
    private final WebDriver driver;
    private final ProductAPIClient client;
    private final CategoryPermissionTree categoryPermissions;

    public final void scrap() {
        Configuration.timeout = config.timeout.toMillis();
        Page.sleepDelay = config.sleepDelay;
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

    protected abstract void scrap(Consumer<Product> handler);

    protected final boolean categoryAllowed(CategoryTreePath path) {
        return categoryPermissions.allowed(path);
    }

    protected final boolean categoryAllowed(List<String> path) {
        return categoryPermissions.allowed(path);
    }

    // todo: remove
    @Deprecated
    protected void scrap(List<String> categories, Consumer<Product> handler) {
        throw new UnsupportedOperationException("Method not supported");
    }

    void waitUntilPageLoads() {
        new WebDriverWait(driver, config.timeout).until(Scraper::pageIsReady);
    }

    private static boolean pageIsReady(WebDriver driver) {
        return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
    }

    public static Scraper create(GroceryDadConfig.Scraper config, WebDriver driver, ProductAPIClient client) {
        return builder(config)
                .config(config)
                .driver(driver)
                .client(client)
                .categoryPermissions(buildCategoryPermissionTree(config))
                .build();
    }

    private static ScraperBuilder<?, ?> builder(GroceryDadConfig.Scraper config) {
        return switch (config.namespace) {
            case Namespace.BARBORA -> BarboraScraper.builder();
            case Namespace.PRISMA -> PrismaScraper.builder();
            case Namespace.RIMI -> RimiScraper.builder();
            default -> throw new UnsupportedOperationException(format("Unrecognized namespace '%s'", config.namespace));
        };
    }

    private static CategoryPermissionTree buildCategoryPermissionTree(GroceryDadConfig.Scraper config) {
        var tree = new CategoryPermissionTree();
        config.categories.forEach(tree::add);
        return tree;
    }
}