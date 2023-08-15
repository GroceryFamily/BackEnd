package GroceryFamily.GroceryDad.scraper.page.context;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.page.Context;
import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.page.Path;
import GroceryFamily.GroceryDad.scraper.page.Source;
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

    @Override
    public List<Link> productPageLinks(Document document, Source selected) {
        return productPageNumberElementsExcludingSelected(document)
                .map(e -> {
                    var number = e.text();
                    var codeSuffix = "1".equals(number) ? "" : "@" + e.text();
                    return Link
                            .builder()
                            .code(substringBefore(selected.code, "@") + codeSuffix)
                            .name(selected.name)
                            .url(e.absUrl("href"))
                            .source(selected.parent)
                            .build();
                })
                .toList();
    }

    @Override
    public List<Link> productLinks(Document document, Source selected) {
        return productListElements(document)
                .map(e -> {
                    var url = e.absUrl("href");
                    return Link
                            .builder()
                            .code(productCode(url))
                            .name(e.text())
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
                .namespace(Namespace.BARBORA)
                .code(productCode(selected.url))
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

    private static SelenideElement englishLanguageElement() {
        return languageSelectElement().$$("li").findBy(text("English"));
    }

    private static SelenideElement languageSelectElement() {
        return $("#fti-header-language-dropdown");
    }

    private static Stream<Element> categoryElements(Document document) {
        return document.select("a[class*=category]").stream().filter(Element::hasText);
    }

    private static Path<String> categoryCodePath(Element e) {
        return Path.of(substringAfter(e.attr("href"), "/").split("/"));
    }

    private static Stream<Element> productPageNumberElementsExcludingSelected(Document document) {
        return document.select("ul[class=pagination] li:matches([0-9]+):not([class=active]) a").stream();
    }

    private static Stream<Element> productListElements(Document document) {
        return document.select("*[itemtype*=Product] a[class*=title]").stream();
    }

    private static String productCode(String url) {
        return substringAfterLast(url, "/");
    }
}