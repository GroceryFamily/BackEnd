package GroceryFamily.GrocerySis.model;

import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.GrocerySis.GrocerySisConfig;
import io.github.antivoland.sfc.FileCache;
import io.github.antivoland.sfc.FileType;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class Unlabeled {
    private static final FileType<String> FILE_TYPE = FileType.text("txt");

    private final Path directory;
    public int size;

    Unlabeled(GrocerySisConfig config) {
        directory = config.unlabeled;
    }

    public void add(Product product) {
        storage(product.code).save(product.code, product.name);
        ++size;
    }

    private FileCache<String> storage(String subdirectory) {
        return FileCache.regular(directory.resolve(subdirectory), FILE_TYPE);
    }
}