package GroceryFamily.GroceryElders.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Data
@Entity
public class Product {
    @Id
    private String id;
    private String namespace;
    private String code;
    private String name;
    private Instant ts;
    @OneToMany(mappedBy = "product")
    List<Price> prices;
    @Version
    private int version;

    public Product patch(GroceryFamily.GroceryElders.domain.Product product, Instant ts) {
        return setName(product.name).setTs(ts).setPrices(patchPrices(product.identifiablePrices(), ts));
    }

    private List<Price> patchPrices(Map<String, GroceryFamily.GroceryElders.domain.Price> patch, Instant ts) {
        var oldPrices = prices.stream().collect(toMap(Price::getId, identity()));
        List<Price> newPrices = new ArrayList<>();
        patch.forEach((id, price) -> {
            var oldPrice = oldPrices.get(id);
            if (oldPrice != null) newPrices.add(oldPrice.patch(price, ts));
            else prices.add(mapPrice(id, price, ts));
        });
        oldPrices.forEach((id, price) -> {
            if (!patch.containsKey(id)) newPrices.add(price);
        });
        return newPrices;
    }

    public static Product map(GroceryFamily.GroceryElders.domain.Product product, Instant ts) {
        return new Product()
                .setId(product.id())
                .setCode(product.code)
                .setName(product.name)
                .setTs(ts)
                .setPrices(mapPrices(product, ts));
    }

    private static List<Price> mapPrices(GroceryFamily.GroceryElders.domain.Product product, Instant ts) {
        return product.prices
                .stream()
                .map(price -> mapPrice(price.id(product.id()), price, ts))
                .toList();
    }

    private static Price mapPrice(String id, GroceryFamily.GroceryElders.domain.Price price, Instant ts) {
        return new Price()
                .setId(id)
                .setUnit(price.unit.name)
                .setAmount(price.amount)
                .setCurrency(price.currency.name)
                .setTs(ts);
    }
}