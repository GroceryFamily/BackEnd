package GroceryFamily.GroceryDad.scraper.view;

import GroceryFamily.GroceryDad.scraper.tree.CategoryTreePath;
import GroceryFamily.GroceryElders.domain.Category;
import lombok.Builder;
import lombok.ToString;

import java.util.List;

@Builder
@ToString
public class NewCategoryView {
    public final CategoryTreePath path;
    public final Category category;
    public final String url;

    public List<String> namePath() {
        return path.categories().stream().map(category -> category.name).toList();
    }
}