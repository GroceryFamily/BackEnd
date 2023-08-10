package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.tree.CategoryTreePath;
import GroceryFamily.GroceryElders.domain.*;
import com.codeborne.selenide.SelenideElement;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static GroceryFamily.GroceryDad.scraper.page.Page.decodeUrl;
import static com.codeborne.selenide.Condition.cssClass;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

record PrismaProductView(SelenideElement e) {
    Product product(CategoryTreePath path) {
        return Product
                .builder()
                .namespace(Namespace.PRISMA)
                .code(code())
                .name(name())
                .prices(prices())
//                .categories(path.categories())
                .build();
    }

    private String code() {
        return decodeUrl(substringAfterLast(url(), "/"));
    }

    private String url() {
        return e.$("a").attr("href");
    }

    private String name() {
        return e.$("*[class='name']").text();
    }

    private Set<Price> prices() {
        var prices = new HashSet<Price>();
        prices.add(firstPrice());
        Optional.ofNullable(secondPrice()).ifPresent(prices::add);
        return prices;
    }

    /*
     <div class="js-info-price">
       <span class="whole-number ">2</span>
       <span class="decimal">49</span>
       <span class="unit">/pcs</span>
     </div>
     */
    private Price firstPrice() {
        var pe = e.$("*[class*='js-info-price']");
        var unit = substringAfter(pe.$("*[class*=unit]").text(), "/");
        var amount = pe.$("*[class*=whole-number]").text() + '.' + pe.$("*[class*=decimal]").text();
        return Price
                .builder()
                .unit("pcs".equals(unit) ? PriceUnit.PC : unit)
                .currency(Currency.EUR)
                .amount(new BigDecimal(amount))
                .build();
    }

    /*
     <div class="unit-price clear js-comp-price ">2,49 €/kg</div>
     */
    private Price secondPrice() {
        var pe = e.$("*[class*=js-comp-price]");
        if (pe.has(cssClass("hidden"))) return null;
        var fragments = pe.text().split(" ");
        var amount = fragments[0].split(",");
        return Price
                .builder()
                .unit(substringAfter(fragments[1], "/"))
                .currency(Currency.EUR)
                .amount(new BigDecimal(amount[0] + '.' + amount[1]))
                .build();
    }
}