package GroceryFamily.GroceryDad.scraper.context.rimi;

import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.view.ProductListView;
import GroceryFamily.GroceryDad.scraper.view.View;
import lombok.experimental.SuperBuilder;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static GroceryFamily.GroceryDad.scraper.context.rimi.RimiView.productCode;
import static java.util.Objects.requireNonNull;

@SuperBuilder
public class RimiProductListView extends View implements ProductListView {
    @Override
    public List<Link> productPageLinks() {
        return productListPaginationItems().map(item -> {
            var pageNo = Integer.parseInt(item.text());
            var url = item.absUrl("href");
            return Link.productList(pageNo, url, selected);
        }).toList();
    }

    @Override
    public List<Link> productLinks() {
        return productListItems().map(item -> {
            var url = requireNonNull(item.select("a").first()).absUrl("href");
            return Link
                    .builder()
                    .code(productCode(url))
                    .name(item.select("*[class*=name]").text())
                    .url(url)
                    .source(selected)
                    .build();
        }).toList();
    }

    private Stream<Element> productListPaginationItems() {
        return productListPagination().stream().flatMap(e -> e.select("a:matches([0-9]+)").stream());
    }

    private Optional<Element> productListPagination() {
        return Optional.ofNullable(document.select("ul[class*=pagination]").first());
    }

    private Stream<Element> productListItems() {
        return productList().stream().flatMap(e -> e.select("div[data-product-code]").stream());
    }

    private Optional<Element> productList() {
        return Optional.ofNullable(document.select("ul[class=product-grid]").first());
    }
}