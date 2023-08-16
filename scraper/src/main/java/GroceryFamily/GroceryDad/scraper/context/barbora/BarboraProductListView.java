package GroceryFamily.GroceryDad.scraper.context.barbora;

import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.view.ProductListView;
import GroceryFamily.GroceryDad.scraper.view.View;
import lombok.experimental.SuperBuilder;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static GroceryFamily.GroceryDad.scraper.context.barbora.BarboraView.productCode;

@SuperBuilder
class BarboraProductListView extends View implements ProductListView {
    @Override
    public List<Link> productPageLinks() {
        return productListPaginationItems().map(e -> {
            var pageNo = Integer.parseInt(e.text());
            var url = e.absUrl("href");
            return Link.productList(pageNo, url, selected);
        }).toList();
    }

    @Override
    public List<Link> productLinks() {
        return productItems().map(e -> {
            var url = e.absUrl("href");
            return Link
                    .builder()
                    .code(productCode(url))
                    .name(e.text())
                    .url(url)
                    .source(selected)
                    .build();
        }).toList();
    }

    private Stream<Element> productListPaginationItems() {
        return productListPagination().stream().flatMap(e -> e.select("a:matches([0-9]+)").stream());
    }

    private Optional<Element> productListPagination() {
        return Optional.ofNullable(document.select("ul[class=pagination]").first());
    }

    private Stream<Element> productItems() {
        return document.select("div[itemtype*=Product] a[class*=title]").stream();
    }
}