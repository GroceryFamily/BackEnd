package GroceryFamily.GrocerySis;

import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.atomic.AtomicInteger;

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
        AtomicInteger no = new AtomicInteger();
        client.listAll().forEach(product -> System.out.println(no.incrementAndGet() + ": " + product));
    }
}