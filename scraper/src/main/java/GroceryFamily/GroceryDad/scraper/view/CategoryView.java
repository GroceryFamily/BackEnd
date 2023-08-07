package GroceryFamily.GroceryDad.scraper.view;

import GroceryFamily.GroceryElders.domain.Category;
import lombok.Builder;
import lombok.ToString;

import java.util.function.Supplier;

@Builder
@ToString
public class CategoryView {
    public final Category category;
    @ToString.Exclude
    private final Supplier<Boolean> selected;
    @ToString.Exclude
    private final Runnable select;
    @ToString.Exclude
    private final Runnable deselect;

    // todo: remove
    @Deprecated
    public boolean isSelected() {
        return selected.get();
    }

    public void select() {
        select.run();
    }

    public void deselect() {
        deselect.run();
    }
}