package GroceryFamily.GroceryDad.scraper.view.barbora;

import GroceryFamily.GroceryDad.scraper.view.ProductView;
import GroceryFamily.GroceryDad.scraper.view.View;
import GroceryFamily.GroceryElders.domain.Detail;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GroceryElders.domain.Product;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static GroceryFamily.GroceryDad.scraper.view.barbora.BarboraView.productCode;

@SuperBuilder
class BarboraProductView extends View implements ProductView {
    @Override
    public Product product() {
        return Product
                .builder()
                .namespace(Namespace.BARBORA)
                .code(productCode(selected.url))
                .name(document.select("*[class=b-product-info--title]").text())
                .url(selected.url)
                // todo: set prices
                .categories(selected.categories())
                .details(details())
                .build();
    }

    private Map<String, String> details() {
        var details = new HashMap<String, String>();
        var container = Optional.ofNullable(document.select("div[class*=page-container]").first());
        container.map(e -> e.select("img[itemprop=image]").attr("src")).filter(StringUtils::isNotBlank)
                .ifPresent(detail -> details.put(Detail.IMAGE, detail));

        var info1 = container.map(e -> e.select("dl[class*=b-product-info--info1]").first());
        info1.map(e -> e.select("dt:contains(brand)").next().text()).filter(StringUtils::isNotBlank)
                .ifPresent(detail -> details.put(Detail.BRAND, detail));
        info1.map(e -> e.select("dt:contains(origin)").next().text()).filter(StringUtils::isNotBlank)
                .ifPresent(detail -> details.put(Detail.ORIGIN, detail));

        var info2 = container.map(e -> e.select("dl[class*=b-product-info--info-2]").first());
        info2.map(e -> e.select("dd[itemprop=description]").text()).filter(StringUtils::isNotBlank)
                .ifPresent(detail -> details.put(Detail.DESCRIPTION, detail));
        info2.map(e -> e.select("dt:contains(ingredients)").next().text()).filter(StringUtils::isNotBlank)
                .ifPresent(detail -> details.put(Detail.INGREDIENTS, detail));
        return details;
    }
}