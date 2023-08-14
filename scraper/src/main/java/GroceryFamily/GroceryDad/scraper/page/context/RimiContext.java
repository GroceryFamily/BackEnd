package GroceryFamily.GroceryDad.scraper.page.context;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.cache.Cache;
import GroceryFamily.GroceryDad.scraper.page.Context;
import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.page.Path;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static GroceryFamily.GroceryDad.scraper.page.Page.html;
import static GroceryFamily.GroceryDad.scraper.page.Page.number;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

public class RimiContext extends Context {
    private boolean initialized;

    public RimiContext(GroceryDadConfig.Scraper config) {
        super(config);
    }

    @Override
    protected void waitUntilReady() {
        if (initialized) return;
        acceptOrRejectCookies();
        initialized = true;
    }

    @Override
    protected Stream<Link> categoryLinks(Document document, Link selected) {
        var buttons = document.select("nav[data-category-menu-container] button");
        return Stream.concat(
                buttons.stream().map(button -> Link
                        .builder()
                        .codePath(categoryCodePath(link(document, button)))
                        .name(button.text())
                        .url(link(document, button).absUrl("href"))
                        .build()),
                buttons.stream().flatMap(button -> submenuLinks(document, button).stream().map(link -> Link
                        .builder()
                        .codePath(categoryCodePath(link))
                        .name(link.text())
                        .url(link.absUrl("href"))
                        .build()))
        );
    }

    private static Path<String> categoryCodePath(Element e) {
        var fragments = e.attr("href").split("/");
        return Path.of(List.of(fragments).subList(4, fragments.length - 2));
    }

    private Element link(Document document, Element button) {
        return submenu(document, button).select("a[class*=base-category-link]").first();
    }

    private Elements submenuLinks(Document document, Element button) {
        return submenu(document, button).select("a").not("[class*=base-category-link]");
    }

    private Element submenu(Document document, Element button) {
        return document.select("*[data-index=" + button.attr("data-target-descendant") + "]").first();
    }

    @Override
    public Stream<Product> loadProducts(Path<String> categoryPath, Link selected) {
        return productLinkPages(categoryPath, selected.url)
                .stream()
                .flatMap(Collection::stream)
                .map(link -> loadProduct(link, categoryPath));
    }

    private Product loadProduct(Link link, Path<String> categoryPath) {
        var cache = productsCache(categoryPath);
        var cacheId = link.code();
        var html = cache.load(cacheId);
        if (html == null) {
            html = _open(link);
            cache.save(cacheId, html);
        }
        var document = Jsoup.parse(html, link.url);
        return Product
                .builder()
                .namespace(Namespace.RIMI)
                .code(substringAfterLast(link.url, "/"))
                .name(document.select("h3[class=name]").text())
                // todo: set prices and categories
                .build();
    }

    private List<List<Link>> productLinkPages(Path<String> categoryPath, String url) {
        var cache = cache(categoryPath);
        var cacheId = categoryPath.tail();
        var html = cache.load(cacheId);
        var document = Jsoup.parse(html, url);
        var pages = new ArrayList<List<Link>>();
        pages.add(productLinkPage(document));
        while (nextProductPageExists(document)) {
            cacheId = format("%s-%s", categoryPath.tail(), selectedProductPageNumber(document) + 1);
            html = cache.load(cacheId);
            if (html == null) {
                html = nextProductPage(document);
                cache.save(cacheId, html);
            }
            document = Jsoup.parse(html, url);
            pages.add(productLinkPage(document));
        }
        return pages;
    }

    private static List<Link> productLinkPage(Document document) {
        return productPageElements(document).map(RimiContext::productLink).toList();
    }

    private static Stream<Element> productPageElements(Document document) {
        return productPageElement(document).select("div[data-product-code]").stream();
    }

    private static Element productPageElement(Document document) {
        return document.select("*[class*=product-grid]").first();
    }

    private static Link productLink(Element e) {
        return Link
                .builder()
                .codePath(Path.<String>empty().followedBy(e.attr("data-product-code")))
                .name(e.select("*[class=card__name]").text())
                .url(requireNonNull(e.select("a").first()).absUrl("href"))
                .build();
    }

    private static String productLinkCode(Element e) {
        return e.select("div[data-b-item-id]").attr("data-b-item-id");
    }

    static boolean nextProductPageExists(Document document) {
        if (productPageNumbersElement(document) == null) return false;
        var selectedPageNumber = selectedProductPageNumber(document);
        return productPageNumberElement(document, selectedPageNumber + 1) != null;
    }

    String nextProductPage(Document document) {
        var nextPageNumber = selectedProductPageNumber(document) + 1;
        var nextPageUrl = productPageNumberElement(document, nextPageNumber).select("a").first().absUrl("href");
        _open(Link.builder().url(nextPageUrl).build());
        return html();
    }

    static SelenideElement productPageNumberElement(int pageNumber) {
        return productPageNumberElements().findBy(number(pageNumber));
    }

    static int selectedProductPageNumber(Document document) {
        return Integer.parseInt(selectedProductPageNumberElement(document).text());
    }

    static Element selectedProductPageNumberElement(Document document) {
        return productPageNumberElements(document).filter(e -> e.select("*").hasClass("active")).findFirst().orElseThrow();
    }

    static int selectedProductPageNumber() {
        return Integer.parseInt(productPageNumberElements().findBy(cssClass("active")).text());
    }

    static Element productPageNumberElement(Document document, int pageNumber) {
        return productPageNumbersElement(document).select("li:matches(" + pageNumber + ")").first();
    }

    static Stream<Element> productPageNumberElements(Document document) {
        var pageNumbers = productPageNumbersElement(document);
        if (pageNumbers == null) return Stream.empty();
        return pageNumbers.select("li:matches([0-9]+)").stream();
    }

    static Element productPageNumbersElement(Document document) {
        return document.select("*[class*=pagination] ul").first();
    }

    static ElementsCollection productPageNumberElements() {
        return $("ul[class*=pagination]").$$("li > *").filter(number());
    }

    private static void acceptOrRejectCookies() {
        $("#CybotCookiebotDialogBodyLevelButtonLevelOptinDeclineAll")
                .shouldBe(visible)
                .click();
    }
}