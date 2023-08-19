package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.worker.WorkerEventListener;
import GroceryFamily.GroceryDad.scraper.worker.WorkerFactory;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.openqa.selenium.chrome.ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY;

// todo: think about robots.txt
//  https://en.wikipedia.org/wiki/Robots.txt
//  https://github.com/google/robotstxt-java
//  https://developers.google.com/search/docs/crawling-indexing/robots/robots_txt
@Slf4j
@Component
public class Scraper {
    private final GroceryDadConfig config;
    private final WorkerFactory workerFactory;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    static {
        System.setProperty(CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");
    }

    Scraper(GroceryDadConfig config, WorkerFactory workerFactory) {
        this.config = config;
        this.workerFactory = workerFactory;
    }

    public void scrap(WorkerEventListener listener) {
        var finish = new CountDownLatch(config.enabledPlatforms.size());
        for (var platform : config.enabledPlatforms) {
            threadPool.execute(() -> {
                Thread.currentThread().setName(platform + "-worker");
                try {
                    var worker = workerFactory.worker(platform, listener);
                    var visited = worker.traverse();
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

    @PreDestroy
    void destroy() {
        threadPool.shutdown();
    }
}