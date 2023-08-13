package GroceryFamily.GroceryDad.scraper.page;

import GroceryFamily.GroceryDad.scraper.page.link.CategoryLink;
import GroceryFamily.GroceryDad.scraper.tree.CategoryLinkTree;
import GroceryFamily.GroceryDad.scraper.view.NewCategoryView;
import GroceryFamily.GroceryDad.scraper.view.Path;
import GroceryFamily.GroceryElders.domain.Category;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

@SuperBuilder
public class NewPage {
    public final Node node;
    public final Document document;
    public final Context context;

    public Category category() {
//        return Category.builder().code(code).name(name).build();
        return null;
    }

    public CategoryLink categoryLink() {
        return CategoryLink
                .builder()
                .codePath(codePath())
//                .source(this)
                .category(category())
//                .url(url)
                .build();
    }

    public final CategoryLinkTree childCategoryLinkTree() {
        var tree = new CategoryLinkTree();
//        var page = parent;
//        while (page != null) {
//            tree.add(CategoryLink.builder().category(page.category()).build());
//            page = page.parent;
//        }
//        childCategoryLinks().forEach(tree::add);
        return tree;
    }

    private List<NewCategoryView> childCategoryViews(Path<String> parentCodePath) {
        var views = new HashMap<Path<String>, NewCategoryView>();
        views.put(parentCodePath, NewCategoryView.root());
        childCategoryLinksSortedByCodePath().forEach(link -> {
            var view = NewCategoryView
                    .builder()
                    .codePath(link.codePath)
                    .category(link.category)
                    .url(link.url)
                    .build();
            var parent = views.get(link.codePath.parent());
            parent.addChild(view);
            views.put(link.codePath, view);
        });
        return views.get(parentCodePath).detachChildren();
    }

    private Stream<CategoryLink> childCategoryLinksSortedByCodePath() {
        return null;
//        var codePath = codePath();
//        return categoryLinks()
//                .filter(view -> view.codePath.contains(codePath))
//                .filter(view -> !view.codePath.equals(codePath))
//                .sorted(comparing(view -> view.codePath.size()));
    }
//
//    private Stream<CategoryLink> childCategoryLinks() {
//        var codePath = codePath();
//        return categoryLinks()
//                .filter(link -> link.codePath.contains(codePath))
//                .filter(link -> !link.codePath.equals(codePath));
//    }

//    protected abstract Stream<CategoryLink> categoryLinks();

    public final Path<String> codePath() {
//        return path(page -> page.code);
        return null;
    }

    public final Path<String> namePath() {
//        return path(page -> page.name);
        return null;
    }

    private <ELEMENT> Path<ELEMENT> path(Function<NewPage, ELEMENT> elementGetter) {
        var path = Path.<ELEMENT>empty();
//        var page = this;
//        while (page.parent != null) {
//            path = path.precededBy(elementGetter.apply(page));
//            page = page.parent;
//        }
        return path;
    }

}