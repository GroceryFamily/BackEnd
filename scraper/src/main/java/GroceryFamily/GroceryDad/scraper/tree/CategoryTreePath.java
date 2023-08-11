package GroceryFamily.GroceryDad.scraper.tree;

import GroceryFamily.GroceryElders.domain.Category;

import java.util.List;

public class CategoryTreePath {
    final Category[] categories; // todo: use linked list instead

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

    public List<Category> categories() {
        return List.of(categories);
    }

    public int size() {
        return categories.length;
    }

    public static CategoryTreePath get(List<Category> categories) {
        var path = new CategoryTreePath(categories.get(0));
        for (int i = 1; i < categories.size(); ++i) {
            path = path.add(categories.get(i));
        }
        return path;
    }
}