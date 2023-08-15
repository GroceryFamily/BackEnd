package GroceryFamily.GroceryDad.scraper.cache;

import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryElders.domain.Product;
import io.github.antivoland.sfc.FileCache;
import io.github.antivoland.sfc.FileType;

import java.nio.file.Path;
import java.util.List;

public class Cache {
    public static Factory factory(Path directory) {
        return new Factory(directory);
    }

    public static class Factory {
        private final Path directory;

        private Factory(Path directory) {
            this.directory = directory;
        }

        public FileCache<Product> get(List<String> categories) {
            Path subdirectory = directory;
            for (String category : categories) {
                subdirectory = subdirectory.resolve(category);
            }
            return FileCache.regular(subdirectory, FileType.document("json", Product.class));
        }

        public FileCache<String> html(List<String> segments) {
            Path subdirectory = directory;
            for (String category : segments) {
                subdirectory = subdirectory.resolve(category);
            }
            return FileCache.compressed(subdirectory, FileType.text("html"));
        }

        public FileCache<String> html(Link link) {
            var subdirectory = directory;
            for (var segment : link.codePath().segments()) {
                subdirectory = subdirectory.resolve(segment);
            }
            return FileCache.compressed(subdirectory, FileType.text("html"));
        }
    }
}