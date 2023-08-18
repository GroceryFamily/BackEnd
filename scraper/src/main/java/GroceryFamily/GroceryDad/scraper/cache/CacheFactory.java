package GroceryFamily.GroceryDad.scraper.cache;

import GroceryFamily.GroceryDad.scraper.model.Link;
import io.github.antivoland.sfc.FileCache;
import io.github.antivoland.sfc.FileType;

import java.nio.file.Path;

public class CacheFactory {
    private final Path directory;

    public CacheFactory(Path directory) {
        this.directory = directory;
    }

    public FileCache<String> html(String platform, Link link) {
        var subdirectory = directory.resolve(platform);
        for (var code : link.codePath()) {
            subdirectory = subdirectory.resolve(code);
        }
        return FileCache.compressed(subdirectory, FileType.text("html"));
    }
}