package GroceryFamily.GroceryDad.scraper.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AllowlistTest {
    @Test
    void testRegular() {
        var allowlist = new Allowlist();
        allowlist.put(Path.of("a", "b", "c"));
        assertThat(allowlist.allowed(Path.of())).isTrue();
        assertThat(allowlist.allowed(Path.of("a"))).isTrue();
        assertThat(allowlist.allowed(Path.of("a", "b"))).isTrue();
        assertThat(allowlist.allowed(Path.of("a", "b", "c"))).isTrue();
        assertThat(allowlist.allowed(Path.of("a", "b", "c", "whatever"))).isFalse();
        assertThat(allowlist.allowed(Path.of("whatever", "b", "c"))).isFalse();
        assertThat(allowlist.allowed(Path.of("a", "whatever", "c"))).isFalse();
        assertThat(allowlist.allowed(Path.of("a", "b", "whatever"))).isFalse();
    }

    @Test
    void testWildcard() {
        var allowlist = new Allowlist();
        assertThatThrownBy(() -> allowlist.put(Path.of("*", "b")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Wildcard (*) can only be used at the end");

        allowlist.put(Path.of("a", "*"));
        assertThat(allowlist.allowed(Path.of())).isTrue();
        assertThat(allowlist.allowed(Path.of("a"))).isTrue();
        assertThat(allowlist.allowed(Path.of("a", "whatever"))).isTrue();
        assertThat(allowlist.allowed(Path.of("whatever", "whatever"))).isFalse();
        assertThat(allowlist.allowed(Path.of("a", "whatever", "whatever"))).isTrue();
        assertThat(allowlist.allowed(Path.of("a", "whatever", "whatever", "whatever"))).isTrue();

        allowlist.put(Path.of("*"));
        assertThat(allowlist.allowed(Path.of())).isTrue();
        assertThat(allowlist.allowed(Path.of("whatever"))).isTrue();
        assertThat(allowlist.allowed(Path.of("whatever", "whatever"))).isTrue();
        assertThat(allowlist.allowed(Path.of("whatever", "whatever", "whatever"))).isTrue();
    }

    @Test
    void testEmpty() {
        var allowlist = new Allowlist();
        assertThatThrownBy(() -> allowlist.put(Path.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Path cannot be empty");
    }
}