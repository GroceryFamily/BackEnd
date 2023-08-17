package GroceryFamily.GroceryDad.scraper.view.rimi;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.model.Source;
import GroceryFamily.GroceryDad.scraper.view.*;
import com.codeborne.selenide.SelenideDriver;
import org.jsoup.nodes.Document;

public class RimiViewFactory extends ViewFactory {
    public RimiViewFactory(GroceryDadConfig.Scraper config) {
        super(config);
    }

    @Override
    public LiveView liveView(SelenideDriver driver) {
        return RimiLiveView
                .builder()
                .driver(driver)
                .timeout(config.timeout)
                .sleepDelay(config.sleepDelay)
                .build();
    }

    @Override
    public CategoryView categoryView(Document document, Source selected) {
        return RimiCategoryView
                .builder()
                .document(document)
                .selected(selected)
                .build();
    }

    @Override
    public ProductListView productListView(Document document, Source selected) {
        return RimiProductListView
                .builder()
                .document(document)
                .selected(selected)
                .build();
    }

    @Override
    public ProductView productView(Document document, Source selected) {
        return RimiProductView
                .builder()
                .document(document)
                .selected(selected)
                .build();
    }
}