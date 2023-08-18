package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.cache.CacheFactory;
import GroceryFamily.GroceryDad.scraper.model.Listener;
import GroceryFamily.GroceryDad.scraper.model.Worker;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private final CacheFactory cacheFactory;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    static {
        System.setProperty(CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");
    }

    Scraper(GroceryDadConfig config, CacheFactory cacheFactory) {
        this.config = config;
        this.cacheFactory = cacheFactory;
    }

    public void scrap(Listener listener) {
        var finish = new CountDownLatch(config.enabledPlatforms.size());
        for (var platform : config.enabledPlatforms) {
            threadPool.execute(() -> {
                Thread.currentThread().setName(platform + "-worker");
                try {
                    var visited = Worker.traverse(platform, workerConfig(platform), cacheFactory, listener);
                    log.info("Visited {} links: \n{}", platform, visited);
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

    private GroceryDadConfig.Platform workerConfig(String name) {
        if (!config.platforms.containsKey(name)) {
            throw new IllegalArgumentException(format("Missing %s config", name));
        }
        return config.platforms.get(name);
    }

    @PreDestroy
    void destroy() {
        threadPool.shutdown();
    }
}