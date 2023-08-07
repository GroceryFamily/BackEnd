package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import GroceryFamily.GroceryElders.domain.Currency;
import GroceryFamily.GroceryElders.domain.*;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

class PrismaScraper extends Scraper {
    PrismaScraper(GroceryDadConfig.Scraper config, WebDriver driver, ProductAPIClient client) {
        super(config, driver, client);
    }

    @Override
    protected void acceptOrRejectCookies() {
        $("*[class*='js-cookie-notice'] *[class='close-icon']")
                .shouldBe(visible)
                .click();
    }

    @Override
    protected void switchToEnglish() {
        $("*[data-language='en']").shouldBe(visible).click();
        $$("*[id='main-navigation'] li").shouldHave(itemWithText("Groceries"));
    }

    @Override
    protected CategoryTree buildCategoryTree() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    protected void scrap(Consumer<Product> handler) {
        var tree = new CategoryTree();
        var stack = new Stack<Category>();
        mainCategoryViews().forEach(rootCategoryView -> {
            stack.push(rootCategoryView.category);
            rootCategoryView.select();

            categoryViews().forEach(categoryView -> {
                stack.push(categoryView.category);
                categoryView.select();

                tree.add(stack);

                categoryView.deselect();
                stack.pop();
            });

            rootCategoryView.deselect();
            stack.pop();
        });
        tree.print();
    }

    private List<CategoryView> mainCategoryViews() {
        return mainCategoryElements()
                .asFixedIterable()
                .stream()
                .map(e -> Category
                        .builder()
                        .code(substringAfterLast(substringBeforeLast(e.attr("href"), "/"), "/"))
                        .name(e.text())
                        .build())
                .map(category -> CategoryView
                        .builder()
                        .category(category)
                        .select(() -> {
                            mainCategoryElement(category).shouldBe(visible).click();
                            breadcrumbElement(category).shouldBe(visible);
                        })
                        .deselect(() -> {})
                        .build())
                .toList();
    }

    private SelenideElement mainCategoryElement(Category category) {
        return mainCategoryElements()
                .findBy(text(category.name));
    }

    private ElementsCollection mainCategoryElements() {
        return $$("*[id='main-navigation'] a[href*='selection']")
                .shouldHave(sizeGreaterThan(0));
    }

    private List<CategoryView> categoryViews() {
        return categoryElements()
                .asFixedIterable()
                .stream()
                .map(e -> Category
                        .builder()
                        .code(substringAfterLast(e.attr("href"), "/"))
                        .name(e.text())
                        .build())
                .map(category -> CategoryView
                        .builder()
                        .category(category)
                        .select(() -> {
                            categoryElement(category).shouldBe(visible).click();
                            breadcrumbElement(category).shouldBe(visible);
                        })
                        .deselect(() -> assortmentElement().shouldBe(visible).click())
                        .build())
                .toList();
    }

    private SelenideElement categoryElement(Category category) {
        return categoryElements()
                .findBy(text(category.name));
    }

    private ElementsCollection categoryElements() {
        return $$("*[role='navigation'] a[data-category-id]")
                .shouldHave(sizeGreaterThan(0));
    }

    private SelenideElement assortmentElement() {
        return $("*[data-section='assortments']");
    }

    private SelenideElement breadcrumbElement(Category category) {
        return breadcrumbElements()
                .findBy(attributeMatching("href", format(".*%s.*", category.code)));
    }

    private ElementsCollection breadcrumbElements() {
        return $$("*[class='breadcrumb-item'] *[class=name] a")
                .shouldHave(sizeGreaterThan(0));
    }

    @Override
    protected void scrap(List<String> categories, Consumer<Product> handler) {
        /* todo: remove
        category(categories);
        products().forEach(handler);
        // todo: finalize
         */
    }

    static void category(Stack<String> categories) {
        if (categories.size() != 3) throw new IllegalArgumentException("Requires exactly three categories");

        $$("*[class='main-navigation-items'] a")
                .shouldHave(sizeGreaterThan(0))
                .findBy(text(categories.get(0)))
                .shouldBe(visible)
                .click();

        $$("*[class*='left-navigation'] a")
                .shouldHave(sizeGreaterThan(0))
                .findBy(text(categories.get(1)))
                .shouldBe(visible)
                .click();

        $$("*[class*='categories-shelf'] a")
                .shouldHave(sizeGreaterThan(0))
                .findBy(text(categories.get(2)))
                .shouldBe(visible)
                .click();
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