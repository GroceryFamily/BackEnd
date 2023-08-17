package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.cache.CacheFactory;
import GroceryFamily.GroceryDad.scraper.driver.LazyDriver;
import GroceryFamily.GroceryDad.scraper.model.*;
import GroceryFamily.GroceryDad.scraper.view.ViewFactory;
import GroceryFamily.GroceryElders.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

// todo: think about robots.txt
//  https://en.wikipedia.org/wiki/Robots.txt
//  https://github.com/google/robotstxt-java
//  https://developers.google.com/search/docs/crawling-indexing/robots/robots_txt
@Slf4j
public class Scraper {
    private final Link root;
    private final LazyDriver driver;
    private final ViewFactory viewFactory;
    private final CacheFactory cacheFactory;
    private final Allowlist allowlist = new Allowlist();
    private final Set<Path<String>> seen = new HashSet<>();
    private final SourceTree visited = new SourceTree();

    private Scraper(GroceryDadConfig.Scraper config) {
        root = Link.builder().code(config.namespace).url(config.url).build();
        driver = new LazyDriver(config.live);
        viewFactory = ViewFactory.create(config);
        cacheFactory = new CacheFactory(config.cache);
        config.allowlist.stream().map(Path::of).forEach(allowlist::put);
    }

    private void traverse(Consumer<Product> handler) {
        try {
            traverse(root, handler);
        } finally {
            driver.destroy();
        }
    }

    private void traverse(Link selected, Consumer<Product> handler) {
        if (seen.contains(selected.codePath())) return;
        seen.add(selected.codePath());
        if (!allowlist.allowed(selected.namePath())) return;

        var document = load(selected); // todo: flexible delays based on a platform response latency

        var categoryView = viewFactory.categoryView(document, Source.category(selected));
        var childCategoryLinks = categoryView.childCategoryLinks();
        if (!childCategoryLinks.isEmpty()) {
            childCategoryLinks.forEach(link -> traverse(link, handler));
            visited.put(selected.namePath(), Source.category(selected));
            return;
        }

        var productListView = viewFactory.productListView(document, Source.productList(selected));
        productListView.productPageLinks().forEach(link -> traverse(link, handler));

        var productLinks = productListView.productLinks();
        if (!productLinks.isEmpty()) {
            productLinks.forEach(link -> traverse(link, handler));
            visited.put(selected.namePath(), Source.productList(selected));
            return;
        }

        var productView = viewFactory.productView(document, Source.product(selected));
        handler.accept(productView.product());
        visited.put(selected.namePath(), Source.product(selected));
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

    public static SourceTree scrap(GroceryDadConfig.Scraper config, Consumer<Product> handler) {
        var scraper = new Scraper(config);
        scraper.traverse(handler);
        return scraper.visited;
    }
}