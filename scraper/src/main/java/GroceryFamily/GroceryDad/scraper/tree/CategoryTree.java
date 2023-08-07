package GroceryFamily.GroceryDad.scraper.tree;

import GroceryFamily.GroceryElders.domain.Category;

import java.util.List;

public class CategoryTree extends Tree<String, Category> {
    public CategoryTree() {
        super(category -> category.name);
    }

    public void add(CategoryTreePath path) {
        add(List.of(path.categories));
    }
}