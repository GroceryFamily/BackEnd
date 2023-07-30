package GroceryFamily.GroceryElders.domain;

import lombok.Builder;
import lombok.ToString;

import java.math.BigDecimal;

@Builder
@ToString
public class Price {
    public final String unit;
    public final String currency;
    public final BigDecimal amount;

    public String id(String productId) {
        return productId + "::" + unit + "::" + currency;
    }
}