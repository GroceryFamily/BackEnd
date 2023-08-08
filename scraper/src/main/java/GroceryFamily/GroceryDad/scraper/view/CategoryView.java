package GroceryFamily.GroceryDad.scraper.view;

import GroceryFamily.GroceryDad.scraper.tree.CategoryTreePath;
import GroceryFamily.GroceryElders.domain.Category;
import com.codeborne.selenide.SelenideElement;
import lombok.Builder;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Builder
public class CategoryView {
    public final CategoryTreePath path;
    private final Runnable select;
    private final Function<CategoryTreePath, List<CategoryView>> children;
    private final Supplier<Boolean> leaf;
    private final Runnable deselect;
    private final Supplier<SelenideElement> e;

    public Category category() {
        return path.last();
    }

    public void select() {
        select.run();
    }

    public List<CategoryView> children() {
        return children.apply(path);
    }

    public boolean isLeaf() {
        return leaf.get();
    }

    public void deselect() {
        deselect.run();
    }

    public SelenideElement e() {
        return e.get();
    }
}