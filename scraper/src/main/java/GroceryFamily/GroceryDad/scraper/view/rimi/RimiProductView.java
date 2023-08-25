package GroceryFamily.GroceryDad.scraper.view.rimi;

import GroceryFamily.GroceryDad.scraper.view.Lazy;
import GroceryFamily.GroceryDad.scraper.view.ProductView;
import GroceryFamily.GroceryDad.scraper.view.View;
import GroceryFamily.GroceryElders.domain.Detail;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GroceryElders.domain.Product;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import java.util.HashMap;

@SuperBuilder
class RimiProductView extends View implements ProductView {
    private final Lazy<Element> content = new Lazy<>(() -> document.select("div[class=cart-layout__main]").first());
    private final Lazy<Element> main = content.map(e -> e.select("div[class=container]").first());
    private final Lazy<Element> extra = content.map(e -> e.select("#details").first());
    private final Lazy<String> name = main.map(e -> e.select("h3[class=name]").text()).filter(StringUtils::isNotBlank);
    private final Lazy<String> brand = extra.map(e -> e.select("span:contains(brand)").next().text()).filter(StringUtils::isNotBlank);
    private final Lazy<String> image = main.map(e -> e.select("img").first()).map(e -> e.absUrl("src")).filter(StringUtils::isNotBlank);
    private final Lazy<String> origin = extra.map(e -> e.select("span:contains(origin)").next().text()).filter(StringUtils::isNotBlank);
    private final Lazy<String> description = extra.map(e -> e.select("span:contains(description)").next().text()).filter(StringUtils::isNotBlank);
    private final Lazy<String> ingredients = extra.map(e -> e.select("p:contains(ingredients)").next().text()).filter(StringUtils::isNotBlank);

    @Override
    public Product product() {
        var details = new HashMap<String, String>();
        brand.ifPresent(detail -> details.put(Detail.BRAND, detail));
        image.ifPresent(detail -> details.put(Detail.IMAGE, detail));
        origin.ifPresent(detail -> details.put(Detail.ORIGIN, detail));
        description.ifPresent(detail -> details.put(Detail.DESCRIPTION, detail));
        ingredients.ifPresent(detail -> details.put(Detail.INGREDIENTS, detail));

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
}