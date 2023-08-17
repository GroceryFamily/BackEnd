package GroceryFamily.GroceryDad.scraper.view.barbora;

import GroceryFamily.GroceryDad.scraper.model.Source;
import GroceryFamily.GroceryDad.scraper.view.*;
import com.codeborne.selenide.SelenideDriver;
import org.jsoup.nodes.Document;

public class BarboraViewFactory implements ViewFactory {
    @Override
    public LiveView liveView(SelenideDriver driver) {
        return BarboraLiveView
                .builder()
                .driver(driver)
                .build();
    }

    @Override
    public CategoryView categoryView(Document document, Source selected) {
        return BarboraCategoryView
                .builder()
                .document(document)
                .selected(selected)
                .build();
    }

    @Override
    public ProductListView productListView(Document document, Source selected) {
        return BarboraProductListView
                .builder()
                .document(document)
                .selected(selected)
                .build();
    }

    @Override
    public ProductView productView(Document document, Source selected) {
        return BarboraProductView
                .builder()
                .document(document)
                .selected(selected)
                .build();
    }
}