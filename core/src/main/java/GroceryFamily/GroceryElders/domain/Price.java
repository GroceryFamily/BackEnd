package GroceryFamily.GroceryElders.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Builder(toBuilder = true)
@ToString
@Jacksonized
@EqualsAndHashCode
public class Price {
    public final String unit;
    public final String currency;
    @EqualsAndHashCode.Exclude
    public final BigDecimal amount;

    public String id(String productId) {
        return productId + "::" + unit + "::" + currency;
    }

    @EqualsAndHashCode.Include
    private BigDecimal amount() {
        return amount != null ? amount.stripTrailingZeros() : null;
    }
}