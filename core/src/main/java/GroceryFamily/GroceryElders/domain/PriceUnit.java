package GroceryFamily.GroceryElders.domain;

public interface PriceUnit {
    String PC = "pc";

    static String normalize(String unit) {
        if (unit == null) throw new NullPointerException("Price unit is missing");
        return unit.toLowerCase();
    }
}