package GroceryFamily.GroceryDad.scraper.worker;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.cache.HTMLCache;
import GroceryFamily.GroceryDad.scraper.model.Allowlist;
import GroceryFamily.GroceryDad.scraper.model.Path;
import GroceryFamily.GroceryDad.scraper.view.ViewFactory;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
public class WorkerFactory {
    private final GroceryDadConfig dadConfig;
    private final HTMLCache cacheFactoryFactory;

    WorkerFactory(GroceryDadConfig dadConfig, HTMLCache cacheFactoryFactory) {
        this.dadConfig = dadConfig;
        this.cacheFactoryFactory = cacheFactoryFactory;
    }

    public Worker worker(String platform, WorkerEventListener listener) {
        var config = workerConfig(platform);
        return Worker
                .builder()
                .platform(platform)
                .config(config)
                .htmlCache(cacheFactoryFactory)
                .viewFactory(ViewFactory.create(config))
                .allowlist(allowlist(config))
                .listener(listener)
                .build();
    }

    private GroceryDadConfig.Platform workerConfig(String platform) {
        if (!dadConfig.platforms.containsKey(platform)) {
            throw new IllegalArgumentException(format("Missing %s config", platform));
        }
        return dadConfig.platforms.get(platform);
    }

    private static Allowlist allowlist(GroceryDadConfig.Platform config) {
        var allowlist = new Allowlist();
        config.allowlist.stream().map(Path::of).forEach(allowlist::put);
        return allowlist;
    }
}