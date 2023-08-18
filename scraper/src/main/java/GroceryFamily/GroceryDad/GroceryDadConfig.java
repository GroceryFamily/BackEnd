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
    public final Path cacheDirectory;
    public final List<String> enabledPlatforms;
    public final Map<String, Platform> platforms;

    @Builder
    public static class Platform {
        public final String namespace;
        public final String url;
        public final Live live;
        public final List<List<String>> allowlist;

        @Builder
        public static class Live {
            public final Duration waitTimeout;
            public final Duration sleepDelay;
        }
    }
}