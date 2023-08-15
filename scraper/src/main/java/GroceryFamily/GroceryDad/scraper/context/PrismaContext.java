package GroceryFamily.GroceryDad.scraper.context;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.page.Path;
import GroceryFamily.GroceryDad.scraper.page.Source;
import GroceryFamily.GroceryElders.domain.Category;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static GroceryFamily.GroceryDad.scraper.page.PageUtils.scrollDown;
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
    protected void initialize() {
        if (!initialized) {
            acceptOrRejectCookies();
            switchToEnglish();
            initialized = true;
        }
        if (leftCategoryElements().isEmpty() && !visibleProductListElements().isEmpty()) {
            var count = visibleProductListElements().size();
            while (count < productListSize()) {
                scrollDown();
                visibleProductListElements().shouldHave(sizeGreaterThan(count));
                count = visibleProductListElements().size();
            }
        }
    }

    @Override
    protected Map<Path<String>, Category> categories(Document document, Source selected) {
        var categories = new HashMap<Path<String>, Category>();
        topCategories(document).forEach(category -> {
            var codePath = selected.root().codePath().followedBy(category.code);
            categories.put(codePath, category);
        });

        leftCategories(document).forEach(category -> {
            var codePath = selected.codePath().followedBy(category.code);
            categories.put(codePath, category);
        });
        return categories;
    }

    @Override
    public List<Link> productPageLinks(Document document, Source selected) {
        return List.of();
    }

    @Override
    public List<Link> productLinks(Document document, Source selected) {
        return productListElements(document)
                .map(e -> {
                    var url = requireNonNull(e.select("a").first()).absUrl("href");
                    return Link
                            .builder()
                            .code(productCode(url))
                            .name(e.select("*[class=name]").text())
                            .url(url)
                            .source(selected)
                            .build();
                })
                .toList();
    }

    @Override
    public Product product(Document document, Source selected) {
        return Product
                .builder()
                .namespace(Namespace.PRISMA)
                .code(productCode(selected.url))
                .name(document.select("#product-name").text())
                .url(selected.url)
                // todo: set prices and categories
                .build();
    }

    private static void acceptOrRejectCookies() {
        $("*[class*=cookie-notice] *[class=close-icon]").shouldBe(visible).click();
    }

    private static void switchToEnglish() {
        $("*[data-language=en]").shouldBe(visible).click();
        topCategoryElements().shouldHave(itemWithText("Groceries"));
    }

    private static ElementsCollection topCategoryElements() {
        return $$("#main-navigation a[href*=selection]");
    }

    private static ElementsCollection leftCategoryElements() {
        return $$("*[role=navigation] a[data-category-id]");
    }

    private static ElementsCollection visibleProductListElements() {
        return $$("li[data-ean]");
    }

    private static int productListSize() {
        return Integer.parseInt(productListSizeElement().text());
    }

    private static SelenideElement productListSizeElement() {
        return $("*[class*=category-items] b");
    }

    private static Stream<Category> topCategories(Document document) {
        return topCategoryElements(document)
                .map(e -> {
                    var url = e.absUrl("href");
                    return Category
                            .builder()
                            .code(substringAfterLast(substringBeforeLast(url, "/"), "/"))
                            .name(e.text())
                            .url(url)
                            .build();
                });
    }

    private static Stream<Element> topCategoryElements(Document document) {
        return document.select("#main-navigation a[href*=/selection]").stream().filter(Element::hasText);
    }

    private static Stream<Category> leftCategories(Document document) {
        return leftCategoryElements(document)
                .map(e -> {
                    var url = e.absUrl("href");
                    return Category
                            .builder()
                            .code(substringAfterLast(url, "/"))
                            .name(e.text())
                            .url(url)
                            .build();
                });
    }

    private static Stream<Element> leftCategoryElements(Document document) {
        return document.select("*[role=navigation] a[data-category-id]").stream().filter(Element::hasText);
    }

    private static Stream<Element> productListElements(Document document) {
        return document.select("li[data-ean]").stream();
    }

    private static String productCode(String url) {
        return substringAfterLast(substringBeforeLast(url, "/"), "/");
    }
}