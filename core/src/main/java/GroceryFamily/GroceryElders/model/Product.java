package GroceryFamily.GroceryElders.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

@Data
@Entity
@ToString
public class Product {
    @Id
    private String id;
    private String namespace;
    private String code;
    private String name;
    @ToString.Exclude
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    List<Price> prices;
    private Instant ts;
    @Version
    private int version;
}