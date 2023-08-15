package GroceryFamily.GroceryDad.scraper.page;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.cache.Cache;
import GroceryFamily.GroceryDad.scraper.tree.PermissionTree;
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
public abstract class Context {
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

    public final void traverse(Link selected, Set<String> seen, Consumer<Product> handler) {
        if (seen.contains(selected.url)) return;
        seen.add(selected.url);
        if (!canOpen(selected)) return;
        log.info("Traversing {}...", selected.namePath());

        var document = load(selected); // todo: flexible delays based on a platform response latency

        var childCategoryLinks = childCategoryLinks(document, Source.category(selected));
        if (!childCategoryLinks.isEmpty()) {
            childCategoryLinks.forEach(link -> traverse(link, seen, handler));
            return;
        }

        var productLinks = productLinks(document, Source.productList(selected));
        if (!productLinks.isEmpty()) {
            productLinks.forEach(link -> traverse(link, seen, handler));
            return;
        }

        productPageLinks(document, Source.productList(selected)).forEach(link -> traverse(link, seen, handler));

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

    public final List<Link> childCategoryLinks(Document document, Source selected) {
        var selectedCodePath = selected.codePath();
        var categories = categories(document, selected);
        return categories.keySet().stream()
                .filter(codePath -> codePath.contains(selectedCodePath))
                .filter(codePath -> codePath.size() - selectedCodePath.size() == 1)
                .map(codePath -> Link.category(categories.get(codePath), selected))
                .toList();
    }

    protected abstract Map<Path<String>, Category> categories(Document document, Source selected);

    public abstract List<Link> productPageLinks(Document document, Source selected);

    public abstract List<Link> productLinks(Document document, Source selected);

    public abstract Product product(Document document, Source selected);

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