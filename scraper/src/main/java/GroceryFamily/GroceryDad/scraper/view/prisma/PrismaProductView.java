package GroceryFamily.GroceryDad.scraper.view.prisma;

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

import static GroceryFamily.GroceryDad.scraper.view.prisma.PrismaView.productCode;

@SuperBuilder
class PrismaProductView extends View implements ProductView {
    private final Lazy<Element> content = new Lazy<>(() -> document.select("div[itemtype*=product]").first());
    private final Lazy<Element> main = content.map(e -> e.select("#product-data").first());
    private final Lazy<Element> extra = content.map(e -> e.select("#info").first());
    private final Lazy<String> name = main.map(e -> e.select("#product-name").text()).filter(StringUtils::isNotBlank);
    private final Lazy<String> brand = main.map(e -> e.select("#product-subname").text()).filter(StringUtils::isNotBlank);
    private final Lazy<String> image = content.map(e -> e.select("#product-image-zoom").first()).map(e -> e.absUrl("src")).filter(StringUtils::isNotBlank);
    private final Lazy<String> origin = extra.map(e -> e.select("h3:contains(origin)").next().text()).filter(StringUtils::isNotBlank);
    private final Lazy<String> description = extra.map(e -> e.select("p[itemprop=description]").text()).filter(StringUtils::isNotBlank);
    private final Lazy<String> ingredients = extra.map(e -> e.select("#product-ingredients").text()).filter(StringUtils::isNotBlank);
    private final Lazy<String> ean = main.map(e -> e.select("span[itemprop=sku]").text()).filter(StringUtils::isNotBlank);

    @Override
    public Product product() {
        var details = new HashMap<String, String>();
        brand.ifPresent(detail -> details.put(Detail.BRAND, detail));
        image.ifPresent(detail -> details.put(Detail.IMAGE, detail));
        origin.ifPresent(detail -> details.put(Detail.ORIGIN, detail));
        description.ifPresent(detail -> details.put(Detail.DESCRIPTION, detail));
        ingredients.ifPresent(detail -> details.put(Detail.INGREDIENTS, detail));
        ean.ifPresent(detail -> details.put(Detail.EAN, detail));

        return Product
                .builder()
                .namespace(Namespace.PRISMA)
                .code(productCode(selected.url))
                .name(name.get())
                .url(selected.url)
                // todo: set prices
                .categories(selected.categories())
                .details(details)
                .build();
    }
}