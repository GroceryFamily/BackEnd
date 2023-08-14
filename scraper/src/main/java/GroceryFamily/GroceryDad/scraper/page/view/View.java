package GroceryFamily.GroceryDad.scraper.page.view;

import GroceryFamily.GroceryDad.scraper.page.Link;
import org.jsoup.nodes.Document;

class View {
    protected final Document document;
    protected final Link selected;

    protected View(Document document, Link selected) {
        this.document = document;
        this.selected = selected;
    }
}