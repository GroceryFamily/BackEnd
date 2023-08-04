package GroceryFamily.GrocerySis;

import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import GroceryFamily.GroceryElders.domain.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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
        var firstPage = client.list(2);
        print(firstPage);
        var secondPage = client.list(firstPage.nextPageToken);
        print(secondPage);
    }

    @SneakyThrows
    private void print(Page<?> page) {
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(page));
    }
}