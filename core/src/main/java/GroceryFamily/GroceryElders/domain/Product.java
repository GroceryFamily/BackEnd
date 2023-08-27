package GroceryFamily.GroceryElders.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Builder(toBuilder = true)
@ToString
@Jacksonized
@EqualsAndHashCode
public class Product {
    public final String namespace;
    public final String code;
    public final String name;
    public final String url;
    @Builder.Default
    public final Set<Price> prices = Set.of();
    @Builder.Default
    public final Map<String, String> categories = Map.of();
    @Builder.Default
    public final Map<String, String> details = Map.of();

    public String id() {
        return Identifiable.id(namespace, code);
    }

    public Set<Identifiable<Price>> identifiablePrices() {
        return prices
                .stream()
                .map(price -> Identifiable.identify(price, id(), price.unit, price.currency))
                .collect(toSet());
    }

    public Optional<String> brand() {
        return Optional.ofNullable(details.get(Detail.BRAND));
    }
}