package GroceryFamily.GroceryDad;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.util.Map;

@Data
@ConfigurationProperties("grocery-dad")
public class GroceryDadConfig {
    public final Map<String, Scraper> scrapers;

    @Data
    public static class Scraper {
        public final String uri;
        public final Scraper.Cache cache;

        @Data
        public static class Cache {
            public final Path directory;
        }
    }
}