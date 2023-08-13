package GroceryFamily.GroceryDad.scraper.page.link;

import GroceryFamily.GroceryDad.scraper.page.Context;
import GroceryFamily.GroceryDad.scraper.view.Path;
import GroceryFamily.GroceryElders.domain.Category;
import lombok.Builder;

@Builder
public class CategoryLink {
    public final Path<String> codePath;
    public final Category category;
    public final String url;
    public final Context context;

}