package GroceryFamily.GroceryDad.scraper.tree;

import GroceryFamily.GroceryDad.scraper.view.NewCategoryView;

public class CategoryViewTree extends Tree<String, NewCategoryView> {
    public void add(NewCategoryView view) {
        add(view.namePath(), view);
    }

    public boolean exists(NewCategoryView view) {
        return exists(view.namePath());
    }
}