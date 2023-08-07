package GroceryFamily.GroceryDad.scraper.tree;

import GroceryFamily.GroceryElders.domain.Category;

public class CategoryTree extends Tree<String, Category> {
    public CategoryTree() {
        super(category -> category.name);
    }
}