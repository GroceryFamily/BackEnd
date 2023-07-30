package GroceryFamily.GroceryElders.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class PriceUnit {
    public static PriceUnit PC = new PriceUnit("pc");

    public final String name;

    private PriceUnit(String name) {
        this.name = name.toLowerCase();
    }

    public static PriceUnit get(String name) {
        if (name == null) throw new NullPointerException("Price unit name is missing");
        return new PriceUnit(name);
    }
}