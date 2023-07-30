package GroceryFamily.GroceryElders.model;

import GroceryFamily.GroceryElders.service.mapper.ModelMapper;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

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

    public Product patch(GroceryFamily.GroceryElders.domain.Product patch, Instant ts) {
        setName(patch.name);
        setTs(ts);
        setPrices(patchPrices(patch.identifiablePrices(), ts));
        return this;
    }

    private List<Price> patchPrices(Map<String, GroceryFamily.GroceryElders.domain.Price> patch, Instant ts) {
        var oldPrices = prices.stream().collect(toMap(Price::getId, identity()));
        List<Price> newPrices = new ArrayList<>();
        patch.forEach((id, price) -> {
            var oldPrice = oldPrices.get(id);
            if (oldPrice != null) newPrices.add(oldPrice.patch(price, ts));
            else prices.add(ModelMapper.price(id, price, ts));
        });
        oldPrices.forEach((id, price) -> {
            if (!patch.containsKey(id)) newPrices.add(price);
        });
        return newPrices;
    }
}