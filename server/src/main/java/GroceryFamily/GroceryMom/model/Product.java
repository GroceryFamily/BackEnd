package GroceryFamily.GroceryMom.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Data
@Entity
@ToString
public class Product {
    @Id
    private String id;
    private String namespace;
    private String code;
    private String name;
    private Instant ts;
    @Version
    private int version;
    @ToString.Exclude
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Price> prices;

    public static Pageable sortById(int pageSize) {
        return PageRequest.of(0, pageSize, Sort.by(ASC, "id"));
    }
}