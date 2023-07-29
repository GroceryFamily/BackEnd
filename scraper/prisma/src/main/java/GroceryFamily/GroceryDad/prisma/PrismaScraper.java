package GroceryFamily.GroceryDad.prisma;

import GroceryFamily.GroceryDad.cache.Cache;
import GroceryFamily.GroceryElders.domain.Currency;
import GroceryFamily.GroceryElders.domain.Price;
import GroceryFamily.GroceryElders.domain.PriceUnit;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.SelenideElement;
import io.github.antivoland.sfc.FileCache;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

@Slf4j
@SpringBootApplication
public class PrismaScraper implements CommandLineRunner {
    public static void main(String... args) {
        SpringApplication.run(PrismaScraper.class, args);
    }

    final WebDriver driver;
    final Cache.Factory cacheFactory;

    PrismaScraper(WebDriver driver, @Value("${scraper.prisma.cache.directory}") Path cacheDirectory) {
        this.driver = driver;
        this.cacheFactory = Cache.factory(cacheDirectory);
    }

    @Override
    public void run(String... args) {
        scrap("Groceries", "Pets", "Toys for pets");
    }

    void scrap(String... categories) {
        FileCache<Product> cache = cacheFactory.get(categories);
        using(driver, () -> {
            open("https://prismamarket.ee");
            switchToEnglish();
            closeCookieNotice();
            category(categories);
            products().forEach(product -> cache.save(product.code, product));
            // todo: implement further
        });
    }

    static void switchToEnglish() {
        $("*[data-language='en']").shouldBe(visible).click();
    }

    static void closeCookieNotice() {
        $("*[class*='js-cookie-notice'] *[class='close-icon']").shouldBe(visible).click();
    }

    static void category(String... names) {
        if (names.length == 0) return;
        $$("*[class='main-navigation-items'] a").findBy(text(names[0])).click();
        if (names.length == 1) return;
        $$("*[class*='left-navigation'] a").findBy(text(names[1])).click();
        if (names.length == 2) return;
        $$("*[class*='categories-shelf'] a").findBy(text(names[2])).click();
        if (names.length > 3) throw new IllegalArgumentException("Too many categories");
    }

    static Collection<Product> products() {
        $("*[class*='js-products-shelf']").shouldBe(visible);
        Collection<Product> products = new ArrayList<>();
        for (var e : $$("*[class*='js-shelf-item']")) {
            products.add(product(e));
        }
        return products;
    }

    static Product product(SelenideElement e) {
        return Product
                .builder()
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