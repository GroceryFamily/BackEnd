package GroceryFamily.GroceryDad.scraper.worker;

import GroceryFamily.GroceryDad.scraper.model.Link;
import lombok.Builder;

@Builder
public class WorkerEvent<PAYLOAD> {
    public final String platform;
    public final Link link;
    public final PAYLOAD payload;
}