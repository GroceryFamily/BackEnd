package GroceryFamily.GroceryDad.scraper.page.view;

import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryElders.domain.Product;
import org.jsoup.nodes.Document;

public abstract class ProductView extends View {
    public ProductView(Document document, Link selected) {
        super(document, selected);
    }

    public abstract Product product();
}