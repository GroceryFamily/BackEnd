package GroceryFamily.GroceryDad.scraper.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PermissionTreeTest {
    @Test
    void testRegular() {
        var tree = new PermissionTree();
        tree.add(List.of("a", "b", "c"));
        assertThat(tree.allowed(List.of())).isTrue();
        assertThat(tree.allowed(List.of("a"))).isTrue();
        assertThat(tree.allowed(List.of("a", "b"))).isTrue();
        assertThat(tree.allowed(List.of("a", "b", "c"))).isTrue();
        assertThat(tree.allowed(List.of("a", "b", "c", "whatever"))).isFalse();
        assertThat(tree.allowed(List.of("whatever", "b", "c"))).isFalse();
        assertThat(tree.allowed(List.of("a", "whatever", "c"))).isFalse();
        assertThat(tree.allowed(List.of("a", "b", "whatever"))).isFalse();
    }

    @Test
    void testWildcard() {
        {
            var tree = new PermissionTree();
            tree.add(List.of("*", "b"));
            assertThat(tree.allowed(List.of("a"))).isTrue();
            assertThat(tree.allowed(List.of("whatever"))).isTrue();
            assertThat(tree.allowed(List.of("a", "b"))).isTrue();
            assertThat(tree.allowed(List.of("whatever", "b"))).isTrue();
            assertThat(tree.allowed(List.of("whatever", "whatever"))).isFalse();
            assertThat(tree.allowed(List.of("a", "b", "whatever"))).isFalse();
            assertThat(tree.allowed(List.of("whatever", "b", "whatever"))).isFalse();
        }
        {
            var tree = new PermissionTree();
            tree.add(List.of("a", "*"));
            assertThat(tree.allowed(List.of("a"))).isTrue();
            assertThat(tree.allowed(List.of("a", "b"))).isTrue();
            assertThat(tree.allowed(List.of("a", "whatever"))).isTrue();
            assertThat(tree.allowed(List.of("whatever", "whatever"))).isFalse();
            assertThat(tree.allowed(List.of("a", "b", "whatever"))).isFalse();
            assertThat(tree.allowed(List.of("a", "whatever", "whatever"))).isFalse();
        }
    }

    @Test
    void testEmpty() {
        var tree = new PermissionTree();
        assertThat(tree.allowed(List.of())).isTrue();
        assertThat(tree.allowed(List.of("whatever"))).isFalse();
    }
}