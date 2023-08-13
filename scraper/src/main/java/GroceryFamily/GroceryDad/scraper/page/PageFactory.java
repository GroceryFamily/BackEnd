package GroceryFamily.GroceryDad.scraper.page;

import org.jsoup.nodes.Document;

public class PageFactory {
    private final Context context;

    public PageFactory(Context context) {
        this.context = context;
    }

    public NewPage page(Node node, Document document) {
        return NewPage
                .builder()
                .node(node)
                .document(document)
                .context(context)
                .build();
    }
}