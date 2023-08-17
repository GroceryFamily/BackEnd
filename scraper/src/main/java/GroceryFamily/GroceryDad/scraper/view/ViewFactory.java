package GroceryFamily.GroceryDad.scraper.view;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.model.Source;
import GroceryFamily.GroceryDad.scraper.view.barbora.BarboraViewFactory;
import GroceryFamily.GroceryDad.scraper.view.prisma.PrismaViewFactory;
import GroceryFamily.GroceryDad.scraper.view.rimi.RimiViewFactory;
import GroceryFamily.GroceryElders.domain.Namespace;
import com.codeborne.selenide.SelenideDriver;
import org.jsoup.nodes.Document;

import static java.lang.String.format;

public abstract class ViewFactory {
    protected final GroceryDadConfig.Scraper config;

    protected ViewFactory(GroceryDadConfig.Scraper config) {
        this.config = config;
    }

    public abstract LiveView liveView(SelenideDriver driver);

    public abstract CategoryView categoryView(Document document, Source selected);

    public abstract ProductListView productListView(Document document, Source selected);

    public abstract ProductView productView(Document document, Source selected);

    public static ViewFactory create(GroceryDadConfig.Scraper config) {
        return switch (config.namespace) {
            case Namespace.BARBORA -> new BarboraViewFactory(config);
            case Namespace.PRISMA -> new PrismaViewFactory(config);
            case Namespace.RIMI -> new RimiViewFactory(config);
            default -> throw new UnsupportedOperationException(format("Unrecognized namespace %s", config.namespace));
        };
    }
}