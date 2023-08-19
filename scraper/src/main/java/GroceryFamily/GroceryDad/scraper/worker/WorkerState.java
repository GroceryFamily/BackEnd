package GroceryFamily.GroceryDad.scraper.worker;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.driver.LazyDriver;
import GroceryFamily.GroceryDad.scraper.model.Path;
import GroceryFamily.GroceryDad.scraper.model.SourceTree;
import com.codeborne.selenide.SelenideDriver;

import java.util.HashSet;
import java.util.Set;

class WorkerState {
    final Set<Path<String>> seen = new HashSet<>();
    final SourceTree visited = new SourceTree();
    private final LazyDriver driver;

    WorkerState(GroceryDadConfig.Platform.Live config) {
        driver = new LazyDriver(config);
    }

    SelenideDriver driver() {
        return driver.get();
    }

    void destroy() {
        driver.destroy();
    }
}