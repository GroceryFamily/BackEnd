package GroceryFamily.GroceryDad.scraper.page;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.cache.Cache;
import GroceryFamily.GroceryDad.scraper.page.view.CategoryView;
import GroceryFamily.GroceryDad.scraper.page.view.ProductListView;
import GroceryFamily.GroceryDad.scraper.page.view.ProductView;
import GroceryFamily.GroceryDad.scraper.tree.PermissionTree;
import GroceryFamily.GroceryElders.domain.Category;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.github.antivoland.sfc.FileCache;
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
import java.util.stream.Stream;

import static GroceryFamily.GroceryDad.scraper.page.PageUtils.html;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static java.util.Comparator.comparing;

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

    @Deprecated
    public final boolean canOpen(Path<String> namePath) {
        return permissions.allowed(namePath.segments());
    }

    public final boolean canOpen(Link link) {
        return permissions.allowed(link.namePath());
    }

    @Deprecated
    public final FileCache<String> cache(Path<String> categoryPath) {
        return cacheFactory.html(categoryPath.segments());
    }

    @Deprecated
    public final FileCache<String> productsCache(Path<String> categoryPath) {
        var path = categoryPath.followedBy("products");
        return cacheFactory.html(path.segments());
    }

    @Deprecated
    public final String _open(Link link) {
        open(link.url);
        waitUntilPageReady();
        initialize();
        return html();
    }

    public final void traverse(Consumer<Product> handler) {
        traverse(Link.builder().code(config.namespace).url(config.url).build(), new HashSet<>(), handler);
    }

    public final void traverse(Link selected, Set<String> seen, Consumer<Product> handler) {
        if (seen.contains(selected.url)) return;
        seen.add(selected.url);
        if (!canOpen(selected)) return;
        log.info("Traversing {}...", selected.namePath());

        var document = load(selected);

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

    @Deprecated
    public SourceType type(Document document) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public final List<Link> childCategoryLinks(Document document, Source selected) {
        var selectedCodePath = selected.codePath();
        var categories = categories(document, selected);
        return categories.keySet().stream()
                .filter(codePath -> codePath.contains(selectedCodePath))
                .filter(codePath -> codePath.size() - selectedCodePath.size() == 1)
                .map(codePath -> Link.category(categories.get(codePath), selected))
                .toList();
    }

    protected Map<Path<String>, Category> categories(Document document, Source selected) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Link> productPageLinks(Document document, Source selected) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Link> productLinks(Document document, Source selected) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Product product(Document document, Source selected) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Deprecated
    protected CategoryView categoryView(Document document, Link selected) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Deprecated
    protected ProductListView productListView(Document document, Link selected) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Deprecated
    protected ProductView productView(Document document, Link selected) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public final Document load(Link link) {
        var cache = cacheFactory.html(link);
        var html = cache.load(link.code);
        if (html == null) {
            Selenide.open(link.url);
            waitUntilPageReady();
            initialize();
            html = $("html").innerHtml();
            cache.save(link.code, html);
        }
        return Jsoup.parse(html, link.url);
    }

    protected abstract void initialize();

    @Deprecated
    public final Stream<Link> childCategoryLinksSortedByCodePathSize(Document document, Link selected) {
        return categoryLinks(document, selected)
                .filter(link -> link.codePath.contains(selected.codePath))
                .filter(link -> !link.codePath.equals(selected.codePath))
                .sorted(comparing(link -> link.codePath.size()));
    }

    @Deprecated
    protected Stream<Link> categoryLinks(Document document, Link selected) {
        throw new UnsupportedOperationException("Method not supported");
    }

    @Deprecated
    public Stream<Product> loadProducts(Path<String> categoryPath, Link selected) {
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