package GroceryFamily.GroceryDad.prisma;

import com.codeborne.selenide.SelenideElement;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

@Slf4j
@SpringBootApplication
public class PrismaScraper implements CommandLineRunner {
    public static void main(String... args) {
        SpringApplication.run(PrismaScraper.class, args);
    }

    final WebDriver driver;

    PrismaScraper(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public void run(String... args) {
        using(driver, () -> {
            open("https://prismamarket.ee");
            switchToEnglish();
            closeCookieNotice();
            category("Groceries", "Pets", "Toys for pets");
            var products = products();
            log.info("Products: {}", products);
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
            products.add(new Product(e));
        }
        return products;
    }

    @ToString
    @EqualsAndHashCode
    static class Product {
        final String code;
        final String name;
        final Price pcPrice;

        Product(SelenideElement e) {
            code = code(e);
            name = e.$("*[class='name']").text();
            pcPrice = Price.pc(e.$("*[class*='js-comp-price']").text());
        }

        static String code(SelenideElement e) {
            return decode(substringAfterLast(url(e), "/"));
        }

        static String url(SelenideElement e) {
            return e.$("a").attr("href");
        }

        static String decode(String url) {
            return URLDecoder.decode(url, StandardCharsets.UTF_8);
        }
    }

    @Builder
    @ToString
    @EqualsAndHashCode
    static class Price {
        final String type;
        final BigDecimal value;
        final String currency;

        // 6,99 €/pcs
        static Price pc(String text) {
            var fragments = text.split(" ");
            var value = fragments[0].split(",");
            return Price
                    .builder()
                    .type("pc")
                    .value(new BigDecimal(value[0] + '.' + value[1]))
                    .currency(fragments[1].substring(0, 1))
                    .build();
        }
    }
}