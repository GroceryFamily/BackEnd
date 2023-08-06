package GroceryFamily.GroceryMom.repository.entity;

import GroceryFamily.GroceryElders.domain.Category;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Data
@EqualsAndHashCode
@Entity(name = "category")
public class CategoryEntity {
    @Id
    private String id;
    private String code;
    private String name;
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

    Category toDomainCategory() {
        return Category
                .builder()
                .code(getCode())
                .name(getName())
                .build();
    }

    static Set<Category> toDomainCategories(List<CategoryEntity> categoryEntities) {
        return categoryEntities.stream().map(CategoryEntity::toDomainCategory).collect(toSet());
    }

    public static CategoryEntity fromDomainCategory(String id, Category category, Instant ts, int version, ProductEntity productEntity) {
        return new CategoryEntity()
                .setId(id)
                .setCode(category.code)
                .setName(category.name)
                .setTs(ts)
                .setVersion(version)
                .setProduct(productEntity);
    }

    static List<CategoryEntity> fromDomainCategories(Map<String, Category> categories, Instant ts, ProductEntity productEntity) {
        var entities = new ArrayList<CategoryEntity>();
        categories.forEach((id, category) -> entities.add(fromDomainCategory(id, category, ts, 0, productEntity)));
        return entities;
    }
}