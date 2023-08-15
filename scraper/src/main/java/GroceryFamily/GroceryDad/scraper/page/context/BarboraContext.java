package GroceryFamily.GroceryDad.scraper.page.context;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.page.*;
import GroceryFamily.GroceryElders.domain.Category;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.SelenideElement;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static GroceryFamily.GroceryDad.scraper.page.PageUtils.sleep;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.*;

public class BarboraContext extends Context {
    private boolean initialized;

    public BarboraContext(GroceryDadConfig.Scraper config) {
        super(config);
    }

    @Override
    protected void initialize() {
        if (!initialized) {
            acceptOrRejectCookies();
            switchToEnglish();
            initialized = true;
        }
    }

    @Override
    public SourceType type(Document document) {
        if (document.select("h1[class*=products-info]").first() != null) return SourceType.PRODUCT;
        if (document.select("*[class*=products-list]").first() != null) return SourceType.PRODUCT_LIST;
        return SourceType.CATEGORY;
    }

    @Override
    protected Map<Path<String>, Category> categories(Document document, Source selected) {
        var categories = new HashMap<Path<String>, Category>();
        categoryElements(document).forEach(e -> {
            var codePath = categoryCodePath(e);
            categories.put(codePath, Category
                    .builder()
                    .code(codePath.tail())
                    .name(e.text().replaceAll("\s[0-9]+$", ""))
                    .url(e.absUrl("href"))
                    .build());
        });
        return categories;
    }

    private Stream<Element> categoryElements(Document document) {
        return document.select("a[class*=category]").stream().filter(Element::hasText);
    }

    private static Path<String> categoryCodePath(Element e) {
        return Path.of(substringAfter(e.attr("href"), "/").split("/"));
    }

    @Override
    public List<Link> productPageLinks(Document document, Source selected) {
        return productPageNumberElementsExcludingSelected(document)
                .map(e -> Link
                        .builder()
                        .code(productPageLinkCode(e, selected))
                        .name(selected.name)
                        .url(e.absUrl("href"))
                        .source(selected.parent)
                        .build())
                .toList();

    }

    Stream<Element> productPageNumberElementsExcludingSelected(Document document) {
        return document.select("ul[class=pagination] li:matches([0-9]+):not([class=active]) a").stream();
    }

    private String productPageLinkCode(Element e, Source selected) {
        return substringBefore(selected.code, "@") + "@" + e.text();
    }

    @Override
    public List<Link> productLinks(Document document, Source selected) {
        return productListElements(document)
                .map(e -> Link
                        .builder()
                        .code(productLinkCode(e))
                        .name(e.select("*[itemprop=name]").text())
                        .url(requireNonNull(e.select("a").first()).absUrl("href"))
                        .source(selected)
                        .build())
                .toList();

    }

    private Stream<Element> productListElements(Document document) {
        return document.select("*[class*=products-list] *[itemtype*=Product]").stream();
    }

    private static String productLinkCode(Element e) {
        return e.select("div[data-b-item-id]").attr("data-b-item-id");
    }

    @Override
    public Product product(Document document, Source selected) {
        return Product
                .builder()
                .namespace(Namespace.BARBORA)
                .code(substringAfterLast(selected.url, "/"))
                .name(document.select("*[class=b-product-info--title]").text())
                .url(selected.url)
                // todo: set prices and categories
                .build();
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