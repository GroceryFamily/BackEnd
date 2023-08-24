package GroceryFamily.GrocerySis.model;

import GroceryFamily.GroceryElders.domain.Product;
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

    public void add(Product product) {
        var sb = new StringBuilder();
        sb.append(product.name).append('\n');
        for (var category : product.categories.values()) {
            sb.append(category).append('\n');
        }
        for (var detail : product.details.values()) {
            sb.append(detail).append('\n');
        }
        storage(product.namespace, product.code).save(product.code, sb.toString());
        ++size;
    }

    private FileCache<String> storage(String... subdirectories) {
        var directory = this.directory;
        for (var subdirectory : subdirectories) {
            directory = directory.resolve(subdirectory);
        }
        return FileCache.regular(directory, FILE_TYPE);
    }
}