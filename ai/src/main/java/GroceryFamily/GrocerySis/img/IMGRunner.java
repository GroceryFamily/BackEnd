package GroceryFamily.GrocerySis.img;

import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import io.github.antivoland.cpb.ConsoleProgressBar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("img")
class IMGRunner implements CommandLineRunner {
    private final ProductAPIClient client;
    private final ImageLoader loader;

    IMGRunner(ProductAPIClient client, ImageLoader loader) {
        this.client = client;
        this.loader = loader;
    }

    @Override
    public void run(String... args) {
        log.info("Load images...");
        try (var bar = new ConsoleProgressBar(client.count())) {
            client.listAll().peek(product -> bar.step()).forEach(product -> {
//                var image = loader.raw(product);
                loader.resized(product);
            });
        }
    }
}