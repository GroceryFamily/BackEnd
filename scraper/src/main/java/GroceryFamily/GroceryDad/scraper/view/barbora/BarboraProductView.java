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
        origin().ifPresent(origin -> details.put(Detail.ORIGIN, origin));
        description().ifPresent(description -> details.put(Detail.DESCRIPTION, description));
        ingredients().ifPresent(ingredients -> details.put(Detail.INGREDIENTS, ingredients));

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

    private Optional<String> origin() {
        var origin = document.select("dt:contains(origin)").next().text();
        return isBlank(origin) ? Optional.empty() : Optional.of(origin);
    }

    private Optional<String> description() {
        var description = document.select("dd[itemprop=description]").text();
        return isBlank(description) ? Optional.empty() : Optional.of(description);
    }

    private Optional<String> ingredients() {
        var ingredients = document.select("dt:contains(ingredients)").next().text();
        return isBlank(ingredients) ? Optional.empty() : Optional.of(ingredients);
    }
}