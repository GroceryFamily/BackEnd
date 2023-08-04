package GroceryFamily.GrocerySis;

import GroceryFamily.GroceryElders.api.client.ProductAPIClientConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties("grocery-sis")
public class GrocerySisConfig {
    @NestedConfigurationProperty
    public final ProductAPIClientConfig api;
}