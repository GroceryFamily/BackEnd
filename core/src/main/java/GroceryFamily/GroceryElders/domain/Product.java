package GroceryFamily.GroceryElders.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Builder
@ToString
@EqualsAndHashCode
public class Product {
    public final String code;
    public final String name;
    public final Set<Price> prices;

    public Price price(PriceUnit unit) {
        if (unit == null) throw new IllegalArgumentException("Missing price unit");
        for (Price price : prices) {
            if (unit.equals(price.unit)) return price;
        }
        return null;
    }
}