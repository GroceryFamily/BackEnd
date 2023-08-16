package GroceryFamily.GroceryDad.scraper.cache;

import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryElders.domain.Product;
import io.github.antivoland.sfc.FileCache;
import io.github.antivoland.sfc.FileType;

import java.nio.file.Path;
import java.util.List;

// todo: just CacheFactory
public class Cache {
    public static Factory factory(Path directory) {
        return new Factory(directory);
    }

    public static class Factory {
        private final Path directory;

        private Factory(Path directory) {
            this.directory = directory;
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