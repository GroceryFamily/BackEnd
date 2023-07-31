package GroceryFamily.GroceryElders.service.exception;

import static java.lang.String.format;

public class ProductNotFound extends RuntimeException {
    public ProductNotFound(String id) {
        super(format("Product '%s' not found", id));
    }
}