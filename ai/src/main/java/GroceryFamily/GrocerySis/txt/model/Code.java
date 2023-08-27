package GroceryFamily.GrocerySis.txt.model;

import lombok.EqualsAndHashCode;

import static java.lang.String.format;

@EqualsAndHashCode
public class Code {
    public enum Type {EAN13, EAN8, UNKNOWN}

    public final Type type;
    public final String value;

    private Code(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return format("%s(%s)", type, value);
    }

    public static Code parse(String value) {
        if (looksLikeEAN13(value)) return new Code(Type.EAN13, value);
        if (looksLikeEAN8(value)) return new Code(Type.EAN8, value);
        return new Code(Type.UNKNOWN, value);
    }

    private static boolean looksLikeEAN13(String value) {
        return value != null && value.matches("[0-9]{13}");
    }

    private static boolean looksLikeEAN8(String value) {
        return value != null && value.matches("[0-9]{8}");
    }
}