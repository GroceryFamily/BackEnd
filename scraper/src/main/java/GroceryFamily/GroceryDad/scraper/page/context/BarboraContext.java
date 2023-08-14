package GroceryFamily.GroceryDad.scraper.page.context;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.page.Context;
import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.page.Path;
import GroceryFamily.GroceryDad.scraper.page.SourceType;
import GroceryFamily.GroceryDad.scraper.page.view.CategoryView;
import GroceryFamily.GroceryDad.scraper.page.view.ProductListView;
import GroceryFamily.GroceryDad.scraper.page.view.ProductView;
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

import static GroceryFamily.GroceryDad.scraper.page.Page.sleep;
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
    protected void waitUntilReady() {
        if (!initialized) {
            acceptOrRejectCookies();
            switchToEnglish();
            initialized = true;
        }
    }

    @Override
    protected SourceType type(Document document) {
        if (document.select("*[class*=products-info]").first() != null) return SourceType.PRODUCT;
        if (document.select("*[class*=products-list]").first() != null) return SourceType.PRODUCT_LIST;
        return SourceType.CATEGORY;
    }

    @Override
    protected CategoryView categoryView(Document document, Link selected) {
        return new CategoryView(document, selected) {
            @Override
            protected Map<Path<String>, Category> categories() {
                var categories = new HashMap<Path<String>, Category>();
                categoryElements().forEach(e -> {
                    var codePath = categoryCodePath(e);
                    categories.put(codePath, Category
                            .builder()
                            .code(codePath.tail())
                            .name(e.text())
                            .url(e.absUrl("href"))
                            .build());
                });
                return categories;
            }

            private Stream<Element> categoryElements() {
                return document.select("a[class*=category]").stream().filter(Element::hasText);
            }

            private static Path<String> categoryCodePath(Element e) {
                return Path.of(substringAfter(e.attr("href"), "/").split("/"));
            }
        };
    }

    @Override
    protected ProductListView productListView(Document document, Link selected) {
        return new ProductListView(document, selected) {
            @Override
            public List<Link> productLinks() {
                return productListElements()
                        .map(e -> Link
                                .builder()
                                .code(productLinkCode(e))
                                .name(e.select("*[itemprop=name]").text())
                                .url(requireNonNull(e.select("a").first()).absUrl("href"))
                                .source(selected.source)
                                .build())
                        .toList();
            }

            private Stream<Element> productListElements() {
                return productListElement().select("*[itemtype*=Product]").stream();
            }

            private Element productListElement() {
                return document.select("*[class*=products-list]").first();
            }

            private static String productLinkCode(Element e) {
                return e.select("div[data-b-item-id]").attr("data-b-item-id");
            }

            @Override
            public List<Link> productPageLinks() {
                return productPageNumberElementsExcludingSelected()
                        .map(e -> Link
                                .builder()
                                .code(productPageLinkCode(e))
                                .name(selected.name)
                                .url(e.absUrl("href"))
                                .source(selected.source)
                                .build())
                        .toList();
            }

            Stream<Element> productPageNumberElementsExcludingSelected() {
                return productPageNumbersElement().select("li[:matches([0-9]+)][:not(class=active)] a").stream();
            }

            Element productPageNumbersElement() {
                return document.select("ul[class=pagination]").first();
            }

            private String productPageLinkCode(Element e) {
                return substringBefore(selected.code, "@") + "@" + e.text();
            }
        };
    }

    @Override
    protected ProductView productView(Document document, Link selected) {
        return new ProductView(document, selected) {
            @Override
            public Product product() {
                return Product
                        .builder()
                        .namespace(Namespace.BARBORA)
                        .code(substringAfterLast(selected.url, "/"))
                        .name(document.select("*[class=b-product-info--title]").text())
                        // todo: set prices and categories
                        .build();
            }
        };
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