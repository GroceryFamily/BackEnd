package GroceryFamily.GroceryMom.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.List;

@Data
@EqualsAndHashCode
@Entity(name = "product")
public class ProductEntity {
    @Id
    private String id;
    private String namespace;
    private String code;
    private String name;
    private Instant ts;
    @Version
    private int version;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private List<PriceEntity> prices;
}