package GroceryFamily.GrocerySis.model;

import GroceryFamily.GrocerySis.GrocerySisConfig;
import GroceryFamily.GrocerySis.dataset.OFFProduct;
import io.github.antivoland.sfc.FileCache;
import io.github.antivoland.sfc.FileType;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class Labeled {
    private static final FileType<String> FILE_TYPE = FileType.text("txt");

    private final Path directory;
    public int size;

    Labeled(GrocerySisConfig config) {
        directory = config.labeled;
    }

    public void add(OFFProduct product) {
        storage(product.code.value).save(product.code.value, product.name);
        ++size;
    }

    private FileCache<String> storage(String subdirectory) {
        return FileCache.regular(directory.resolve(subdirectory), FILE_TYPE);
    }
}