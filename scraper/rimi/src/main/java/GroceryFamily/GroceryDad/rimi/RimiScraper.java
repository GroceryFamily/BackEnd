package GroceryFamily.GroceryDad.rimi;

import GroceryFamily.GroceryDad.cache.Cache;
import GroceryFamily.GroceryDad.domain.Currency;
import GroceryFamily.GroceryDad.domain.Price;
import GroceryFamily.GroceryDad.domain.PriceUnit;
import GroceryFamily.GroceryDad.domain.Product;
import com.codeborne.selenide.SelenideElement;
import io.github.antivoland.sfc.FileCache;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static java.lang.String.format;

@Slf4j
@SpringBootApplication
public class RimiScraper implements CommandLineRunner {
    public static void main(String... args) {
        SpringApplication.run(RimiScraper.class, args);
    }

    final WebDriver driver;
    final Cache.Factory cacheFactory;

    RimiScraper(WebDriver driver, @Value("${scraper.rimi.cache.directory}") Path cacheDirectory) {
        this.driver = driver;
        this.cacheFactory = Cache.factory(cacheDirectory);
    }

    @Override
    public void run(String... args) {
        scrap("Groceries", "All category products");
    }

    void scrap(String... categories) {
        FileCache<Product> cache = cacheFactory.get(categories);
        using(driver, () -> {
            open("https://rimi.ee/epood/en");
            useOnlyStrictlyNecessaryCookies();
            category(categories);
            products().forEach(product -> cache.save(product.code, product));
            // todo: implement further
        });
    }

    static void useOnlyStrictlyNecessaryCookies() {
        $("#CybotCookiebotDialogBodyLevelButtonLevelOptinDeclineAll").shouldBe(visible).click();
    }

    static void category(String... names) {
        if (names.length == 0) return;
        $("#desktop_category_menu_button").shouldBe(visible).click();
        var name = names[0];
        var button = $$("#desktop_category_menu button").findBy(text(name));
        var submenu = button.attr("aria-owns");
        button.click();
        for (int idx = 1; idx < names.length; ++idx) {
            name = names[idx];
            button = $$("#" + submenu + " a").findBy(text(name));
            submenu = button.attr("aria-owns");
            button.click();
        }
    }

    static Collection<Product> products() {
        Collection<Product> products = new ArrayList<>();
        for (var e : $$("*[class='product-grid__item'] > div")) {
            products.add(product(e));
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
        if (symbol == null) throw new InvalidArgumentException("Currency is missing");
        if (symbol.equals("€")) return Currency.EUR;
        throw new UnsupportedOperationException(format("Currency '%s' is not supported", symbol));
    }
}