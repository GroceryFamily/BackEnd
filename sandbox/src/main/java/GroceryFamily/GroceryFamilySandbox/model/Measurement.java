package GroceryFamily.GroceryFamilySandbox.model;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Measurement {
    private BigDecimal value;
    private String unit;


    public Measurement() {
    }

    public Measurement(String value, String unit) {
        this.value = new BigDecimal(value);
        this.unit = unit;
    }

    //Getters/Setters
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = new BigDecimal(value);
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public static Measurement setValueUnit(String product) {
        Measurement measurement = new Measurement();

        Pattern pattern = Pattern.compile("((?:\\d+x)?\\d+)(g|kg|L|ml|tk)(?:[,\\s]*(\\d+)k)?");
        Matcher matcher = pattern.matcher(product);

        if (matcher.find()) {
            String unit = matcher.group(2);
            String value = matcher.group(1).replaceAll(",", ".");

            measurement.setUnit(unit);
            measurement.setValue(value);
        } else {
            System.out.println("SetValueUnit");
        }

        return measurement;
    }


    @Override
    public String toString() {
        return "Measurement{" +
                "value='" + value + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }
}


