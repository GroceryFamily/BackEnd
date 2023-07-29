package GroceryFamily.scraper;

import GroceryFamily.GroceryElders.domain.Currency;
import GroceryFamily.GroceryElders.domain.Price;
import GroceryFamily.GroceryElders.domain.PriceUnit;
import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.scraper.cache.Cache;
import com.codeborne.selenide.SelenideElement;
import io.github.antivoland.sfc.FileCache;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static java.lang.String.format;

@SpringBootApplication
public class BarboraScraper implements CommandLineRunner {
    public static void main(String... args) {
        SpringApplication.run(BarboraScraper.class, args);
    }

    final WebDriver driver;
    final Cache.Factory cacheFactory;

    BarboraScraper(WebDriver driver, @Value("${scraper.barbora.cache.directory}") Path cacheDirectory) {
        this.driver = driver;
        this.cacheFactory = Cache.factory(cacheDirectory);
    }

    @Override
    public void run(String... args) {
        scrap("Dairy and eggs", "Milk", "Pasteurised milk");
    }

    void scrap(String... categories) {
        FileCache<Product> cache = cacheFactory.get(categories);
        using(driver, () -> {
            waitUntilPageLoads(driver);
            open("https://barbora.ee");
            switchToEnglish();
            declineAllCookies();
            category(categories);
            products().forEach(product -> cache.save(product.code, product));
            // todo: implement further
        });
    }

    static void waitUntilPageLoads(WebDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(BarboraScraper::pageIsReady);
    }

    static boolean pageIsReady(WebDriver driver) {
        return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
    }

    static void switchToEnglish() {
        $("#fti-header-language-dropdown")
                .shouldBe(visible)
                .hover();
        $$("#fti-header-language-dropdown li")
                .shouldHave(sizeGreaterThan(0))
                .findBy(text("English"))
                .shouldBe(visible)
                .click();
        $$("*[id*='fti-desktop-menu-item']")
                .shouldHave(itemWithText("Products"), Duration.ofSeconds(10))
                .findBy(text("Products"))
                .shouldBe(visible);
    }

    static void declineAllCookies() {
        $("#CybotCookiebotDialogBodyLevelButtonLevelOptinDeclineAll")
                .shouldBe(visible)
                .click();
    }

    static void category(String... names) {
        if (names.length == 0) return;
        var firstCategory = $$("*[id*='fti-desktop-category']")
                .shouldHave(sizeGreaterThan(0))
                .findBy(text(names[0]))
                .shouldBe(visible);
        firstCategory.hover();
        if (names.length == 1) throw new IllegalArgumentException("Requires at least two categories");
        var secondCategory = $$("*[id*='fti-category-tree-child'] > div")
                .shouldHave(sizeGreaterThan(0))
                .findBy(text(names[1]))
                .shouldBe(visible);
        if (names.length == 2) {
            secondCategory.click();
            return;
        }
        var thirdCategory = $$("*[id*='fti-category-tree-grand-child']")
                .shouldHave(sizeGreaterThan(0))
                .findBy(text(names[2]))
                .shouldBe(visible);
        thirdCategory.click();
        if (names.length > 3) throw new IllegalArgumentException("Requires no more than three categories");
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