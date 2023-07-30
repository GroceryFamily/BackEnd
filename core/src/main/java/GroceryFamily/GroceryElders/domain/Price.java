package GroceryFamily.GroceryElders.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@Builder
@ToString
@EqualsAndHashCode
public class Price {
    public final PriceUnit unit;
    public final Currency currency;
    public final BigDecimal amount;

    public String id(String productId) {
        return productId + "::" + unit.name + "::" + currency.name;
    }
}