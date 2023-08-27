package GroceryFamily.GrocerySis;

import GroceryFamily.GroceryElders.api.client.APIClientConfig;
import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.nio.file.Path;

@Builder
@ConfigurationProperties("grocery-sis")
public class GrocerySisConfig {
    @NestedConfigurationProperty
    public final APIClientConfig api;
    public final OpenFoodFacts openFoodFacts;
    public final Path labeled;
    public final Path unlabeled;
    public final Path rawImages;
    public final Path trimmedImages;
    public final Path squaredImages;

    @Builder
    public static class OpenFoodFacts {
        public final Path dataset;
    }
}