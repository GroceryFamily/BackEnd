package GroceryFamily.GroceryDad.scraper.view;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.model.Source;
import com.codeborne.selenide.SelenideDriver;
import org.jsoup.nodes.Document;

public abstract class ViewFactory {
    protected final GroceryDadConfig.Scraper config;

    protected ViewFactory(GroceryDadConfig.Scraper config) {
        this.config = config;
    }

    public abstract LiveView liveView(SelenideDriver driver);

    public abstract CategoryView categoryView(Document document, Source selected);

    public abstract ProductListView productListView(Document document, Source selected);

    public abstract ProductView productView(Document document, Source selected);
}