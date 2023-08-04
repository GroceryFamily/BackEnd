package GroceryFamily.GroceryMom.model;

public interface PageTokenKeysExtractor<DATA, KEYS> {
    KEYS extract(DATA data);
}