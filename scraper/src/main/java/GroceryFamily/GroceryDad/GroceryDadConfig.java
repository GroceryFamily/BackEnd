package GroceryFamily.GroceryDad;

import GroceryFamily.GroceryElders.api.client.APIClientConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties("grocery-dad")
public class GroceryDadConfig {
    @NestedConfigurationProperty
    public final APIClientConfig api;
    public final List<String> enabled;
    public final Map<String, Scraper> scrapers;

    @Data
    public static class Scraper {
        public final String namespace;
        public final String url;
        public final List<List<String>> allowlist;
        public final Cache cache;
        public final Live live;

        @Data
        public static class Cache {
            public final Path directory;
            public final boolean compressed;
        }

        @Data
        public static class Live {
            public final Duration timeout;
            public final Duration sleepDelay;
        }
    }
}