package GroceryFamily.GroceryDad;

import GroceryFamily.GroceryDad.scraper.Scraper;
import GroceryFamily.GroceryDad.scraper.model.Source;
import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import GroceryFamily.GroceryElders.domain.Product;
import io.github.antivoland.sfc.FileCache;
import io.github.antivoland.sfc.FileType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;

@Slf4j
@SpringBootApplication
class GroceryDad implements CommandLineRunner {
    private static final Path SAMPLES = Path.of("scraper/src/test/resources/samples");

    private final ProductAPIClient client;
    private final Scraper scraper;

    GroceryDad(ProductAPIClient client, Scraper scraper) {
        this.client = client;
        this.scraper = scraper;
    }

    @Override
    public void run(String... args) {
        scraper.scrap((platform, product, source) -> {
            client.update(product);
            samples(platform, source).save(product.code, product);
        });
    }

    private static FileCache<Product> samples(String platform, Source source) {
        var subdirectory = SAMPLES.resolve(platform);
        for (var code : source.parent.codePath()) {
            subdirectory = subdirectory.resolve(code);
        }
        return FileCache.regular(subdirectory, FileType.document("json", Product.class));
    }

    public static void main(String... args) {
        SpringApplication.run(GroceryDad.class, args);
        System.exit(0);
    }
}