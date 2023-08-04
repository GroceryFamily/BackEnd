package GroceryFamily.GroceryElders.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@SuperBuilder(toBuilder = true)
@ToString
@Jacksonized
@EqualsAndHashCode
public class Page<DATA> {
    public final List<DATA> content;
    public final String nextPageToken;

    public static <DATA> Page<DATA> empty() {
        return Page.<DATA>builder().content(List.of()).build();
    }
}