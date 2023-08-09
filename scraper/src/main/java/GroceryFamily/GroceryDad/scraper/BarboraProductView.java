package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.tree.CategoryTreePath;
import GroceryFamily.GroceryElders.domain.*;
import com.codeborne.selenide.SelenideElement;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.codeborne.selenide.Condition.cssClass;
import static java.lang.String.format;

record BarboraProductView(SelenideElement e) {
    Product product(CategoryTreePath path) {
        return Product
                .builder()
                .namespace(Namespace.BARBORA)
                .code(code())
                .name(name())
                .prices(prices())
                .categories(path.categories())
                .build();
    }

    private String code() {
        return e.$("div").attr("data-b-item-id");
    }

    private String url() {
        return e.$("a").attr("href");
    }

    private String name() {
        return e.$("*[itemprop='name']").text();
    }

    private Set<Price> prices() {
        var prices = new HashSet<Price>();
        Optional.ofNullable(firstPrice()).ifPresent(prices::add);
        Optional.ofNullable(secondPrice()).ifPresent(prices::add);
        return prices;
    }

    /*
     <span class="b-product-price-current-number" itemprop="price" content="1.19">€1.19</span>
     */
    private Price firstPrice() {
        var pe = e.$("*[itemprop=price]");
        if (!pe.exists()) return null;
        var text = pe.text();
        return Price
                .builder()
                .unit(PriceUnit.PC)
                .currency(currency(text.substring(0, 1)))
                .amount(new BigDecimal(text.substring(1)))
                .build();
    }

    /*
     <div class="b-product-price--extra">
       <div>€4.76/kg</div>
     </div>
     */
    private Price secondPrice() {
        var pe = e.$("*[class*=extra]");
        if (!pe.exists() || pe.has(cssClass("hidden"))) return null;
        var text = pe.text();
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