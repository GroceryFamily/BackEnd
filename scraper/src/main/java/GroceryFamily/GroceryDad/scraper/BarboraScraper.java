package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.page.Node;
import GroceryFamily.GroceryDad.scraper.page.context.BarboraContext;
import GroceryFamily.GroceryElders.domain.Currency;
import GroceryFamily.GroceryElders.domain.Price;
import GroceryFamily.GroceryElders.domain.PriceUnit;
import GroceryFamily.GroceryElders.domain.Product;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.function.Consumer;

import static java.lang.String.format;

@Slf4j
@SuperBuilder
class BarboraScraper extends Scraper {
    @Override
    protected void scrap(Consumer<Product> handler) {
        Node.root(rootURL(), new BarboraContext(cacheFactory(), categoryPermissions)).traverse(handler);
    }

    // €2.29
    static Price pcPrice(String text) {
        return Price
                .builder()
                .unit(PriceUnit.PC)
                .currency(currency(text.substring(0, 1)))
                .amount(new BigDecimal(text.substring(1)))
                .build();
    }

    // €1.15/l
    static Price price(String text) {
        var fragments = text.substring(1).split("/");
        return Price
                .builder()
                .unit(PriceUnit.normalize(fragments[1]))
                .currency(currency(text.substring(0, 1)))
                .amount(new BigDecimal(fragments[0]))
                .build();
    }

    static String currency(String symbol) {
        if (symbol == null) throw new IllegalArgumentException("Currency symbol is missing");
        if (symbol.equals("€")) return Currency.EUR;
        throw new UnsupportedOperationException(format("Currency symbol '%s' is not recognized", symbol));
    }
}