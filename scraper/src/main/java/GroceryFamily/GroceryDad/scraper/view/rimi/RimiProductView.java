package GroceryFamily.GroceryDad.scraper.view.rimi;

import GroceryFamily.GroceryDad.scraper.view.ProductView;
import GroceryFamily.GroceryDad.scraper.view.View;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GroceryElders.domain.Product;
import lombok.experimental.SuperBuilder;

@SuperBuilder
class RimiProductView extends View implements ProductView {
    @Override
    public Product product() {
        return Product
                .builder()
                .namespace(Namespace.RIMI)
                .code(RimiView.productCode(selected.url))
                .name(document.select("h3[class=name]").text())
                .url(selected.url)
                // todo: set prices and categories
                .build();
    }
}