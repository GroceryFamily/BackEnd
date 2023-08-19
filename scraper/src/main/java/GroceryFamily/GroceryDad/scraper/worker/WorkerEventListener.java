package GroceryFamily.GroceryDad.scraper.worker;

import GroceryFamily.GroceryDad.scraper.model.Link;
import GroceryFamily.GroceryElders.domain.Product;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
@Builder
public class WorkerEventListener {
    private final Consumer<WorkerEvent<Product>> productHandler;
    @Builder.Default
    private final Consumer<WorkerEvent<Throwable>> errorHandler = event ->
            log.error("Failed to scrap {} URL '{}'",
                    event.platform,
                    event.link.url,
                    event.payload);

    public void product(String platform, Link link, Product product) {
        productHandler.accept(WorkerEvent
                .<Product>builder()
                .platform(platform)
                .link(link)
                .payload(product)
                .build());
    }

    public void error(String platform, Link link, Throwable error) {
        errorHandler.accept(WorkerEvent
                .<Throwable>builder()
                .platform(platform)
                .link(link)
                .payload(error)
                .build());
    }
}