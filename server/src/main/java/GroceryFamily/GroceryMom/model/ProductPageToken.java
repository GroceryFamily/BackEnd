package GroceryFamily.GroceryMom.model;

public interface ProductPageToken {
    record OrderedById(String id, int pageSize) {}
}