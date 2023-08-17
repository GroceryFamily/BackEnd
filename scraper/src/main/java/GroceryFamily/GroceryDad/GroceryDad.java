package GroceryFamily.GroceryDad;

import GroceryFamily.GroceryDad.scraper.Scraper;
import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Collection;

import static java.lang.String.format;

@SpringBootApplication
class GroceryDad implements CommandLineRunner {
    private final Collection<Scraper> scrapers = new ArrayList<>();

    GroceryDad(GroceryDadConfig dadConfig, WebDriver driver) {
        var client = new ProductAPIClient(dadConfig.api.uri);
        for (var name : dadConfig.enabled) {
            var config = dadConfig.scrapers.get(name);
            if (config == null) throw new IllegalArgumentException(format("Missing '%s' config", name));
            scrapers.add(Scraper
                    .builder()
                    .config(config)
                    .driver(driver)
                    .client(client)
                    .build());
        }
    }

    @Override
    public void run(String... args) {
        scrapers.forEach(Scraper::scrap);
    }

    public static void main(String... args) {
        SpringApplication.run(GroceryDad.class, args);
    }
}