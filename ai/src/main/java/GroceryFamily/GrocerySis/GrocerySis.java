package GroceryFamily.GrocerySis;

import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
class GrocerySis implements CommandLineRunner {
    public static void main(String... args) {
        SpringApplication.run(GrocerySis.class, args);
    }

    private final ProductAPIClient client;

    GrocerySis(GrocerySisConfig sisConfig) {
        client = new ProductAPIClient(sisConfig.api.uri);
    }

    @Override
    public void run(String... args) throws JsonProcessingException {
        var page = client.list(2);
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(page));
    }
}