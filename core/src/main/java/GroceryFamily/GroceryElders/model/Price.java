package GroceryFamily.GroceryElders.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@ToString
public class Price {
    @Id
    private String id;
    private String unit;
    private String currency;
    private BigDecimal amount;
    @ManyToOne
    private Product product;
    private Instant ts;
    @Version
    private int version;
}