package GroceryFamily.GroceryDad.scraper.cache;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.model.Link;
import io.github.antivoland.sfc.FileCache;
import io.github.antivoland.sfc.FileType;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class HTMLCache {
    private static final FileType<String> FILE_TYPE = FileType.text("html");

    private final Path directory;

    HTMLCache(GroceryDadConfig config) {
        this.directory = config.cacheDirectory;
    }

    public String load(String platform, Link link) {
        return cache(platform, link).load(link.code);
    }

    public void save(String platform, Link link, String html) {
        cache(platform, link).save(link.code, html);
    }

    private FileCache<String> cache(String platform, Link link) {
        return FileCache.compressed(subdirectory(platform, link), FILE_TYPE);
    }

    private Path subdirectory(String platform, Link link) {
        var subdirectory = directory.resolve(platform);
        for (var code : link.codePath()) {
            subdirectory = subdirectory.resolve(code);
        }
        return subdirectory;
    }
}