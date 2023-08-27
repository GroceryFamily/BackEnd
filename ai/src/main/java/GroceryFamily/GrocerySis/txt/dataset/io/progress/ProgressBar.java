package GroceryFamily.GrocerySis.txt.dataset.io.progress;

public interface ProgressBar extends AutoCloseable {
    void stepBy(long delta);
}