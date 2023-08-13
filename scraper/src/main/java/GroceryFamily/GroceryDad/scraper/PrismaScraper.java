package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.page.Node;
import GroceryFamily.GroceryDad.scraper.page.context.PrismaContext;
import GroceryFamily.GroceryElders.domain.Product;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
@SuperBuilder
class PrismaScraper extends Scraper {
    @Override
    protected void scrap(Consumer<Product> handler) {
        Node.root(rootURL(), new PrismaContext(cacheFactory(), categoryPermissions)).traverse(handler);
    }
}