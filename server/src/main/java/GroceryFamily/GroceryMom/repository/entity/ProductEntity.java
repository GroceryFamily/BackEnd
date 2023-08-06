package GroceryFamily.GroceryMom.repository.entity;

import GroceryFamily.GroceryElders.domain.Page;
import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.GroceryMom.model.PageToken;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private List<CategoryEntity> categories;

    public Product toDomainProduct() {
        return Product
                .builder()
                .namespace(getNamespace())
                .code(getCode())
                .name(getName())
                .prices(PriceEntity.toDomainPrices(getPrices()))
                .categories(CategoryEntity.toDomainCategories(getCategories()))
                .build();
    }

    public static Page<Product> toDomainProductPage(List<ProductEntity> entities, int pageSize) {
        if (entities.isEmpty()) return Page.empty();
        var nextPageToken = Optional
                .ofNullable(entities.size() > pageSize ? entities.get(entities.size() - 1) : null)
                .map(nextPageHead -> PageToken.builder().pageHeadId(nextPageHead.getId()).pageSize(pageSize).build())
                .map(PageToken::encode)
                .orElse(null);
        return Page
                .<Product>builder()
                .content(entities.stream().limit(pageSize).map(ProductEntity::toDomainProduct).toList())
                .nextPageToken(nextPageToken)
                .build();
    }

    public static ProductEntity fromDomainProduct(Product product, Instant ts) {
        var entity = new ProductEntity();
        return entity
                .setId(product.id())
                .setNamespace(product.namespace)
                .setCode(product.code)
                .setName(product.name)
                .setPrices(PriceEntity.fromDomainPrices(product.identifiablePrices(), ts, entity))
                .setCategories(CategoryEntity.fromDomainCategories(product.identifiableCategories(), ts, entity))
                .setTs(ts)
                .setVersion(0);
    }
}