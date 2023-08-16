package GroceryFamily.GroceryDad.scraper.context.barbora;

import GroceryFamily.GroceryDad.scraper.view.ProductView;
import GroceryFamily.GroceryDad.scraper.view.View;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GroceryElders.domain.Product;
import lombok.experimental.SuperBuilder;

import static GroceryFamily.GroceryDad.scraper.context.barbora.BarboraView.productCode;

@SuperBuilder
public class BarboraProductView extends View implements ProductView {
    @Override
    public Product product() {
        return Product
                .builder()
                .namespace(Namespace.BARBORA)
                .code(productCode(selected.url))
                .name(document.select("*[class=b-product-info--title]").text())
                .url(selected.url)
                // todo: set prices and categories
                .build();
    }
}