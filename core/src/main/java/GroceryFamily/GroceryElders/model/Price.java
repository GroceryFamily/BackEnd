package GroceryFamily.GroceryElders.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
public class Price {
    @Id
    private String id;
    private String unit;
    private String currency;
    private BigDecimal amount;
    private Instant ts;
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;
    @Version
    private int version;

    Price patch(GroceryFamily.GroceryElders.domain.Price price, Instant ts) {
        return setAmount(price.amount).setTs(ts);
    }
}