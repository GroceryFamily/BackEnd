package GroceryFamily.GroceryDad.scraper.model;

import GroceryFamily.GroceryElders.domain.Product;

public interface Listener {
    void product(String platform, Product product, Source source);
}