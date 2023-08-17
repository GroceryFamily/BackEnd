package GroceryFamily.GroceryDad;

import GroceryFamily.GroceryDad.scraper.Scraper;
import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.Executors;

import static java.lang.String.format;
import static org.openqa.selenium.chrome.ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY;

@Slf4j
@SpringBootApplication
class GroceryDad implements CommandLineRunner {
    private final GroceryDadConfig config;

    GroceryDad(GroceryDadConfig config) {
        this.config = config;
    }

    @Override
    public void run(String... args) {
        var client = new ProductAPIClient(config.api.uri);
        //noinspection resource
        var threadPool = Executors.newCachedThreadPool();
        try {
            for (var name : config.enabled) {
                threadPool.execute(() -> {
                    Thread.currentThread().setName(name + "-worker");
                    var visited = Scraper.scrap(scraperConfig(name), client::update);
                    log.info("Visited {} links: \n{}", name, visited);
                });
            }
        } finally {
            threadPool.shutdown();
        }
    }

    private GroceryDadConfig.Scraper scraperConfig(String name) {
        if (!config.scrapers.containsKey(name)) {
            throw new IllegalArgumentException(format("Missing %s config", name));
        }
        return config.scrapers.get(name);
    }

    public static void main(String... args) {
        System.setProperty(CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");
        SpringApplication.run(GroceryDad.class, args);
    }
}