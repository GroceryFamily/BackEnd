package GroceryFamily.GroceryDad.scraper.context.prisma;

import GroceryFamily.GroceryDad.scraper.view.ProductView;
import GroceryFamily.GroceryDad.scraper.view.View;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GroceryElders.domain.Product;
import lombok.experimental.SuperBuilder;

import static GroceryFamily.GroceryDad.scraper.context.prisma.PrismaView.productCode;

@SuperBuilder
public class PrismaProductView extends View implements ProductView {
    @Override
    public Product product() {
        return Product
                .builder()
                .namespace(Namespace.PRISMA)
                .code(productCode(selected.url))
                .name(document.select("#product-name").text())
                .url(selected.url)
                // todo: set prices and categories
                .build();
    }

}