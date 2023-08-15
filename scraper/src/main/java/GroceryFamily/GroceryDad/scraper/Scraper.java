package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.context.Context;
import GroceryFamily.GroceryDad.scraper.page.PageUtils;
import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import lombok.Builder;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Consumer;

import static com.codeborne.selenide.Selenide.using;

// todo: think about robots.txt
//  https://en.wikipedia.org/wiki/Robots.txt
//  https://github.com/google/robotstxt-java
//  https://developers.google.com/search/docs/crawling-indexing/robots/robots_txt
@Builder()
public class Scraper {
    private final GroceryDadConfig.Scraper config;
    private final WebDriver driver;
    private final ProductAPIClient client;
    final Context context;

    public final void scrap() {
        Configuration.timeout = config.timeout.toMillis();
        PageUtils.sleepDelay = config.sleepDelay;
        using(driver, () -> {
            scrap(client::update);
        });
    }

    protected void scrap(Consumer<Product> handler) {
        context.traverse(handler);
    }

    public static void waitUntilPageReady() {
        new WebDriverWait(WebDriverRunner.getWebDriver(), Duration.ofMillis(Configuration.timeout)).until(Scraper::pageIsReady);
    }

    private static boolean pageIsReady(WebDriver driver) {
        return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
    }
}