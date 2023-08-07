package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryElders.domain.Category;
import lombok.Builder;
import lombok.ToString;

import java.util.function.Supplier;

@Builder
@ToString
class CategoryView {
    public final Category category;
    @ToString.Exclude
    private final Supplier<Boolean> selected;
    @ToString.Exclude
    private final Runnable select;
    @ToString.Exclude
    private final Runnable deselect;

    boolean isSelected() {
        return selected.get();
    }

    void select() {
        select.run();
    }

    void deselect() {
        deselect.run();
    }
}