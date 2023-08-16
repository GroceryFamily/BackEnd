package GroceryFamily.GroceryDad.scraper.context;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.context.rimi.RimiCategoryView;
import GroceryFamily.GroceryDad.scraper.context.rimi.RimiProductListView;
import GroceryFamily.GroceryDad.scraper.context.rimi.RimiProductView;
import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.page.Source;
import GroceryFamily.GroceryElders.domain.Product;
import org.jsoup.nodes.Document;

import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RimiContext extends Context {
    private boolean initialized;

    public RimiContext(GroceryDadConfig.Scraper config) {
        super(config);
    }

    @Override
    protected void initialize() {
        if (!initialized) {
            acceptOrRejectCookies();
            initialized = true;
        }
    }

    @Override
    public List<Link> childCategoryLinks(Document document, Source selected) {
        return RimiCategoryView
                .builder()
                .document(document)
                .selected(selected)
                .build()
                .childCategoryLinks();
    }

    @Override
    public List<Link> productPageLinks(Document document, Source selected) {
        return RimiProductListView
                .builder()
                .document(document)
                .selected(selected)
                .build()
                .productPageLinks();
    }

    @Override
    public List<Link> productLinks(Document document, Source selected) {
        return RimiProductListView
                .builder()
                .document(document)
                .selected(selected)
                .build()
                .productLinks();
    }

    @Override
    public Product product(Document document, Source selected) {
        return RimiProductView
                .builder()
                .document(document)
                .selected(selected)
                .build()
                .product();
    }

    private static void acceptOrRejectCookies() {
        $("#CybotCookiebotDialogBodyLevelButtonLevelOptinDeclineAll")
                .shouldBe(visible)
                .click();
    }
}