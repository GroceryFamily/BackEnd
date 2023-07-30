package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryElders.domain.*;
import com.codeborne.selenide.SelenideElement;
import io.github.antivoland.sfc.FileCache;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static java.lang.String.format;

class BarboraScraper extends Scraper {
    BarboraScraper(GroceryDadConfig.Scraper config, WebDriver driver) {
        super(config, driver);
    }

    @Override
    void acceptOrRejectCookies() {
        $("#CybotCookiebotDialogBodyLevelButtonLevelOptinDeclineAll")
                .shouldBe(visible)
                .click();
    }

    @Override
    void switchToEnglish() {
        $("#fti-header-language-dropdown")
                .shouldBe(visible)
                .hover();
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
    void scrap(List<String> categories, FileCache<Product> cache) {
        category(categories);
        products().forEach(product -> cache.save(product.code, product));
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
                .source(Source.BARBORA)
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
                .value(new BigDecimal(text.substring(1)))
                .currency(currency(text.substring(0, 1)))
                .build();
    }

    // €1.15/l
    static Price price(String text) {
        var fragments = text.substring(1).split("/");
        return Price
                .builder()
                .unit(PriceUnit.get(fragments[1]))
                .value(new BigDecimal(fragments[0]))
                .currency(currency(text.substring(0, 1)))
                .build();
    }

    static Currency currency(String symbol) {
        if (symbol == null) throw new IllegalArgumentException("Currency is missing");
        if (symbol.equals("€")) return Currency.EUR;
        throw new UnsupportedOperationException(format("Currency '%s' is not supported", symbol));
    }
}