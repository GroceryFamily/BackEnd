package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryElders.domain.Currency;
import GroceryFamily.GroceryElders.domain.Price;
import GroceryFamily.GroceryElders.domain.PriceUnit;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.SelenideElement;
import io.github.antivoland.sfc.FileCache;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static java.lang.String.format;

class RimiScraper extends Scraper {
    RimiScraper(GroceryDadConfig.Scraper config, WebDriver driver) {
        super(config, driver);
    }

    @Override
    void scrap(List<String> categories) {
        FileCache<Product> cache = cache(categories);
        open(config.uri);
        waitUntilPageLoads();
        useOnlyStrictlyNecessaryCookies();
        category(categories);
        products().forEach(product -> cache.save(product.code, product));
        // todo: finalize
    }

    static void useOnlyStrictlyNecessaryCookies() {
        $("#CybotCookiebotDialogBodyLevelButtonLevelOptinDeclineAll").shouldBe(visible).click();
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
                .value(new BigDecimal(fragments[0] + '.' + fragments[1]))
                .currency(currency(fragments[2].substring(0, 1)))
                .build();
    }

    // 8,09 € /kg
    static Price price(String text) {
        var fragments = text.split(" ");
        var value = fragments[0].split(",");
        return Price
                .builder()
                .unit(PriceUnit.get(fragments[2].substring(1)))
                .value(new BigDecimal(value[0] + '.' + value[1]))
                .currency(currency(fragments[1]))
                .build();
    }

    static Currency currency(String symbol) {
        if (symbol == null) throw new IllegalArgumentException("Currency is missing");
        if (symbol.equals("€")) return Currency.EUR;
        throw new UnsupportedOperationException(format("Currency '%s' is not supported", symbol));
    }
}