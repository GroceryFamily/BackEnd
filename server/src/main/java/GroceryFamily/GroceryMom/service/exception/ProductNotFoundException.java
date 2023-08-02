package GroceryFamily.GroceryMom.service.exception;

import static java.lang.String.format;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String id) {
        super(format("Product '%s' not found", id));
    }
}