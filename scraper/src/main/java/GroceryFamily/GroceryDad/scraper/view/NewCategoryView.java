package GroceryFamily.GroceryDad.scraper.view;

import GroceryFamily.GroceryDad.scraper.tree.CategoryTreePath;
import GroceryFamily.GroceryElders.domain.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewCategoryView {
    @Deprecated
    public final CategoryTreePath oldPath;

    public final Path<String> codePath;
    public final Category category;
    public final String url;

    public String code() {
        return codePath.tail();
    }

    private final Map<String, NewCategoryView> children = new HashMap<>();
    private NewCategoryView parent;
    @Getter
    private boolean visited;

    @Builder
    private NewCategoryView(Path<String> codePath, Category category, String url, CategoryTreePath oldPath) {
        this.codePath = codePath;
        this.category = category;
        this.url = url;
        this.oldPath = oldPath;
    }

    public void addChild(NewCategoryView child) {
        children.put(child.code(), child);
        child.parent = this;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public void markVisited() {
        visited = true;
    }

    public Path<String> namePath() { // todo: reimplement
//        return oldPath.categories().stream().map(category -> category.name).toList();
        var path = Path.<String>empty();
        var view = this;
        while (!view.isRoot()) {
            path = path.precededBy(view.category.name);
            view = view.parent;
        }
        return path;
    }

    public List<NewCategoryView> leaves() {
        if (children.isEmpty()) return isRoot() ? List.of() : List.of(this);
        return children.values().stream().flatMap(child -> child.leaves().stream()).toList();
    }

    public static NewCategoryView root() {
        return NewCategoryView.builder().codePath(Path.empty()).build();
    }

    public List<NewCategoryView> detachChildren() {
        children.values().forEach(child -> child.parent = null);
        var detached = List.copyOf(children.values());
        children.clear();
        return detached;
    }
}