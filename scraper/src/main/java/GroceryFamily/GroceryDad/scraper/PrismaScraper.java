package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.tree.CategoryTree;
import GroceryFamily.GroceryDad.scraper.tree.CategoryTreePath;
import GroceryFamily.GroceryDad.scraper.tree.CategoryViewTree;
import GroceryFamily.GroceryDad.scraper.view.CategoryView;
import GroceryFamily.GroceryDad.scraper.view.NewCategoryView;
import GroceryFamily.GroceryElders.domain.Category;
import GroceryFamily.GroceryElders.domain.Product;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Consumer;

import static GroceryFamily.GroceryDad.scraper.PrismaPage.*;
import static GroceryFamily.GroceryDad.scraper.page.Page.scrollUp;
import static GroceryFamily.GroceryDad.scraper.page.Page.sleep;
import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

@Slf4j
@SuperBuilder
class PrismaScraper extends Scraper {
    @Override
    protected void acceptOrRejectCookies() {
        $("*[class*='js-cookie-notice'] *[class='close-icon']").shouldBe(visible).click();
        sleep();
    }

    @Override
    protected void switchToEnglish() {
        $("*[data-language='en']").shouldBe(visible).click();
        topCategoryElements().shouldHave(itemWithText("Groceries"));
        sleep();
    }

    @Override
    protected void scrap(Consumer<Product> handler) {
        /*
        var categories = new CategoryTree();
        topCategoryViews().forEach(view -> traverse(view, handler, categories));
        log.info("[PRISMA] Traversed categories: {}", categories);
         */

//        var seen = new CategoryViewTree();
//        NewPrismaPage
//                .runtime()
//                .categoryViewTree()
//                .leaves()
//                .forEach(node -> scrap(node.value, handler, seen));
//        log.info("[PRISMA] Traversed categories: {}", seen);

    }

//    private void scrap(NewCategoryView parent, Consumer<Product> handler, CategoryViewTree seen) {
//        if (seen.exists(parent)) return;
//        seen.add(parent);
//        open(parent.url);
//        waitUntilPageLoads();
//
//        var subcategories = NewPrismaPage
//                .runtime()
//                .subcategoryViewTree(parent.oldPath)
//                .leaves();
//        if (subcategories.isEmpty()) {
//            // todo: scrap products
//        } else {
//            subcategories.forEach(leaf -> scrap(leaf.value, handler, seen));
//        }
//    }

    private void traverse(CategoryView view, Consumer<Product> handler, CategoryTree categories) {
        if (categoryAllowed(view.path)) {
            view.select();
            if (view.isLeaf()) {
                products(view.path).forEach(handler);
                categories.add(view.path);
            } else {
                leftCategoryViews(view).forEach(child -> traverse(child, handler, categories));
            }
            view.deselect();
        }
    }

    private List<CategoryView> topCategoryViews() {
        return topCategoryElements()
                .shouldHave(sizeGreaterThan(0))
                .asFixedIterable()
                .stream()
                .map(e -> Category
                        .builder()
                        .code(substringAfterLast(substringBeforeLast(e.attr("href"), "/"), "/"))
                        .name(e.text())
                        .build())
                .map(category -> CategoryView
                        .builder()
                        .path(new CategoryTreePath(category))
                        .select(() -> {
                            topCategoryElement(category).shouldBe(visible).click();
                            breadcrumbElement(category).shouldBe(visible);
                            sleep();
                        })
                        .leaf(() -> false)
                        .deselect(() -> {})
                        .e(() -> topCategoryElement(category))
                        .build())
                .toList();
    }

    private List<CategoryView> leftCategoryViews(CategoryView parent) {
        return leftCategoryElements()
                .shouldHave(sizeGreaterThan(0))
                .asFixedIterable()
                .stream()
                .map(e -> Category
                        .builder()
                        .code(substringAfterLast(e.attr("href"), "/"))
                        .name(e.text())
                        .build())
                .map(category -> CategoryView
                        .builder()
                        .path(parent.path.add(category))
                        .select(() -> {
                            leftCategoryElement(category).shouldBe(visible).click();
                            breadcrumbElement(category).shouldBe(visible);
                            sleep();
                        })
                        .leaf(() -> leftCategoryElements().isEmpty())
                        .deselect(() -> {
                            scrollUp();
                            breadcrumbElement(parent.category()).shouldBe(visible).click();
                        })
                        .e(() -> leftCategoryElement(category))
                        .build())
                .toList();
    }

    private static List<Product> products(CategoryTreePath path) {
        return productElements()
                .asFixedIterable()
                .stream()
                .map(PrismaProductView::new)
                .map(view -> view.product(path))
                .toList();
    }
}