package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.tree.CategoryTree;
import GroceryFamily.GroceryElders.domain.Currency;
import GroceryFamily.GroceryElders.domain.*;
import com.codeborne.selenide.SelenideElement;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;

import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

@SuperBuilder
class BarboraScraper extends Scraper {
    @Override
    protected void acceptOrRejectCookies() {
        $("#CybotCookiebotDialogBodyLevelButtonLevelOptinDeclineAll")
                .shouldBe(visible)
                .click();
    }

    @Override
    protected void switchToEnglish() {
        $("#fti-header-language-dropdown")
                .shouldBe(visible)
                .hover();
        sleep();
        $$("#fti-header-language-dropdown li")
                .shouldHave(sizeGreaterThan(0))
                .findBy(text("English"))
                .shouldBe(visible)
                .click();
        $$("*[id*='fti-desktop-menu-item']")
                .shouldHave(itemWithText("Products"))
                .findBy(text("Products"))
                .shouldBe(visible);
    }

    @Override
    protected CategoryTree buildCategoryTree() {
        var tree = new CategoryTree();
        var stack = new Stack<Category>();
        for (var lvl1 : $$("*[id*='fti-desktop-category']").shouldHave(sizeGreaterThan(0))) {
            stack.push(Category
                    .builder()
                    .code(substringAfterLast(lvl1.attr("href"), "/"))
                    .name(lvl1.text())
                    .build());
            lvl1.hover();

            for (var lvl2 : $$("*[id*='fti-category-tree-child']").shouldHave(sizeGreaterThan(0))) {
                stack.push(Category
                        .builder()
                        .code(substringAfterLast(lvl2.attr("href"), "/"))
                        .name(lvl2.$("div").text())
                        .build());

                for (var lvl3 : lvl2.$$("*[id*='fti-category-tree-grand-child']").shouldHave(sizeGreaterThan(0))) {
                    stack.push(Category
                            .builder()
                            .code(substringAfterLast(lvl3.attr("href"), "/"))
                            .name(lvl3.text())
                            .build());
                    tree.add(stack);
                    stack.pop();
                }
                stack.pop();
            }
            stack.pop();
        }
        return tree;
    }

    @Override
    protected void scrap(Consumer<Product> handler) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    protected void scrap(List<String> categories, Consumer<Product> handler) {
        category(categories);
        products().forEach(handler);
        // todo: finalize
    }

    static void category(List<String> categories) {
        if (categories.size() < 2) throw new IllegalArgumentException("Requires at least two categories");

        var firstCategory = $$("*[id*='fti-desktop-category']")
                .shouldHave(sizeGreaterThan(0))
                .findBy(text(categories.get(0)))
                .shouldBe(visible);
        firstCategory.hover();

        var secondCategory = $$("*[id*='fti-category-tree-child'] > div")
                .shouldHave(sizeGreaterThan(0))
                .findBy(text(categories.get(1)))
                .shouldBe(visible);
        if (categories.size() == 2) {
            secondCategory.click();
            return;
        }

        var thirdCategory = $$("*[id*='fti-category-tree-grand-child']")
                .shouldHave(sizeGreaterThan(0))
                .findBy(text(categories.get(2)))
                .shouldBe(visible);
        thirdCategory.click();

        if (categories.size() > 3) throw new IllegalArgumentException("Requires no more than three categories");
    }

    static Collection<Product> products() {
        Collection<Product> products = new ArrayList<>();
        for (var e : $$("*[itemtype*='Product']").shouldHave(sizeGreaterThan(0))) {
            if (e.$("*[itemprop='price']").exists()) {
                products.add(product(e));
            }
        }
        return products;
    }

    static Product product(SelenideElement e) {
        return Product
                .builder()
                .namespace(Namespace.BARBORA)
                .code(e.$("div").attr("data-b-item-id"))
                .name(e.$("*[itemprop='name']").text())
                .prices(Set.of(
                        pcPrice(e.$("*[itemprop='price']").text()),
                        price(e.$("*[class='b-product-price--extra']").text())))
                .build();
    }

    // €2.29
    static Price pcPrice(String text) {
        return Price
                .builder()
                .unit(PriceUnit.PC)
                .currency(currency(text.substring(0, 1)))
                .amount(new BigDecimal(text.substring(1)))
                .build();
    }

    // €1.15/l
    static Price price(String text) {
        var fragments = text.substring(1).split("/");
        return Price
                .builder()
                .unit(PriceUnit.normalize(fragments[1]))
                .currency(currency(text.substring(0, 1)))
                .amount(new BigDecimal(fragments[0]))
                .build();
    }

    static String currency(String symbol) {
        if (symbol == null) throw new IllegalArgumentException("Currency symbol is missing");
        if (symbol.equals("€")) return Currency.EUR;
        throw new UnsupportedOperationException(format("Currency symbol '%s' is not recognized", symbol));
    }
}