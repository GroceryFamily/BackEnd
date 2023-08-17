package GroceryFamily.GroceryDad.scraper.cache;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.model.Link;
import io.github.antivoland.sfc.FileCache;
import io.github.antivoland.sfc.FileType;
import lombok.Builder;

public class CacheFactory {
    private final GroceryDadConfig.Scraper.Cache config;

    public CacheFactory(GroceryDadConfig.Scraper.Cache config) {
        this.config = config;
    }

    public FileCache<String> html(Link link) {
        var subdirectory = config.directory;
        for (var segment : link.codePath().segments()) {
            subdirectory = subdirectory.resolve(segment);
        }
        var fileType = FileType.text("html");
        return config.compressed
                ? FileCache.compressed(subdirectory, fileType)
                : FileCache.regular(subdirectory, fileType);
    }
}