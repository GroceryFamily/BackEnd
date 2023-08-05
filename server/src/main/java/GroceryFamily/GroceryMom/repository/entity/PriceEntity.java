package GroceryFamily.GroceryMom.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@EqualsAndHashCode
@Entity(name = "price")
public class PriceEntity {
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
    private ProductEntity product;

    @EqualsAndHashCode.Include
    private String productId() {
        return product != null ? product.getId() : null;
    }
}