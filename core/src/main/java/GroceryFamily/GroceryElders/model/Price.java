package GroceryFamily.GroceryElders.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
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
    @ManyToOne
    private Product product;
    @Version
    private int version;

    Price patch(GroceryFamily.GroceryElders.domain.Price price, Instant ts) {
        return this.setAmount(price.amount).setTs(ts);
    }
}