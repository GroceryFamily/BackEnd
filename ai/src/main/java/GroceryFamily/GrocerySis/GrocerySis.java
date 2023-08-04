package GroceryFamily.GrocerySis;

import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
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
    public void run(String... args) {
        var page = client.list(10);
        System.out.println(page);
    }
}