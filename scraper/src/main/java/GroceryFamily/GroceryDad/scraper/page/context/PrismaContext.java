package GroceryFamily.GroceryDad.scraper.page.context;

import GroceryFamily.GroceryDad.GroceryDadConfig;
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

import java.util.stream.Stream;

import static GroceryFamily.GroceryDad.scraper.page.Page.html;
import static GroceryFamily.GroceryDad.scraper.page.Page.scrollDown;
import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

public class PrismaContext extends Context {
    private boolean initialized;

    public PrismaContext(GroceryDadConfig.Scraper config) {
        super(config);
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
        return Stream.concat(topCategoryLinks(document), leftCategoryLinks(document, selected));
    }

    private static Stream<Link> topCategoryLinks(Document document) {
        return document
                .select("#main-navigation a[href*=selection]")
                .stream()
                .filter(Element::hasText)
                .map(PrismaContext::topCategoryLink);
    }

    private static Link topCategoryLink(Element e) {
        return Link
                .builder()
                .codePath(Path.<String>empty().followedBy(topCategoryLinkCode(e)))
                .name(e.text())
                .url(e.absUrl("href"))
                .build();
    }

    private static String topCategoryLinkCode(Element e) {
        return substringAfterLast(substringBeforeLast(e.attr("href"), "/"), "/");
    }

    private static Stream<Link> leftCategoryLinks(Document document, Link selected) {
        return document
                .select("*[role=navigation] a[data-category-id]")
                .stream()
                .filter(Element::hasText)
                .map(e -> leftCategoryLink(e, selected));
    }

    private static Link leftCategoryLink(Element e, Link selected) {
        return Link
                .builder()
                .codePath(selected.codePath.followedBy(leftCategoryLinkCode(e)))
                .name(e.text())
                .url(e.absUrl("href"))
                .build();
    }

    private static String leftCategoryLinkCode(Element e) {
        return substringAfterLast(e.attr("href"), "/");
    }

    @Override
    public Stream<Product> loadProducts(Path<String> categoryPath, Link selected) {
        var cache = cache(categoryPath);
        var cacheId = categoryPath.tail();
        var html = cache.load(cacheId);
        var document = Jsoup.parse(html, selected.url);
        if (visibleProductElementCount(document) < totalProductElementCount(document)) {
            open(selected);
            var totalCount = Integer.parseInt(productCountElement().text());
            var count = visibleProductElementCount();
            while (count < totalCount) {
                scrollDown();
                visibleProductElements().shouldHave(sizeGreaterThan(count));
                count = visibleProductElementCount();
            }
            html = html();
            document = Jsoup.parse(html, selected.url);
            cache.save(cacheId, html);
        }
        return productLinks(document).map(link -> loadProduct(link, categoryPath));
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
                .namespace(Namespace.PRISMA)
                .code(substringAfterLast(link.url, "/"))
                .name(document.select("#product-name").text())
                // todo: set prices and categories
                .build();
    }

    static Stream<Link> productLinks(Document document) {
        return document.select("li[data-ean]").stream().map(PrismaContext::productLink);
    }

    private static Link productLink(Element e) {
        var code = substringAfterLast(e.select("a").attr("href"), "/");
        return Link
                .builder()
                .codePath(Path.<String>empty().followedBy(code))
                .name(e.select("*[class='name']").text())
                .url(requireNonNull(e.select("a").first()).absUrl("href"))
                .build();
    }

    private static SelenideElement productCountElement() {
        return $("*[class*='category-items'] b");
    }

    private static Element productCountElement(Document document) {
        return document.select("*[class*='category-items'] b").first();
    }

    private static int visibleProductElementCount() {
        return (int) visibleProductElements().asFixedIterable().stream().count();
    }

    private static int totalProductElementCount(Document document) {
        return Integer.parseInt(productCountElement(document).text());
    }

    private static int visibleProductElementCount(Document document) {
        return (int) visibleProductElements(document).count();
    }

    private static ElementsCollection visibleProductElements() {
        return $$("li[data-ean]");
    }

    private static Stream<Element> visibleProductElements(Document document) {
        return document.select("li[data-ean]").stream();
    }

    private static void acceptOrRejectCookies() {
        $("*[class*='js-cookie-notice'] *[class='close-icon']").shouldBe(visible).click();
    }

    private static void switchToEnglish() {
        $("*[data-language='en']").shouldBe(visible).click();
        topCategoryElements().shouldHave(itemWithText("Groceries"));
    }

    private static ElementsCollection topCategoryElements() {
        return $$("*[id='main-navigation'] a[href*='selection']");
    }
}