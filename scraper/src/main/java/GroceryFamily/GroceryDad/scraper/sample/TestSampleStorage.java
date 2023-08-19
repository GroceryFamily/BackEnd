package GroceryFamily.GroceryDad.scraper.sample;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.model.Link;
import GroceryFamily.GroceryDad.scraper.model.Source;
import GroceryFamily.GroceryElders.domain.Product;
import io.github.antivoland.sfc.FileCache;
import io.github.antivoland.sfc.FileType;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class TestSampleStorage {
    private static final FileType<Product> FILE_TYPE = FileType.document("json", Product.class);

    private final Path directory;

    public TestSampleStorage(GroceryDadConfig config) {
        this.directory = config.sampleDirectory;
    }

    public Product load(String platform, Link link) {
        return storage(platform, link).load(link.code);
    }

    public void save(String platform, Link link, Product product) {
        storage(platform, link).save(link.code, product);
    }

    private FileCache<Product> storage(String platform, Link link) {
        return FileCache.regular(subdirectory(platform, link), FILE_TYPE);
    }

    private Path subdirectory(String platform, Link link) {
        var subdirectory = directory.resolve(platform);
        for (var code : link.source.codePath()) {
            subdirectory = subdirectory.resolve(code);
        }
        return subdirectory;
    }
}