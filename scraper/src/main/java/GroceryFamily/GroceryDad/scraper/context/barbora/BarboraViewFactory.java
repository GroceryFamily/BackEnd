package GroceryFamily.GroceryDad.scraper.context.barbora;

import GroceryFamily.GroceryDad.scraper.page.Source;
import GroceryFamily.GroceryDad.scraper.view.*;
import org.jsoup.nodes.Document;

public class BarboraViewFactory implements ViewFactory {
    @Override
    public LiveView liveView() {
        return BarboraLiveView.INSTANCE;
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