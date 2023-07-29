package GroceryFamily.scraper.cache;

import GroceryFamily.GroceryElders.domain.Product;
import io.github.antivoland.sfc.FileCache;
import io.github.antivoland.sfc.FileType;

import java.nio.file.Path;

public class Cache {
    public static Factory factory(Path directory) {
        return new Factory(directory);
    }

    public static class Factory {
        private final Path directory;

        private Factory(Path directory) {
            this.directory = directory;
        }

        public FileCache<Product> get(String... categories) {
            Path subdirectory = directory;
            for (String category : categories) {
                subdirectory = subdirectory.resolve(category);
            }
            return FileCache.regular(subdirectory, FileType.document("json", Product.class));
        }
    }
}