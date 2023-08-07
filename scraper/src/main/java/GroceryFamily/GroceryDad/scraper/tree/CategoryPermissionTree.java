package GroceryFamily.GroceryDad.scraper.tree;

import GroceryFamily.GroceryElders.domain.Category;

import java.util.Stack;

public class CategoryPermissionTree extends PermissionTree {
    public boolean allowed(Stack<Category> path) {
        return allowed(path.stream().map(category -> category.name).toList());
    }
}