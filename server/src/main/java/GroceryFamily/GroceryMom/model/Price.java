package GroceryFamily.GroceryMom.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
    private Instant ts;
    @Version
    private int version;
    @ManyToOne
    @EqualsAndHashCode.Exclude
    private Product product;

    @EqualsAndHashCode.Include
    private String productId() {
        return product != null ? product.getId() : null;
    }
}