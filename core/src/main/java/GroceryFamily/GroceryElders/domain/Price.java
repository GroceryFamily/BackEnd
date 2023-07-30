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
    public final BigDecimal amount;
    public final Currency currency;
}