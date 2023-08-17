package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.cache.CacheFactory;
import GroceryFamily.GroceryDad.scraper.driver.LazyDriver;
import GroceryFamily.GroceryDad.scraper.model.Allowlist;
import GroceryFamily.GroceryDad.scraper.model.Link;
import GroceryFamily.GroceryDad.scraper.model.Path;
import GroceryFamily.GroceryDad.scraper.model.Source;
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
    private final GroceryDadConfig.Scraper config;
    private final ViewFactory viewFactory;
    private final CacheFactory cacheFactory;
    private final Allowlist allowlist;
    private final LazyDriver driver;

    public Scraper(GroceryDadConfig.Scraper config) {
        this.config = config;
        this.viewFactory = ViewFactory.create(config);
        this.cacheFactory = new CacheFactory(config.cache);
        this.allowlist = allowlist(config);
        this.driver = new LazyDriver(config.live);
    }

    public void scrap(Consumer<Product> handler) {
        traverse(Link.builder().code(config.namespace).url(config.url).build(), new HashSet<>(), handler);
    }

    private void traverse(Link selected, Set<Path<String>> seen, Consumer<Product> handler) {
        if (seen.contains(selected.codePath())) return;
        seen.add(selected.codePath());
        if (!allowlist.allowed(selected.namePath())) return;
        log.info("{}: {}", config.namespace, selected.namePath());

        var document = load(selected); // todo: flexible delays based on a platform response latency

        var categoryView = viewFactory.categoryView(document, Source.category(selected));
        var childCategoryLinks = categoryView.childCategoryLinks();
        if (!childCategoryLinks.isEmpty()) {
            childCategoryLinks.forEach(link -> traverse(link, seen, handler));
            return;
        }

        var productListView = viewFactory.productListView(document, Source.productList(selected));
        productListView.productPageLinks().forEach(link -> traverse(link, seen, handler));

        var productLinks = productListView.productLinks();
        if (!productLinks.isEmpty()) {
            productLinks.forEach(link -> traverse(link, seen, handler));
            return;
        }

        var productView = viewFactory.productView(document, Source.product(selected));
        handler.accept(productView.product());
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

    public void destroy() {
        driver.destroy();
    }

    private static Allowlist allowlist(GroceryDadConfig.Scraper config) {
        var allowlist = new Allowlist();
        config.allowlist.stream().map(Path::of).forEach(allowlist::put);
        return allowlist;
    }
}