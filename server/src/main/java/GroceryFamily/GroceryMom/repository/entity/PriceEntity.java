package GroceryFamily.GroceryMom.repository.entity;

import GroceryFamily.GroceryElders.domain.Price;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

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

    Price toDomainPrice() {
        return Price
                .builder()
                .unit(getUnit())
                .currency(getCurrency())
                .amount(getAmount())
                .build();
    }

    static Set<Price> toDomainPrices(List<PriceEntity> priceEntities) {
        return priceEntities.stream().map(PriceEntity::toDomainPrice).collect(toSet());
    }

    public static PriceEntity fromDomainPrice(String id, Price price, Instant ts, int version, ProductEntity productEntity) {
        return new PriceEntity()
                .setId(id)
                .setUnit(price.unit)
                .setCurrency(price.currency)
                .setAmount(price.amount)
                .setTs(ts)
                .setVersion(version)
                .setProduct(productEntity);
    }

    static List<PriceEntity> fromDomainPrices(Map<String, Price> prices, Instant ts, ProductEntity productEntity) {
        var entities = new ArrayList<PriceEntity>();
        prices.forEach((id, price) -> entities.add(fromDomainPrice(id, price, ts, 0, productEntity)));
        return entities;
    }
}