package GroceryFamily.GroceryDad.scraper.view;

import GroceryFamily.GroceryDad.scraper.tree.CategoryTreePath;
import lombok.Builder;

import java.util.function.Supplier;

@Builder
public class CategoryView {
    public final CategoryTreePath path;
    private final Runnable select;
    private final Supplier<Boolean> leaf;
    private final Runnable deselect;

    public void select() {
        select.run();
    }

    public boolean isLeaf() {
        return leaf.get();
    }

    public void deselect() {
        deselect.run();
    }
}