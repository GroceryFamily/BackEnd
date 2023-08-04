package GroceryFamily.GroceryElders.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder(toBuilder = true)
@ToString
@Jacksonized
@EqualsAndHashCode
public class Page<DATA> {
    public final List<DATA> content;
    public final String nextPageToken;
}