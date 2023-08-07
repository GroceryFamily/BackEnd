package GroceryFamily.GroceryDad.scraper.tree;

import java.util.Arrays;

public class CategoryPermissionTree extends PermissionTree {
    public boolean allowed(CategoryTreePath path) {
        return allowed(Arrays.stream(path.categories).map(category -> category.name).toList());
    }
}