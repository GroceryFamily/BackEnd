package GroceryFamily.GroceryDad.scraper.page.context;

import GroceryFamily.GroceryDad.scraper.cache.Cache;
import GroceryFamily.GroceryDad.scraper.page.Context;
import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.tree.PermissionTree;
import GroceryFamily.GroceryDad.scraper.view.Path;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static GroceryFamily.GroceryDad.scraper.page.Page.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

public class BarboraContext extends Context {
    private boolean initialized;

    public BarboraContext(Cache.Factory cacheFactory, PermissionTree permissions) {
        super(cacheFactory, permissions);
    }

    @Override
    protected void waitUntilReady() {
        if (initialized) return;
        acceptOrRejectCookies();
        switchToEnglish();
        initialized = true;
    }

    @Override
    protected Stream<Link> categoryLinks(Document document, Link selected) {
        return document
                .select("a[class*=category]")
                .stream()
                .filter(Element::hasText)
                .map(BarboraContext::categoryLink);
    }

    private static Link categoryLink(Element e) {
        return Link
                .builder()
                .codePath(categoryLinkCodePath(e))
                .name(e.text())
                .url(e.absUrl("href"))
                .build();
    }

    private static Path<String> categoryLinkCodePath(Element e) {
        return Path.of(substringAfter(e.attr("href"), "/").split("/"));
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
            html = open(link);
            cache.save(cacheId, html);
        }
        var document = Jsoup.parse(html, link.url);
        return Product
                .builder()
                .namespace(Namespace.BARBORA)
                .code(substringAfterLast(link.url, "/"))
                .name(document.select("*[class=b-product-info--title]").text())
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
                html = nextProductPage();
                cache.save(cacheId, html);
            }
            document = Jsoup.parse(html, url);
            pages.add(productLinkPage(document));
        }
        return pages;
    }

    private static List<Link> productLinkPage(Document document) {
        return productPageElements(document).map(BarboraContext::productLink).toList();
    }

    private static Stream<Element> productPageElements(Document document) {
        return productPageElement(document).select("*[itemtype*=Product]").stream();
    }

    private static Element productPageElement(Document document) {
        return document.select("*[class*=products-list]").first();
    }

    private static Link productLink(Element e) {
        return Link
                .builder()
                .codePath(Path.<String>empty().followedBy(productLinkCode(e)))
                .name(e.select("*[itemprop=name]").text())
                .url(requireNonNull(e.select("a").first()).absUrl("href"))
                .build();
    }

    private static String productLinkCode(Element e) {
        return e.select("div[data-b-item-id]").attr("data-b-item-id");
    }

    static boolean nextProductPageExists(Document document) {
        var selectedPageNumber = selectedProductPageNumber(document);
        return productPageNumberElement(document, selectedPageNumber + 1) != null;
    }

    static String nextProductPage() {
        var nextPageNumber = selectedProductPageNumber() + 1;
        productPageNumberElement(nextPageNumber).$("a").click();
        productPageNumberElement(nextPageNumber).shouldHave(cssClass("active"));
        return html();
    }

    static SelenideElement productPageNumberElement(int pageNumber) {
        return productPageNumberElements().findBy(number(pageNumber));
    }

    static int selectedProductPageNumber(Document document) {
        return Integer.parseInt(selectedProductPageNumberElement(document).text());
    }

    static Element selectedProductPageNumberElement(Document document) {
        return productPageNumberElements(document).filter(e -> e.hasClass("active")).findFirst().orElseThrow();
    }

    static int selectedProductPageNumber() {
        return Integer.parseInt(productPageNumberElements().findBy(cssClass("active")).text());
    }

    static Element productPageNumberElement(Document document, int pageNumber) {
        return productPageNumbersElement(document).select("li:matches(" + pageNumber + ")").first();
    }

    static Stream<Element> productPageNumberElements(Document document) {
        return productPageNumbersElement(document).select("li:matches([0-9]+)").stream();
    }

    static Element productPageNumbersElement(Document document) {
        return document.select("ul[class=pagination]").first();
    }

    static ElementsCollection productPageNumberElements() {
        return $("ul[class=pagination]").$$("li").filter(number());
    }

    private static void acceptOrRejectCookies() {
        $("#CybotCookiebotDialogBodyLevelButtonLevelOptinDeclineAll").shouldBe(visible).click();
    }

    private static void switchToEnglish() {
        topMenuItemElement("Kaubavalik").shouldBe(visible);
        languageSelectElement().shouldBe(visible).hover();
        sleep();
        englishLanguageElement().shouldBe(visible).hover();
        sleep();
        englishLanguageElement().shouldBe(visible).click();
        topMenuItemElement("Products").shouldBe(visible);
    }

    private static SelenideElement topMenuItemElement(String name) {
        return topMenuElement().$$("li[id*=fti-desktop-menu-item]").findBy(text(name));
    }

    private static SelenideElement topMenuElement() {
        return $("#desktop-menu-placeholder");
    }

    static SelenideElement englishLanguageElement() {
        return languageSelectElement().$$("li").findBy(text("English"));
    }

    static SelenideElement languageSelectElement() {
        return $("#fti-header-language-dropdown");
    }
}