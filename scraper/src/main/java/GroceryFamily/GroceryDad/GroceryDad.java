package GroceryFamily.GroceryDad;

import GroceryFamily.GroceryDad.scraper.Scraper;
import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import static java.lang.String.format;

@SpringBootApplication
class GroceryDad implements CommandLineRunner {
    private final Map<String, Scraper> scrapers = new HashMap<>();
    private final ProductAPIClient client;

    GroceryDad(GroceryDadConfig dadConfig) {
        for (var name : dadConfig.enabled) {
            var config = dadConfig.scrapers.get(name);
            if (config == null) throw new IllegalArgumentException(format("Missing %s config", name));
            scrapers.put(name, new Scraper(config));
        }
        client = new ProductAPIClient(dadConfig.api.uri);
    }

    @Override
    public void run(String... args) {
        //noinspection resource
        var threadPool = Executors.newCachedThreadPool();
        try {
            scrapers.forEach((name, scraper) -> threadPool.execute(() -> {
                Thread.currentThread().setName(name + "-worker");
                scraper.scrap(client::update);
            }));
        } finally {
            threadPool.shutdown();
            scrapers.values().forEach(Scraper::destroy);
        }
    }

    public static void main(String... args) {
        SpringApplication.run(GroceryDad.class, args);
    }
}