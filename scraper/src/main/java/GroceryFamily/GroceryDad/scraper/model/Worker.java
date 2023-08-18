package GroceryFamily.GroceryDad.scraper.model;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.cache.CacheFactory;
import GroceryFamily.GroceryDad.scraper.driver.LazyDriver;
import GroceryFamily.GroceryDad.scraper.view.ViewFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;

public class Worker {
    private final String platform;
    private final Link root;
    private final LazyDriver driver;
    private final ViewFactory viewFactory;
    private final CacheFactory cacheFactory;
    private final Allowlist allowlist = new Allowlist();
    private final Listener listener;
    private final Set<Path<String>> seen = new HashSet<>();
    private final SourceTree visited = new SourceTree();

    private Worker(String platform, GroceryDadConfig.Platform config, CacheFactory cacheFactory, Listener listener) {
        this.platform = platform;
        root = Link.builder().code(config.namespace).url(config.url).build();
        driver = new LazyDriver(config.live);
        viewFactory = ViewFactory.create(config);
        this.cacheFactory = cacheFactory;
        config.allowlist.stream().map(Path::of).forEach(allowlist::put);
        this.listener = listener;
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
        listener.product(platform, productView.product(), selected);
        visited.put(selected.namePath(), selected);
    }

    private Document load(Link link) {
        var cache = cacheFactory.html(platform, link);
        var html = cache.load(link.code);
        if (html == null) {
            html = viewFactory.liveView(driver.get()).open(link);
            cache.save(link.code, html);
        }
        return Jsoup.parse(html, link.url);
    }

    public static SourceTree traverse(String platform, GroceryDadConfig.Platform config, CacheFactory cacheFactory, Listener listener) {
        var worker = new Worker(platform, config, cacheFactory, listener);
        worker.traverse();
        return worker.visited;
    }
}