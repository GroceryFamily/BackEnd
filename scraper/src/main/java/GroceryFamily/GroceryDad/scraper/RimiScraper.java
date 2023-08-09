package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.tree.CategoryTree;
import GroceryFamily.GroceryDad.scraper.view.CategoryView;
import GroceryFamily.GroceryElders.domain.*;
import com.codeborne.selenide.SelenideElement;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static GroceryFamily.GroceryDad.scraper.RimiPage.mainCategoryViews;
import static GroceryFamily.GroceryDad.scraper.page.Page.hrefContains;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static java.lang.String.format;

@Slf4j
@SuperBuilder
class RimiScraper extends Scraper {
    @Override
    protected void acceptOrRejectCookies() {
        $("#CybotCookiebotDialogBodyLevelButtonLevelOptinDeclineAll")
                .shouldBe(visible)
                .click();
    }

    @Override
    protected void switchToEnglish() {
        // do nothing
    }

    @Override
    protected void scrap(Consumer<Product> handler) {
//        var categories = new CategoryTree();
//        mainCategoryViews().forEach(view -> traverse(view, handler, categories));
//        log.info("[RIMI] Traversed categories: {}", categories);
        var startMillis = System.currentTimeMillis();
//        var cnt = $$("nav[data-category-menu-container]")
//                .asDynamicIterable()
//                .stream()
//                .count();
//        System.out.println(cnt);


        $("nav[data-category-menu-container]")
                .$$("a")
                .filter(hrefContains("/products/"))
                .asDynamicIterable()
//                .asFixedIterable()
                .stream()
                .forEach(e -> System.out.println(e.text()));
        System.out.println(System.currentTimeMillis() - startMillis);
    }

    private void traverse(CategoryView view, Consumer<Product> handler, CategoryTree categories) {
        if (categoryAllowed(view.path)) {
            view.select();
            var children = view.children();
            if (children.isEmpty()) {
//                products(view.path).forEach(handler);
                categories.add(view.path);
            } else {
                view.children().forEach(child -> traverse(child, handler, categories));
            }
            view.deselect();
        }
    }

    @Override
    protected void scrap(List<String> categories, Consumer<Product> handler) {
        category(categories);
        products().forEach(handler);
        // todo: finalize
    }

    static void category(List<String> categories) {
        if (categories.size() < 2) throw new IllegalArgumentException("Requires at least two categories");

        var button = $("#desktop_category_menu_button").shouldBe(visible);
        button.click();
        int dataLevel = 1;
        for (String category : categories) {
            button = $$("*[data-level='" + dataLevel + "'] li")
                    .shouldHave(sizeGreaterThan(0))
                    .findBy(text(category))
                    .shouldBe(visible);
            button.click();
            ++dataLevel;
        }
    }

    static Collection<Product> products() {
        Collection<Product> products = new ArrayList<>();
        for (var e : $$("*[class='product-grid__item'] > div").shouldHave(sizeGreaterThan(0))) {
            if (e.$("*[class*='price-tag']").exists()) {
                products.add(product(e));
            }
        }
        return products;
    }

    static Product product(SelenideElement e) {
        return Product
                .builder()
                .namespace(Namespace.RIMI)
                .code(e.attr("data-product-code"))
                .name(e.$("*[class='card__name']").text())
                .prices(Set.of(
                        pcPrice(e.$("*[class*='price-tag']").text()),
                        price(e.$("*[class='card__price-per']").text())))
                .build();
    }

    // 4\n29\n€/pcs.
    static Price pcPrice(String text) {
        var fragments = text.split("\n");
        return Price
                .builder()
                .unit(PriceUnit.PC)
                .currency(currency(fragments[2].substring(0, 1)))
                .amount(new BigDecimal(fragments[0] + '.' + fragments[1]))
                .build();
    }

    // 8,09 € /kg
    static Price price(String text) {
        var fragments = text.split(" ");
        var value = fragments[0].split(",");
        return Price
                .builder()
                .unit(PriceUnit.normalize(fragments[2].substring(1)))
                .currency(currency(fragments[1]))
                .amount(new BigDecimal(value[0] + '.' + value[1]))
                .build();
    }

    static String currency(String symbol) {
        if (symbol == null) throw new IllegalArgumentException("Currency symbol is missing");
        if (symbol.equals("€")) return Currency.EUR;
        throw new UnsupportedOperationException(format("Currency symbol '%s' is not recognized", symbol));
    }
}