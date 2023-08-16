package GroceryFamily.GroceryDad.scraper.view.prisma;

import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.view.ProductListView;
import GroceryFamily.GroceryDad.scraper.view.View;
import lombok.experimental.SuperBuilder;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.stream.Stream;

import static GroceryFamily.GroceryDad.scraper.view.prisma.PrismaView.productCode;
import static java.util.Objects.requireNonNull;

@SuperBuilder
class PrismaProductListView extends View implements ProductListView {
    @Override
    public List<Link> productPageLinks() {
        return List.of();
    }

    @Override
    public List<Link> productLinks() {
        return items()
                .map(e -> Link
                        .builder()
                        .code(code(e))
                        .name(e.select("*[class=name]").text())
                        .url(url(e))
                        .source(selected)
                        .build())
                .toList();
    }

    private Stream<Element> items() {
        return document.select("li[data-ean]").stream();
    }

    private static String code(Element item) {
        return productCode(url(item));
    }

    private static String url(Element item) {
        return requireNonNull(item.select("a").first()).absUrl("href");
    }
}