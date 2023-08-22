package GroceryFamily.GrocerySis;

import GroceryFamily.GroceryElders.GroceryEldersApplicationConfig;
import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {GroceryEldersApplicationConfig.class})
class GrocerySisApplicationConfig {
    @Bean
    ProductAPIClient client(GrocerySisConfig config) {
        return new ProductAPIClient(config.api.uri);
    }
}