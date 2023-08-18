package GroceryFamily.GroceryDad;

import GroceryFamily.GroceryDad.scraper.Scraper;
import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.openqa.selenium.chrome.ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY;

@Slf4j
@SpringBootApplication
class GroceryDad implements CommandLineRunner {
    private final ProductAPIClient client;
    private final Scraper scraper;

    GroceryDad(ProductAPIClient client, Scraper scraper) {
        this.client = client;
        this.scraper = scraper;
    }

    @Override
    public void run(String... args) {
        scraper.scrap(client::update);
    }

    public static void main(String... args) {
        SpringApplication.run(GroceryDad.class, args);
        System.exit(0);
    }
}