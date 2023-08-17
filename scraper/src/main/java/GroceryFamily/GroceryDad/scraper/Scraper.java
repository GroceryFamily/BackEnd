package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.cache.Cache;
import GroceryFamily.GroceryDad.scraper.driver.LazyDriver;
import GroceryFamily.GroceryDad.scraper.model.Allowlist;
import GroceryFamily.GroceryDad.scraper.model.Link;
import GroceryFamily.GroceryDad.scraper.model.Path;
import GroceryFamily.GroceryDad.scraper.model.Source;
import GroceryFamily.GroceryDad.scraper.page.PageUtils;
import GroceryFamily.GroceryDad.scraper.view.ViewFactory;
import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.Configuration;
import lombok.Builder;
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
    private final ProductAPIClient client;
    private final ViewFactory viewFactory;
    private final Cache.Factory cacheFactory;
    private final Allowlist allowlist;
    private final LazyDriver driver;

    @Builder()
    public Scraper(GroceryDadConfig.Scraper config, ProductAPIClient client) {
        this.config = config;
        this.client = client;
        this.viewFactory = ViewFactory.get(config.namespace);
        this.cacheFactory = Cache.factory(config.cache.directory);
        this.allowlist = allowlist(config);
        this.driver = new LazyDriver(config); // todo: destroy
    }

    public void scrap() {
        Configuration.timeout = config.timeout.toMillis();
        PageUtils.sleepDelay = config.sleepDelay;
        traverse(client::update);
    }

    private void traverse(Consumer<Product> handler) {
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
            html = viewFactory.liveView(driver.get()).open(link, config.timeout);
            cache.save(link.code, html);
        }
        return Jsoup.parse(html, link.url);
    }

    private static Allowlist allowlist(GroceryDadConfig.Scraper config) {
        var allowlist = new Allowlist();
        config.categories.stream().map(Path::of).forEach(allowlist::put);
        return allowlist;
    }
}