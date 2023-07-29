package GroceryFamily.scraper;

import GroceryFamily.GroceryElders.domain.Source;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.util.List;

@Data
@ConfigurationProperties("grocery-dad")
public class GroceryDadConfig {
    public final List<Scraper> scrapers;

    @Data
    public static class Scraper {
        public final Source source;
        public final String uri;
        public final List<List<String>> categories;
        public final Cache cache;

        @Data
        public static class Cache {
            public final Path directory;
        }
    }
}