package GroceryFamily.GroceryElders.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;
import java.util.Set;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Builder
@ToString
@EqualsAndHashCode
public class Product {
    public final String namespace;
    public final String code;
    public final String name;
    public final Set<Price> prices;

    public String id() {
        return namespace + "::" + code;
    }

    public Map<String, Price> identifiablePrices() {
        return prices.stream().collect(toMap(price -> price.id(id()), identity()));
    }
}