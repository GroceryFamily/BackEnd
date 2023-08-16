package GroceryFamily.GroceryDad.scraper.view;

import GroceryFamily.GroceryDad.scraper.page.Source;
import lombok.experimental.SuperBuilder;
import org.jsoup.nodes.Document;

@SuperBuilder
public class View {
    protected final Document document;
    protected final Source selected;
}