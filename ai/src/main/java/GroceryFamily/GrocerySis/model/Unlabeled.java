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
        var sb = new StringBuilder();
        sb.append(product.name).append('\n');
        product.brand().ifPresent(brand -> sb.append(brand).append('\n'));
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