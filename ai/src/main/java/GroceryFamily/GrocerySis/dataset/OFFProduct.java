package GroceryFamily.GrocerySis.dataset;

import GroceryFamily.GrocerySis.model.Code;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class OFFProduct {
    public final Code code;
    public final String name;
}