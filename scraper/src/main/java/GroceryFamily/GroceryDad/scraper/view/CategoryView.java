package GroceryFamily.GroceryDad.scraper.view;

import GroceryFamily.GroceryDad.scraper.tree.CategoryTreePath;
import GroceryFamily.GroceryElders.domain.Category;
import com.codeborne.selenide.SelenideElement;
import lombok.Builder;

import java.util.function.Supplier;

@Builder
public class CategoryView {
    public final CategoryTreePath path;
    private final Runnable select;
    private final Supplier<Boolean> leaf;
    private final Runnable deselect;
    private final Supplier<SelenideElement> e;

    public Category category() {
        return path.last();
    }

    public void select() {
        select.run();
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