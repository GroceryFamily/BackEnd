package GroceryFamily.GroceryElders.domain;

import static org.springframework.util.StringUtils.arrayToDelimitedString;

public class Id {
    private static final String DELIMITER = "::";

    public static String build(String... fragments) {
        return arrayToDelimitedString(fragments, DELIMITER);
    }
}