package GroceryFamily.GrocerySis.txt.dataset;

import GroceryFamily.GrocerySis.txt.model.Code;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class OFFProduct {
    public final Code code;
    public final String name;
}