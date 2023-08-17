package GroceryFamily.GroceryDad.scraper.cache;

import GroceryFamily.GroceryDad.scraper.model.Link;
import io.github.antivoland.sfc.FileCache;
import io.github.antivoland.sfc.FileType;
import lombok.Builder;

import java.nio.file.Path;

@Builder
public class CacheFactory {
    private final Path directory;
    private final boolean compressed;

    public FileCache<String> html(Link link) {
        var subdirectory = directory;
        for (var segment : link.codePath().segments()) {
            subdirectory = subdirectory.resolve(segment);
        }
        var fileType = FileType.text("html");
        return compressed
                ? FileCache.compressed(subdirectory, fileType)
                : FileCache.regular(subdirectory, fileType);
    }
}