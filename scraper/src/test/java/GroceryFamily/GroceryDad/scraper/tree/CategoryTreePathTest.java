package GroceryFamily.GroceryDad.scraper.tree;

import GroceryFamily.GroceryElders.domain.Category;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTreePathTest {
    @Test
    void test() {
        var path = new CategoryTreePath(category("a"));
        assertThat(path.categories).containsExactlyElementsOf(categories("a"));
        path = path.add(category("b"));
        assertThat(path.categories).containsExactlyElementsOf(categories("a", "b"));
        path = path.add(category("c"));
        assertThat(path.categories).containsExactlyElementsOf(categories("a", "b", "c"));
    }

    private static List<Category> categories(String... names) {
        return Arrays.stream(names).map(CategoryTreePathTest::category).toList();
    }

    private static Category category(String name) {
        return Category.builder().code(name).name(name).build();
    }
}