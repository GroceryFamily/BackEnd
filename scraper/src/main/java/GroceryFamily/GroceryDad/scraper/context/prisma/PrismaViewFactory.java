package GroceryFamily.GroceryDad.scraper.context.prisma;

import GroceryFamily.GroceryDad.scraper.page.Source;
import GroceryFamily.GroceryDad.scraper.view.*;
import org.jsoup.nodes.Document;

public class PrismaViewFactory implements ViewFactory {
    @Override
    public LiveView liveView() {
        return PrismaLiveView.INSTANCE;
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