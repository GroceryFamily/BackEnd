package GroceryFamily.GroceryDad;

import GroceryFamily.GroceryElders.api.client.APIClientConfig;
import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Builder
@ConfigurationProperties("grocery-dad")
public class GroceryDadConfig {
    @NestedConfigurationProperty
    public final APIClientConfig api;
    public final List<String> enabled;
    public final Map<String, Scraper> scrapers;

    @Builder
    public static class Scraper {
        public final String namespace;
        public final String url;
        public final List<List<String>> allowlist;
        public final Cache cache;
        public final Live live;

        @Builder
        public static class Cache {
            public final Path directory;
            public final boolean compressed;
        }

        @Builder
        public static class Live {
            public final Duration waitTimeout;
            public final Duration sleepDelay;
        }
    }
}