package GroceryFamily.GroceryDad.scraper.view.rimi;

import GroceryFamily.GroceryDad.scraper.view.ProductView;
import GroceryFamily.GroceryDad.scraper.view.View;
import GroceryFamily.GroceryElders.domain.Detail;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GroceryElders.domain.Product;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@SuperBuilder
class RimiProductView extends View implements ProductView {
    @Override
    public Product product() {
        var details = new HashMap<String, String>();
        brand().ifPresent(brand -> details.put(Detail.BRAND, brand));
        origin().ifPresent(origin -> details.put(Detail.ORIGIN, origin));
        ingredients().ifPresent(ingredients -> details.put(Detail.INGREDIENTS, ingredients));

        return Product
                .builder()
                .namespace(Namespace.RIMI)
                .code(RimiView.productCode(selected.url))
                .name(document.select("h3[class=name]").text())
                .url(selected.url)
                // todo: set prices
                .categories(selected.categories())
                .details(details)
                .build();
    }

    private Optional<String> brand() {
        var brand = document.select("#details span:contains(brand)").next().text();
        return isBlank(brand) ? Optional.empty() : Optional.of(brand);
    }

    private Optional<String> origin() {
        var origin = document.select("span:contains(origin)").next().text();
        return isBlank(origin) ? Optional.empty() : Optional.of(origin);
    }

    private Optional<String> ingredients() {
        var ingredients = document.select("p:contains(ingredients)").next().text();
        return isBlank(ingredients) ? Optional.empty() : Optional.of(ingredients);
    }
}