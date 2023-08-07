package GroceryFamily.GroceryDad.scraper.view;

import GroceryFamily.GroceryElders.domain.Category;
import GroceryFamily.GroceryElders.domain.Price;

import java.util.Set;

public interface ProductView {
    String namespace();

    String code();

    String name();

    Set<Price> prices();

    Set<Category> categories();
}