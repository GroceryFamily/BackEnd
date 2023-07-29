package GroceryFamily.scraper;

import org.openqa.selenium.WebDriver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Collection;

@SpringBootApplication
class GroceryDad implements CommandLineRunner {
    public static void main(String... args) {
        SpringApplication.run(GroceryDad.class, args);
    }

    private final Collection<Scraper> scrapers = new ArrayList<>();

    GroceryDad(GroceryDadConfig dadConfig, WebDriver driver) {
        for (GroceryDadConfig.Scraper config : dadConfig.scrapers) {
            scrapers.add(Scraper.create(config, driver));
        }
    }

    @Override
    public void run(String... args) {
        scrapers.forEach(Scraper::scrap);
    }
}