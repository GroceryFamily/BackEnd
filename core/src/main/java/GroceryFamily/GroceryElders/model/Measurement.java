package GroceryFamily.GroceryElders.model;

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

    public void setValue(String value) {
        this.value = new BigDecimal(value);
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

//    public static Measurement setValueUnit(String product) {
//        Measurement measurement = new Measurement();
//
//        Pattern pattern = Pattern.compile("((?:\\d+x)?\\d+)(g|kg|L|ml|tk)(?:[,\\s]*(\\d+)k)?");
//        Matcher matcher = pattern.matcher(product);
//
//        if (matcher.find()) {
//            String unit = matcher.group(2);
//            String value = matcher.group(1).replaceAll(",", ".");
//            if (matcher.group(3) != null) {
//                value = value + "x" + matcher.group(3);
//            }
//            measurement.setUnit(unit);
//            measurement.setValue(value);
//        }
//
//        return measurement;
//    }
public static Measurement setValueUnit(String product) {
    Measurement measurement = new Measurement();

    Pattern pattern = Pattern.compile("((?:\\d+x)?\\d+)(g|kg|L|ml|tk)(?:[,\\s]*(\\d+)k)?");
    Matcher matcher = pattern.matcher(product);

    if (matcher.find()) {
        String unit = matcher.group(2);
        String value = matcher.group(1).replaceAll(",", ".");
        if (matcher.group(3) != null) {
            value = value + "x" + matcher.group(3);
        }
        measurement.setUnit(unit);
        measurement.setValue(value);
    } else {
        System.out.println("SetValueUnit");  // or throw an exception, or return a default value
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


