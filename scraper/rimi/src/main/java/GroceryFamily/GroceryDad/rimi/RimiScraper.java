package GroceryFamily.GroceryDad.rimi;

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
import java.util.ArrayList;
import java.util.Collection;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

@Slf4j
@SpringBootApplication
public class RimiScraper implements CommandLineRunner {
    public static void main(String... args) {
        SpringApplication.run(RimiScraper.class, args);
    }

    final WebDriver driver;

    RimiScraper(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public void run(String... args) {
        using(driver, () -> {
            open("https://rimi.ee/epood/en");
            useOnlyStrictlyNecessaryCookies();
            category("Groceries", "All category products");
            var products = products();
            log.info("Products: {}", products);
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
        final Price kgPrice;

        Product(SelenideElement e) {
            code = e.attr("data-product-code");
            name = e.$("*[class='card__name']").text();
            pcPrice = Price.pc(e.$("*[class*='price-tag']").text());
            kgPrice = Price.kg(e.$("*[class='card__price-per']").text());
        }
    }

    @Builder
    @ToString
    @EqualsAndHashCode
    static class Price {
        final String type;
        final BigDecimal value;
        final String currency;

        // 4\n29\n€/pcs.
        static Price pc(String text) {
            var fragments = text.split("\n");
            return Price
                    .builder()
                    .type("pc")
                    .value(new BigDecimal(fragments[0] + '.' + fragments[1]))
                    .currency(fragments[2].substring(0, 1))
                    .build();
        }

        // 8,09 € /kg
        static Price kg(String text) {
            var fragments = text.split(" ");
            var value = fragments[0].split(",");
            return Price
                    .builder()
                    .type("kg")
                    .value(new BigDecimal(value[0] + '.' + value[1]))
                    .currency(fragments[1])
                    .build();
        }
    }
}