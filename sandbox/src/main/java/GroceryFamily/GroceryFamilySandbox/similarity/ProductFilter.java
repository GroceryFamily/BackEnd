package GroceryFamily.GroceryFamilySandbox.similarity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ProductFilter {
    /* todo: fix
    public static List<Product> containsAllWords(List<Product> products, String substring) {

        String[] subWords = substring.toLowerCase().split("\\s+");
        Arrays.sort(subWords);

        List<Product> filteredProducts = new ArrayList<>();

        for (Product product : products) {
            String[] strWords = product.getName().split("\\s+");
            Arrays.sort(strWords);

            if (Arrays.asList(strWords).containsAll(Arrays.asList(subWords))) {
                filteredProducts.add(product);

            }
        }

        return filteredProducts;
    }

    //gives the cheapest product. Use price per unit
    public static Product cheaperPrice(List<Product> products) {
        return products.stream()
                .min(Comparator.comparing(product -> {
                    BigDecimal price = product.getPricePerUnit().getValue();
                    return price != null ? price : BigDecimal.ZERO;
                }))
                .orElse(null);
    }
    */
}
