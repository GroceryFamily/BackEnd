package GroceryFamily.GroceryDad.scraper.model;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.cache.CacheFactory;
import GroceryFamily.GroceryDad.scraper.driver.LazyDriver;
import GroceryFamily.GroceryDad.scraper.view.ViewFactory;
import GroceryFamily.GroceryElders.domain.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static java.lang.String.format;

public class Worker {
    private final Link root;
    private final LazyDriver driver;
    private final ViewFactory viewFactory;
    private final CacheFactory cacheFactory;
    private final Allowlist allowlist = new Allowlist();
    private final Consumer<Product> handler;
    private final Set<Path<String>> seen = new HashSet<>();
    private final SourceTree visited = new SourceTree();

    private Worker(GroceryDadConfig.Scraper config, Consumer<Product> handler) {
        root = Link.builder().code(config.namespace).url(config.url).build();
        driver = new LazyDriver(config.live);
        viewFactory = ViewFactory.create(config);
        cacheFactory = new CacheFactory(config.cache);
        config.allowlist.stream().map(Path::of).forEach(allowlist::put);
        this.handler = handler;
    }

    private void traverse() {
        try {
            traverse(root);
        } finally {
            driver.destroy();
        }
    }

    private void traverse(Link selected) {
        if (seen.contains(selected.codePath())) return;
        seen.add(selected.codePath());
        if (!allowlist.allowed(selected.namePath())) return;

        var document = load(selected); // todo: flexible delays based on a platform response latency

        if (selected.source == null) {
            handleCategory(document, Source.category(selected));
            return;
        }

        if (selected.source.type == SourceType.PRODUCT_LIST) {
            handleProduct(document, Source.product(selected));
            return;
        }

        if (selected.source.type == SourceType.CATEGORY) {
            if (handleCategory(document, Source.category(selected))) return;
            handleProductList(document, Source.productList(selected));
            return;
        }

        throw new UnsupportedOperationException(format("Source type %s is not supported", selected.source));
    }

    private boolean handleCategory(Document document, Source selected) {
        var categoryView = viewFactory.categoryView(document, selected);
        var childCategoryLinks = categoryView.childCategoryLinks();
        if (childCategoryLinks.isEmpty()) return false;
        childCategoryLinks.forEach(this::traverse);
        visited.put(selected.namePath(), selected);
        return true;
    }

    private void handleProductList(Document document, Source selected) {
        var productListView = viewFactory.productListView(document, selected);
        productListView.productLinks().forEach(this::traverse);
        productListView.productPageLinks().forEach(this::traverse);
        visited.put(selected.namePath(), selected);
    }

    private void handleProduct(Document document, Source selected) {
        var productView = viewFactory.productView(document, selected);
        handler.accept(productView.product());
        visited.put(selected.namePath(), selected);
    }

    private Document load(Link link) {
        var cache = cacheFactory.html(link);
        var html = cache.load(link.code);
        if (html == null) {
            html = viewFactory.liveView(driver.get()).open(link);
            cache.save(link.code, html);
        }
        return Jsoup.parse(html, link.url);
    }

    public static SourceTree traverse(GroceryDadConfig.Scraper config, Consumer<Product> handler) {
        var worker = new Worker(config, handler);
        worker.traverse();
        return worker.visited;
    }
}