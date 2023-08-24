package GroceryFamily.GroceryMom.repository.entity;

import GroceryFamily.GroceryElders.domain.Page;
import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.GroceryMom.model.PageToken;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@EqualsAndHashCode
@Entity(name = "product")
public class ProductEntity {
    private static ObjectMapper MAPPER = new ObjectMapper();
    private static final JavaType CATEGORIES = MAPPER.getTypeFactory().constructMapLikeType(Map.class, String.class, String.class);
    private static final JavaType DETAILS = MAPPER.getTypeFactory().constructMapLikeType(Map.class, String.class, String.class);

    @Id
    private String id;
    private String namespace;
    private String code;
    private String name;
    private String url;
    private Instant ts;
    @Version
    private int version;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private List<PriceEntity> prices;
    @EqualsAndHashCode.Exclude
    private String categories;
    @EqualsAndHashCode.Exclude
    @Column(columnDefinition = "text")
    private String details;

    @SneakyThrows
    public Map<String, String> getCategories() {
        if (categories == null) return Map.of();
        return MAPPER.readValue(categories, CATEGORIES);
    }

    @SneakyThrows
    public ProductEntity setCategories(Map<String, String> categories) {
        this.categories = MAPPER.writeValueAsString(categories);
        return this;
    }

    @SneakyThrows
    public Map<String, String> getDetails() {
        if (details == null) return Map.of();
        return MAPPER.readValue(details, DETAILS);
    }

    @SneakyThrows
    public ProductEntity setDetails(Map<String, String> details) {
        this.details = MAPPER.writeValueAsString(details);
        return this;
    }

    public Product toDomainProduct() {
        return Product
                .builder()
                .namespace(getNamespace())
                .code(getCode())
                .name(getName())
                .url(getUrl())
                .prices(PriceEntity.toDomainPrices(getPrices()))
                .categories(getCategories())
                .details(getDetails())
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
                .setUrl(product.url)
                .setTs(ts)
                .setVersion(0)
                .setPrices(PriceEntity.fromDomainPrices(product.identifiablePrices(), ts, entity))
                .setCategories(product.categories)
                .setDetails(product.details);
    }
}