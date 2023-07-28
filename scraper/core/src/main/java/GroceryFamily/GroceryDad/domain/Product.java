package GroceryFamily.GroceryDad.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Builder
@ToString
@EqualsAndHashCode
public class Product {
    public final String code;
    public final String name;
    public final Set<Price> prices;
}