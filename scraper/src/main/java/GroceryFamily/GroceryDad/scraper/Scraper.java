package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.model.Context;
import GroceryFamily.GroceryDad.scraper.page.PageUtils;
import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.Configuration;
import lombok.Builder;
import org.openqa.selenium.WebDriver;

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
}