package GroceryFamily.scraper;

import GroceryFamily.GroceryElders.domain.Source;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties("grocery-dad")
public class GroceryDadConfig {
    public final List<String> enabled;
    public final Map<String, Scraper> scrapers;

    @Data
    public static class Scraper {
        public final Source source;
        public final String uri;
        public final List<List<String>> categories;
        public final Cache cache;
        public final Duration timeout;

        @Data
        public static class Cache {
            public final Path directory;
        }
    }
}