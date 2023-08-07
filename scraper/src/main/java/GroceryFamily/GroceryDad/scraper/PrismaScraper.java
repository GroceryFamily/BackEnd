package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.tree.CategoryTree;
import GroceryFamily.GroceryDad.scraper.tree.CategoryTreePath;
import GroceryFamily.GroceryDad.scraper.view.CategoryView;
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
        var categories = new CategoryTree();
        topCategoryViews().forEach(view -> traverse(view, handler, categories));
        log.info("[PRISMA] Traversed categories: {}", categories);
    }

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
                            breadcrumbElement(parent.path.last()).shouldBe(visible).click();
                        })
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