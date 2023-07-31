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
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;
    private Instant ts;
    @Version
    private int version;
}