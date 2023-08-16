package GroceryFamily.GroceryDad.scraper.view;

import GroceryFamily.GroceryDad.scraper.page.Source;
import org.jsoup.nodes.Document;

public interface ViewFactory {
    LiveView liveView();

    CategoryView categoryView(Document document, Source selected);

    ProductListView productListView(Document document, Source selected);

    ProductView productView(Document document, Source selected);
}