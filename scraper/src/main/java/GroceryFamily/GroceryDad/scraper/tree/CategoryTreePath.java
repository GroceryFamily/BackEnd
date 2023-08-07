package GroceryFamily.GroceryDad.scraper.tree;

import GroceryFamily.GroceryElders.domain.Category;

public class CategoryTreePath {
    final Category[] categories;

    public CategoryTreePath(Category first) {
        this(new Category[]{first});
    }

    private CategoryTreePath(Category[] categories) {
        this.categories = categories;
    }

    public Category last() {
        return categories[categories.length - 1];
    }

    public CategoryTreePath add(Category category) {
        Category[] categories = new Category[this.categories.length + 1];
        System.arraycopy(this.categories, 0, categories, 0, this.categories.length);
        categories[this.categories.length] = category;
        return new CategoryTreePath(categories);
    }
}