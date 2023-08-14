package GroceryFamily.GroceryDad.scraper.page.view;

import GroceryFamily.GroceryDad.scraper.page.Link;
import lombok.Builder;
import org.jsoup.nodes.Document;

import java.util.List;

public abstract class ProductListView extends View {
    public ProductListView(Document document, Link selected) {
        super(document, selected);
    }

    public abstract List<Link> productLinks();

    public abstract List<Link> productPageLinks();

    @Builder
    protected static class Page {
        public final List<Link> links;
        public final int number;
    }
}