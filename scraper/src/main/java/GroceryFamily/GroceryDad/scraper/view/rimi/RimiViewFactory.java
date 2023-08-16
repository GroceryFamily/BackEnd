package GroceryFamily.GroceryDad.scraper.view.rimi;

import GroceryFamily.GroceryDad.scraper.page.Source;
import GroceryFamily.GroceryDad.scraper.view.*;
import org.jsoup.nodes.Document;

public class RimiViewFactory implements ViewFactory {
    @Override
    public LiveView liveView() {
        return RimiLiveView.INSTANCE;
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