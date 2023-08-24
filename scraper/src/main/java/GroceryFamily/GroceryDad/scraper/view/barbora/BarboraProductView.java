package GroceryFamily.GroceryDad.scraper.view.barbora;

import GroceryFamily.GroceryDad.scraper.view.ProductView;
import GroceryFamily.GroceryDad.scraper.view.View;
import GroceryFamily.GroceryElders.domain.Detail;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GroceryElders.domain.Product;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Optional;

import static GroceryFamily.GroceryDad.scraper.view.barbora.BarboraView.productCode;
import static org.apache.commons.lang3.StringUtils.isBlank;

@SuperBuilder
class BarboraProductView extends View implements ProductView {
    @Override
    public Product product() {
        var details = new HashMap<String, String>();
        brand().ifPresent(brand -> details.put(Detail.BRAND, brand));

        return Product
                .builder()
                .namespace(Namespace.BARBORA)
                .code(productCode(selected.url))
                .name(document.select("*[class=b-product-info--title]").text())
                .url(selected.url)
                // todo: set prices
                .categories(selected.categories())
                .details(details)
                .build();
    }

    private Optional<String> brand() {
        var brand = document.select("dt:contains(brand)").next().text();
        return isBlank(brand) ? Optional.empty() : Optional.of(brand);
    }
}