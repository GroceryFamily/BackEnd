package GroceryFamily.GroceryMom.model;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class PageToken<KEYS> { // todo: sort order?
    public final KEYS pageHead;
    public final int pageSize;
}