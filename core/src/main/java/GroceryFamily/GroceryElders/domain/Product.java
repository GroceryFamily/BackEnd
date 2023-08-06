package GroceryFamily.GroceryElders.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;
import java.util.Set;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Builder(toBuilder = true)
@ToString
@Jacksonized
@EqualsAndHashCode
public class Product {
    public final String namespace;
    public final String code;
    public final String name;
    @Builder.Default
    public final Set<Price> prices = Set.of();
    @Builder.Default
    public final Set<Category> categories = Set.of();

    public String id() {
        return Id.build(namespace, code);
    }

    public Map<String, Price> identifiablePrices() {
        return prices.stream().collect(toMap(price -> price.id(id()), identity()));
    }

    public Map<String, Category> identifiableCategories() {
        return categories.stream().collect(toMap(category -> category.id(id()), identity()));
    }
}