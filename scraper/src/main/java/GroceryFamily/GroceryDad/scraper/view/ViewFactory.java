package GroceryFamily.GroceryDad.scraper.view;

import GroceryFamily.GroceryDad.scraper.context.barbora.BarboraViewFactory;
import GroceryFamily.GroceryDad.scraper.context.prisma.PrismaViewFactory;
import GroceryFamily.GroceryDad.scraper.context.rimi.RimiViewFactory;
import GroceryFamily.GroceryDad.scraper.page.Source;
import GroceryFamily.GroceryElders.domain.Namespace;
import org.jsoup.nodes.Document;

import java.util.Map;
import java.util.function.Supplier;

import static java.lang.String.format;

public interface ViewFactory {
    Map<String, Supplier<ViewFactory>> FACTORIES = Map.of(
            Namespace.BARBORA, BarboraViewFactory::new,
            Namespace.PRISMA, PrismaViewFactory::new,
            Namespace.RIMI, RimiViewFactory::new);

    LiveView liveView();

    CategoryView categoryView(Document document, Source selected);

    ProductListView productListView(Document document, Source selected);

    ProductView productView(Document document, Source selected);

    static ViewFactory get(String namespace) {
        var factory = FACTORIES.get(namespace);
        if (factory == null) throw new UnsupportedOperationException(format("Unrecognized namespace '%s'", namespace));
        return factory.get();
    }
}