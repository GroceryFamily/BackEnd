package GroceryFamily.GroceryElders.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import static org.springframework.util.StringUtils.arrayToDelimitedString;

@ToString
@EqualsAndHashCode
public class Identifiable<DATA> {
    public final String id;
    public final DATA data;

    private Identifiable(DATA data, String... idFragments) {
        this.id = id(idFragments);
        this.data = data;
    }

    static String id(String... fragments) {
        return arrayToDelimitedString(fragments, "::");
    }

    public static <DATA> Identifiable<DATA> identify(DATA data, String... idFragments) {
        return new Identifiable<>(data, idFragments);
    }
}