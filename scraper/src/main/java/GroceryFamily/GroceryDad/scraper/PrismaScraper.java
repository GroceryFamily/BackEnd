package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryElders.domain.*;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

class PrismaScraper extends Scraper {
    PrismaScraper(GroceryDadConfig.Scraper config, WebDriver driver) {
        super(config, driver);
    }

    @Override
    protected void acceptOrRejectCookies() {
        $("*[class*='js-cookie-notice'] *[class='close-icon']")
                .shouldBe(visible)
                .click();
    }

    @Override
    protected void switchToEnglish() {
        $("*[data-language='en']")
                .shouldBe(visible)
                .click();
    }

    @Override
    protected void scrap(List<String> categories, Consumer<Product> handler) {
        category(categories);
        products().forEach(handler);
        // todo: finalize
    }

    static void category(List<String> categories) {
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
                .value(new BigDecimal(value[0] + '.' + value[1]))
                .currency(currency(fragments[1].substring(0, 1)))
                .build();
    }

    static Currency currency(String symbol) {
        if (symbol == null) throw new IllegalArgumentException("Currency is missing");
        if (symbol.equals("€")) return Currency.EUR;
        throw new UnsupportedOperationException(format("Currency '%s' is not supported", symbol));
    }

    static String decode(String url) {
        return URLDecoder.decode(url, StandardCharsets.UTF_8);
    }
}