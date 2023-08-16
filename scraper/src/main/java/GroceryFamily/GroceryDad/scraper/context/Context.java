package GroceryFamily.GroceryDad.scraper.context;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.cache.Cache;
import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.page.Path;
import GroceryFamily.GroceryDad.scraper.page.Source;
import GroceryFamily.GroceryDad.scraper.tree.PermissionTree;
import GroceryFamily.GroceryDad.scraper.view.ViewFactory;
import GroceryFamily.GroceryElders.domain.Category;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
public abstract class Context { // todo: live vs. cached?
    private final GroceryDadConfig.Scraper config;
    private final Cache.Factory cacheFactory;
    private final PermissionTree permissions;

    public Context(GroceryDadConfig.Scraper config) {
        this.config = config;
        this.cacheFactory = Cache.factory(config.cache.directory);
        this.permissions = buildCategoryPermissionTree(config);
    }

    public final boolean canOpen(Link link) {
        return permissions.allowed(link.namePath());
    }

    public final void traverse(Consumer<Product> handler) {
        traverse(Link.builder().code(config.namespace).url(config.url).build(), new HashSet<>(), handler);
    }

    public final void traverse(Link selected, Set<Path<String>> seen, Consumer<Product> handler) {
        if (seen.contains(selected.codePath())) return;
        seen.add(selected.codePath());
        if (!canOpen(selected)) return;
        log.info("{}: {}", config.namespace, selected.namePath());

        var document = load(selected); // todo: flexible delays based on a platform response latency

        var childCategoryLinks = childCategoryLinks(document, Source.category(selected));
        if (!childCategoryLinks.isEmpty()) {
            childCategoryLinks.forEach(link -> traverse(link, seen, handler));
            return;
        }

        productPageLinks(document, Source.productList(selected)).forEach(link -> traverse(link, seen, handler));

        var productLinks = productLinks(document, Source.productList(selected));
        if (!productLinks.isEmpty()) {
            productLinks.forEach(link -> traverse(link, seen, handler));
            return;
        }

        handler.accept(product(document, Source.product(selected)));
    }

    public final Document load(Link link) {
        var cache = cacheFactory.html(link);
        var html = cache.load(link.code);
        if (html == null) {
            Selenide.open(link.url); // todo: run web driver when it actually needed
            waitUntilPageReady();
            initialize();
            html = $("html").innerHtml();
            cache.save(link.code, html);
        }
        return Jsoup.parse(html, link.url);
    }

    protected abstract void initialize();

    public List<Link> childCategoryLinks(Document document, Source selected) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Deprecated
    protected Map<Path<String>, Category> categories(Document document, Source selected) {
        throw new UnsupportedOperationException("Method not supported");
    }

    public List<Link> productPageLinks(Document document, Source selected) {
        throw new UnsupportedOperationException("Method not supported");
    }

    public List<Link> productLinks(Document document, Source selected) {
        throw new UnsupportedOperationException("Method not supported");
    }

    public Product product(Document document, Source selected) {
        throw new UnsupportedOperationException("Method not supported");
    }

    private static PermissionTree buildCategoryPermissionTree(GroceryDadConfig.Scraper config) {
        var tree = new PermissionTree();
        config.categories.forEach(tree::add);
        return tree;
    }

    private static void waitUntilPageReady() {
        var driver = WebDriverRunner.getWebDriver();
        var timeout = Duration.ofMillis(Configuration.timeout);
        new WebDriverWait(driver, timeout).until(Context::pageIsReady);
    }

    private static boolean pageIsReady(WebDriver driver) {
        return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
    }
}