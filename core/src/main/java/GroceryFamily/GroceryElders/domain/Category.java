package GroceryFamily.GroceryElders.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Builder(toBuilder = true)
@ToString
@Jacksonized
@EqualsAndHashCode
public class Category {
    public final String code;
    public final String name;

    public String id(String productId) {
        return Id.build(productId, code);
    }
}