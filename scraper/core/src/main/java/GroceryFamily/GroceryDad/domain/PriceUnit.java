package GroceryFamily.GroceryDad.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.openqa.selenium.InvalidArgumentException;

@ToString
@EqualsAndHashCode
public class PriceUnit {
    public static PriceUnit PC = new PriceUnit("pc");

    public final String name;

    private PriceUnit(String name) {
        this.name = name.toLowerCase();
    }

    public static PriceUnit get(String name) {
        if (name == null) throw new InvalidArgumentException("Price unit name is missing");
        return new PriceUnit(name);
    }
}