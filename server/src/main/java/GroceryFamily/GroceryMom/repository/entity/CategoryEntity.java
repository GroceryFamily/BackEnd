package GroceryFamily.GroceryMom.repository.entity;

import GroceryFamily.GroceryElders.domain.Category;
import GroceryFamily.GroceryElders.domain.Identifiable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.List;
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
    private String url;
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
                .url(getUrl())
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
                .setUrl(category.url)
                .setTs(ts)
                .setVersion(version)
                .setProduct(productEntity);
    }

    static List<CategoryEntity> fromDomainCategories(Set<Identifiable<Category>> categories, Instant ts, ProductEntity productEntity) {
        return categories.stream().map(category -> fromDomainCategory(category.id, category.data, ts, 0, productEntity)).toList();
    }
}