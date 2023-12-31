package GroceryFamily.GroceryDad.scraper.view.rimi;

import GroceryFamily.GroceryDad.scraper.model.Link;
import GroceryFamily.GroceryDad.scraper.view.ProductListView;
import GroceryFamily.GroceryDad.scraper.view.View;
import lombok.experimental.SuperBuilder;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static GroceryFamily.GroceryDad.scraper.view.rimi.RimiView.productCode;
import static java.util.Objects.requireNonNull;

@SuperBuilder
class RimiProductListView extends View implements ProductListView {
    @Override
    public List<Link> productPageLinks() {
        return paginationItems().map(e -> {
            var pageNo = Integer.parseInt(e.text());
            var url = e.absUrl("href");
            return Link.productList(pageNo, url, selected);
        }).toList();
    }

    @Override
    public List<Link> productLinks() {
        return items().map(e -> {
            var url = requireNonNull(e.select("a").first()).absUrl("href");
            return Link
                    .builder()
                    .code(productCode(url))
                    .name(e.select("*[class*=name]").text())
                    .url(url)
                    .source(selected)
                    .build();
        }).toList();
    }

    private Stream<Element> paginationItems() {
        return pagination().stream().flatMap(e -> e.select("a:matches([0-9]+)").stream());
    }

    private Optional<Element> pagination() {
        return Optional.ofNullable(document.select("ul[class*=pagination]").first());
    }

    private Stream<Element> items() {
        return document.select("ul[class=product-grid] div[data-product-code]").stream();
    }
}