package GroceryFamily.GroceryDad.scraper.view.prisma;

import GroceryFamily.GroceryDad.scraper.view.ProductView;
import GroceryFamily.GroceryDad.scraper.view.View;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GroceryElders.domain.Product;
import lombok.experimental.SuperBuilder;

import static GroceryFamily.GroceryDad.scraper.view.prisma.PrismaView.productCode;

@SuperBuilder
class PrismaProductView extends View implements ProductView {
    @Override
    public Product product() {
        return Product
                .builder()
                .namespace(Namespace.PRISMA)
                .code(productCode(selected.url))
                .name(document.select("#product-name").text())
                .url(selected.url)
                // todo: set prices
                .categories(selected.categories())
                .build();
    }

}