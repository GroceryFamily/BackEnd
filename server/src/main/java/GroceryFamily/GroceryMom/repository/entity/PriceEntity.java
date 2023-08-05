package GroceryFamily.GroceryMom.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
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
    private ProductEntity product;
}