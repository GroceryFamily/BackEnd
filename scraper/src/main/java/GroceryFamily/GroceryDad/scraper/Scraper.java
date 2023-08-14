package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.cache.Cache;
import GroceryFamily.GroceryDad.scraper.page.Context;
import GroceryFamily.GroceryDad.scraper.page.Node;
import GroceryFamily.GroceryDad.scraper.page.Page;
import GroceryFamily.GroceryDad.scraper.page.context.BarboraContext;
import GroceryFamily.GroceryDad.scraper.page.context.PrismaContext;
import GroceryFamily.GroceryDad.scraper.page.context.RimiContext;
import GroceryFamily.GroceryDad.scraper.tree.PermissionTree;
import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import lombok.Builder;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Consumer;

import static com.codeborne.selenide.Selenide.using;
import static java.lang.String.format;

// todo: think about robots.txt
//  https://en.wikipedia.org/wiki/Robots.txt
//  https://github.com/google/robotstxt-java
//  https://developers.google.com/search/docs/crawling-indexing/robots/robots_txt
@Builder()
public class Scraper {
    private final GroceryDadConfig.Scraper config;
    private final WebDriver driver;
    private final ProductAPIClient client;
    protected final PermissionTree categoryPermissions;
    final Context context;

    public final void scrap() {
        Configuration.timeout = config.timeout.toMillis();
        Page.sleepDelay = config.sleepDelay;
        using(driver, () -> {
            scrap(client::update);
        });
    }

    protected void scrap(Consumer<Product> handler) {
        Node.root(config.url, context).traverse(handler);
    }

    public static void waitUntilPageReady() {
        new WebDriverWait(WebDriverRunner.getWebDriver(), Duration.ofMillis(Configuration.timeout)).until(Scraper::pageIsReady);
    }

    private static boolean pageIsReady(WebDriver driver) {
        return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
    }

    public static Scraper create(GroceryDadConfig.Scraper config, WebDriver driver, ProductAPIClient client) {
        return Scraper
                .builder()
                .config(config)
                .driver(driver)
                .client(client)
                .categoryPermissions(buildCategoryPermissionTree(config))
                .context(context(config))
                .build();
    }

    private static Context context(GroceryDadConfig.Scraper config) {
        return switch (config.namespace) {
            case Namespace.BARBORA -> new BarboraContext(config);
            case Namespace.PRISMA -> new PrismaContext(config);
            case Namespace.RIMI -> new RimiContext(config);
            default -> throw new UnsupportedOperationException(format("Unrecognized namespace '%s'", config.namespace));
        };
    }

    private static PermissionTree buildCategoryPermissionTree(GroceryDadConfig.Scraper config) {
        var tree = new PermissionTree();
        config.categories.forEach(tree::add);
        return tree;
    }
}