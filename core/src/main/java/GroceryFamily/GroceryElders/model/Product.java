package GroceryFamily.GroceryElders.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Product {
    @Id
    private String id;
    private String namespace;
    private String code;
    private String name;
    @OneToMany(mappedBy = "product")
    List<Price> prices;
    @Version
    private int version;
}