package GroceryFamily.GroceryElders.api.client;

import GroceryFamily.GroceryElders.domain.Page;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;

class PageIterator<DATA> implements Iterator<Page<DATA>> {
    private final Supplier<Page<DATA>> firstPage;
    private final Function<String, Page<DATA>> nextPage;
    private Page<DATA> page;

    PageIterator(Supplier<Page<DATA>> firstPage, Function<String, Page<DATA>> nextPage) {
        this.firstPage = firstPage;
        this.nextPage = nextPage;
    }

    @Override
    public boolean hasNext() {
        if (page == null) page = firstPage.get();
        return !page.content.isEmpty();
    }

    @Override
    public Page<DATA> next() {
        if (!hasNext()) throw new NoSuchElementException();
        var next = page;
        page = page.nextPageToken != null ? nextPage.apply(page.nextPageToken) : Page.empty();
        return next;
    }
}