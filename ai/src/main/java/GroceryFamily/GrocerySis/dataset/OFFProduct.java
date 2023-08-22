package GroceryFamily.GrocerySis.dataset;

import GroceryFamily.GrocerySis.model.Code;
import lombok.Builder;

@Builder
public class OFFProduct {
    public final Code code;
    public final String name;
}