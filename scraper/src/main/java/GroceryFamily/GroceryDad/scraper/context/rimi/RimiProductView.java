package GroceryFamily.GroceryDad.scraper.context.rimi;

import GroceryFamily.GroceryDad.scraper.view.ProductView;
import GroceryFamily.GroceryDad.scraper.view.View;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GroceryElders.domain.Product;
import lombok.experimental.SuperBuilder;

import static GroceryFamily.GroceryDad.scraper.context.rimi.RimiView.productCode;

@SuperBuilder
class RimiProductView extends View implements ProductView {
    @Override
    public Product product() {
        return Product
                .builder()
                .namespace(Namespace.RIMI)
                .code(productCode(selected.url))
                .name(document.select("h3[class=name]").text())
                .url(selected.url)
                // todo: set prices and categories
                .build();
    }
}