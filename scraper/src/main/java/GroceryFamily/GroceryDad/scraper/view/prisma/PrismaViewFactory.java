package GroceryFamily.GroceryDad.scraper.view.prisma;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.model.Source;
import GroceryFamily.GroceryDad.scraper.view.*;
import com.codeborne.selenide.SelenideDriver;
import org.jsoup.nodes.Document;

public class PrismaViewFactory extends ViewFactory {
    public PrismaViewFactory(GroceryDadConfig.Scraper config) {
        super(config);
    }

    @Override
    public LiveView liveView(SelenideDriver driver) {
        return PrismaLiveView
                .builder()
                .driver(driver)
                .timeout(config.timeout)
                .sleepDelay(config.sleepDelay)
                .build();
    }

    @Override
    public CategoryView categoryView(Document document, Source selected) {
        return PrismaCategoryView
                .builder()
                .document(document)
                .selected(selected)
                .build();
    }

    @Override
    public ProductListView productListView(Document document, Source selected) {
        return PrismaProductListView
                .builder()
                .document(document)
                .selected(selected)
                .build();
    }

    @Override
    public ProductView productView(Document document, Source selected) {
        return PrismaProductView
                .builder()
                .document(document)
                .selected(selected)
                .build();
    }
}