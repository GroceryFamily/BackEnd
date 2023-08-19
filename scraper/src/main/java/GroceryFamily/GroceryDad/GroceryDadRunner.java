package GroceryFamily.GroceryDad;

import GroceryFamily.GroceryDad.scraper.Scraper;
import GroceryFamily.GroceryDad.scraper.sample.TestSampleStorage;
import GroceryFamily.GroceryDad.scraper.worker.WorkerEventListener;
import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class GroceryDadRunner implements CommandLineRunner {
    private final ProductAPIClient client;
    private final Scraper scraper;
    private final TestSampleStorage testSampleStorage;

    GroceryDadRunner(ProductAPIClient client, Scraper scraper, TestSampleStorage testSampleStorage) {
        this.client = client;
        this.scraper = scraper;
        this.testSampleStorage = testSampleStorage;
    }

    @Override
    public void run(String... args) {
        var listener = WorkerEventListener
                .builder()
                .productHandler(event -> {
                    client.update(event.payload);
                    testSampleStorage.save(event.platform, event.link, event.payload);
                })
                .build();
        scraper.scrap(listener);
        System.exit(0);
    }
}