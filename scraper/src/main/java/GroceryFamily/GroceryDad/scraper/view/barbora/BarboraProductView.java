package GroceryFamily.GroceryDad.scraper.view.barbora;

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

import static GroceryFamily.GroceryDad.scraper.view.barbora.BarboraView.productCode;

@SuperBuilder
class BarboraProductView extends View implements ProductView {
    private final Lazy<Element> content = new Lazy<>(() -> document.select("div[class*=page-container]").first());
    private final Lazy<Element> info1 = content.map(e -> e.select("dl[class*=b-product-info--info1]").first());
    private final Lazy<Element> info2 = content.map(e -> e.select("dl[class*=b-product-info--info-2]").first());
    private final Lazy<String> name = content.map(e -> e.select("h1[class=b-product-info--title]").text()).filter(StringUtils::isNotBlank);
    private final Lazy<String> brand = info1.map(e -> e.select("dt:contains(brand)").next().text()).filter(StringUtils::isNotBlank);
    private final Lazy<String> image = content.map(e -> e.select("img[itemprop=image]").first()).map(e -> e.absUrl("src")).filter(StringUtils::isNotBlank);
    private final Lazy<String> origin = info1.map(e -> e.select("dt:contains(origin)").next().text()).filter(StringUtils::isNotBlank);
    private final Lazy<String> description = info2.map(e -> e.select("dd[itemprop=description]").text()).filter(StringUtils::isNotBlank);
    private final Lazy<String> ingredients = info2.map(e -> e.select("dt:contains(ingredients)").next().text()).filter(StringUtils::isNotBlank);

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
                .namespace(Namespace.BARBORA)
                .code(productCode(selected.url))
                .name(name.get())
                .url(selected.url)
                // todo: set prices
                .categories(selected.categories())
                .details(details)
                .build();
    }
}