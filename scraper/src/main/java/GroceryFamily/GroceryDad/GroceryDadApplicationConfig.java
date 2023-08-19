package GroceryFamily.GroceryDad;

import GroceryFamily.GroceryElders.GroceryEldersApplicationConfig;
import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {GroceryEldersApplicationConfig.class})
class GroceryDadApplicationConfig {
    @Bean
    ProductAPIClient client(GroceryDadConfig config) {
        return new ProductAPIClient(config.api.uri);
    }
}