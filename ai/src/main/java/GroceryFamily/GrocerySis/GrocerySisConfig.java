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

    @Builder
    public static class OpenFoodFacts {
        public final Path dataset;
    }
}