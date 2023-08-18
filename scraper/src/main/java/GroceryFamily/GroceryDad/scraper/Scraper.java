package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.model.Worker;
import GroceryFamily.GroceryElders.domain.Product;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static java.lang.String.format;
import static org.openqa.selenium.chrome.ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY;

// todo: think about robots.txt
//  https://en.wikipedia.org/wiki/Robots.txt
//  https://github.com/google/robotstxt-java
//  https://developers.google.com/search/docs/crawling-indexing/robots/robots_txt
@Slf4j
@Component
public class Scraper {
    private final GroceryDadConfig config;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    static {
        System.setProperty(CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");
    }

    Scraper(GroceryDadConfig config) {
        this.config = config;
    }

    public void scrap(Consumer<Product> handler) {
        var finish = new CountDownLatch(config.enabled.size());
        for (var name : config.enabled) {
            threadPool.execute(() -> {
                Thread.currentThread().setName(name + "-worker");
                try {
                    var visited = Worker.traverse(workerConfig(name), handler);
                    log.info("Visited {} links: \n{}", name, visited);
                } finally {
                    finish.countDown();
                }
            });
        }
        try {
            finish.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private GroceryDadConfig.Scraper workerConfig(String name) {
        if (!config.scrapers.containsKey(name)) {
            throw new IllegalArgumentException(format("Missing %s config", name));
        }
        return config.scrapers.get(name);
    }

    @PreDestroy
    void destroy() {
        threadPool.shutdown();
    }
}