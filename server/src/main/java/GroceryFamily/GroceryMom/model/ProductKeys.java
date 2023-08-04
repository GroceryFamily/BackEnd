package GroceryFamily.GroceryMom.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

public interface ProductKeys {
    record Id(String id) {
        public static PageTokenIO<Id> IO = new PageTokenIO<>(new TypeReference<>() {});
        public static PageTokenKeysExtractor<Product, Id> EXTRACTOR = product -> new Id(product.getId());
    }
}