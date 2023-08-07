package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.tree.CategoryTree;
import GroceryFamily.GroceryDad.scraper.tree.CategoryTreePath;
import GroceryFamily.GroceryDad.scraper.view.CategoryView;
import GroceryFamily.GroceryElders.domain.*;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

@SuperBuilder
class PrismaScraper extends Scraper {
    @Override
    protected void acceptOrRejectCookies() {
        $("*[class*='js-cookie-notice'] *[class='close-icon']").shouldBe(visible).click();
        sleep();
    }

    @Override
    protected void switchToEnglish() {
        $("*[data-language='en']").shouldBe(visible).click();
        topCategoryElements().shouldHave(itemWithText("Groceries"));
        sleep();
    }

    @Override
    protected void scrap(Consumer<Product> handler) {
        var categories = new CategoryTree();
        topCategoryViews().forEach(view -> scrap(view, handler, categories));
        categories.print();
    }

    private void scrap(CategoryView view, Consumer<Product> handler, CategoryTree categories) {
        if (categoryAllowed(view.path)) {
            view.select();
            if (view.isLeaf()) {
                categories.add(view.path);
                products().forEach(handler);
                // todo: scrap other product pages
            } else {
                leftCategoryViews(view).forEach(child -> scrap(child, handler, categories));
            }
            view.deselect();
        }
    }

    private List<CategoryView> topCategoryViews() {
        return topCategoryElements()
                .shouldHave(sizeGreaterThan(0))
                .asFixedIterable()
                .stream()
                .map(e -> Category
                        .builder()
                        .code(substringAfterLast(substringBeforeLast(e.attr("href"), "/"), "/"))
                        .name(e.text())
                        .build())
                .map(category -> CategoryView
                        .builder()
                        .path(new CategoryTreePath(category))
                        .select(() -> {
                            topCategoryElement(category).shouldBe(visible).click();
                            breadcrumbElement(category).shouldBe(visible);
                            sleep();
                        })
                        .leaf(() -> false)
                        .deselect(() -> {})
                        .build())
                .toList();
    }

    private List<CategoryView> leftCategoryViews(CategoryView parent) {
        return leftCategoryElements()
                .shouldHave(sizeGreaterThan(0))
                .asFixedIterable()
                .stream()
                .map(e -> Category
                        .builder()
                        .code(substringAfterLast(e.attr("href"), "/"))
                        .name(e.text())
                        .build())
                .map(category -> CategoryView
                        .builder()
                        .path(parent.path.add(category))
                        .select(() -> {
                            leftCategoryElement(category).shouldBe(visible).click();
                            breadcrumbElement(category).shouldBe(visible);
                            sleep();
                        })
                        .leaf(() -> leftCategoryElements().isEmpty())
                        .deselect(() -> breadcrumbElement(parent.path.last()).shouldBe(visible).click())
                        .build())
                .toList();
    }

    private SelenideElement topCategoryElement(Category category) {
        return topCategoryElements().findBy(hrefContains(category.code));
    }

    private ElementsCollection topCategoryElements() {
        return $$("*[id='main-navigation'] a[href*='selection']");
    }

    private SelenideElement leftCategoryElement(Category category) {
        return leftCategoryElements().findBy(hrefContains(category.code));
    }

    private ElementsCollection leftCategoryElements() {
        return $$("*[role='navigation'] a[data-category-id]");
    }

    private SelenideElement breadcrumbElement(Category category) {
        return breadcrumbElements().findBy(hrefContains(category.code));
    }

    private ElementsCollection breadcrumbElements() {
        return $$("*[class='breadcrumb-item'] *[class=name] a");
    }

    private static Condition hrefContains(String value) {
        return attributeMatching("href", format(".*%s.*", value));
    }

    static Collection<Product> products() {
        Collection<Product> products = new ArrayList<>();
        for (var e : $$("*[class*='js-shelf-item']").shouldHave(sizeGreaterThan(0))) {
            products.add(product(e));
        }
        return products;
    }

    static Product product(SelenideElement e) {
        return Product
                .builder()
                .namespace(Namespace.PRISMA)
                .code(productCode(e))
                .name(e.$("*[class='name']").text())
                .prices(Set.of(pcPrice(e.$("*[class*='js-comp-price']").text())))
                .build();
    }

    static String productCode(SelenideElement e) {
        return decode(substringAfterLast(productUrl(e), "/"));
    }

    static String productUrl(SelenideElement e) {
        return e.$("a").attr("href");
    }

    // 6,99 €/pcs
    static Price pcPrice(String text) {
        var fragments = text.split(" ");
        var value = fragments[0].split(",");
        return Price
                .builder()
                .unit(PriceUnit.PC)
                .currency(currency(fragments[1].substring(0, 1)))
                .amount(new BigDecimal(value[0] + '.' + value[1]))
                .build();
    }

    static String currency(String symbol) {
        if (symbol == null) throw new IllegalArgumentException("Currency symbol is missing");
        if (symbol.equals("€")) return Currency.EUR;
        throw new UnsupportedOperationException(format("Currency symbol '%s' is not recognized", symbol));
    }

    static String decode(String url) {
        return URLDecoder.decode(url, StandardCharsets.UTF_8);
    }
}