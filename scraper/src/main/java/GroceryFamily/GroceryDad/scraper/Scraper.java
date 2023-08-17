package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.cache.Cache;
import GroceryFamily.GroceryDad.scraper.model.*;
import GroceryFamily.GroceryDad.scraper.page.PageUtils;
import GroceryFamily.GroceryDad.scraper.view.ViewFactory;
import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.using;

// todo: think about robots.txt
//  https://en.wikipedia.org/wiki/Robots.txt
//  https://github.com/google/robotstxt-java
//  https://developers.google.com/search/docs/crawling-indexing/robots/robots_txt
@Slf4j
public class Scraper {
    private final GroceryDadConfig.Scraper config;
    private final WebDriver driver;
    private final ProductAPIClient client;
    private final ViewFactory viewFactory;
    private final Cache.Factory cacheFactory;
    private final Allowlist allowlist;

    @Builder()
    public Scraper(GroceryDadConfig.Scraper config, WebDriver driver, ProductAPIClient client) {
        this.config = config;
        this.driver = driver;
        this.client = client;
        this.viewFactory = ViewFactory.get(config.namespace);
        this.cacheFactory = Cache.factory(config.cache.directory);
        this.allowlist = allowlist(config);
    }

    public void scrap() {
        Configuration.timeout = config.timeout.toMillis();
        PageUtils.sleepDelay = config.sleepDelay;
        using(driver, () -> traverse(client::update));
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
            Selenide.open(link.url); // todo: run web driver when it actually needed
            waitUntilPageReady();
            viewFactory.liveView().initialize();
            html = $("html").innerHtml();
            cache.save(link.code, html);
        }
        return Jsoup.parse(html, link.url);
    }

    private static Allowlist allowlist(GroceryDadConfig.Scraper config) {
        var allowlist = new Allowlist();
        config.categories.stream().map(Path::of).forEach(allowlist::put);
        return allowlist;
    }

    // todo: move to LiveView
    private static void waitUntilPageReady() {
        var driver = WebDriverRunner.getWebDriver();
        var timeout = Duration.ofMillis(Configuration.timeout);
        new WebDriverWait(driver, timeout).until(Scraper::pageIsReady);
    }

    private static boolean pageIsReady(WebDriver driver) {
        return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
    }
}