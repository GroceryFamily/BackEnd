package GroceryFamily.GroceryElders.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Currency {
    public static Currency EUR = new Currency("eur");

    public final String name;

    private Currency(String name) {
        this.name = name.toLowerCase();
    }

    public static Currency get(String name) {
        if (name == null) throw new NullPointerException("Missing currency name");
        return new Currency(name);
    }
}