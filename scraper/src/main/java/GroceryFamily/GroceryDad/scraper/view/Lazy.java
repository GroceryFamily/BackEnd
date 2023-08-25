package GroceryFamily.GroceryDad.scraper.view;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Lazy<E> {
    private final Supplier<E> getter;
    private boolean initialized;
    private E e;

    public Lazy(Supplier<E> getter) {
        this.getter = getter;
    }

    public E get() {
        if (!initialized) {
            e = getter.get();
            initialized = true;
        }
        return e;
    }

    public <MAPPED> Lazy<MAPPED> map(Function<E, MAPPED> mapper) {
        return new Lazy<>(() -> Optional.ofNullable(get()).map(mapper).orElse(null));
    }

    public Lazy<E> filter(Predicate<E> predicate) {
        return new Lazy<>(() -> Optional.ofNullable(get()).filter(predicate).orElse(null));
    }

    public void ifPresent(Consumer<E> action) {
        Optional.ofNullable(get()).ifPresent(action);
    }
}