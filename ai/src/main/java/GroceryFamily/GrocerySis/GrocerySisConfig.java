package GroceryFamily.GrocerySis;

import GroceryFamily.GroceryElders.api.client.APIClientConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties("grocery-sis")
public class GrocerySisConfig {
    @NestedConfigurationProperty
    public final APIClientConfig api;
}