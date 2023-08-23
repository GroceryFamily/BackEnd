package GroceryFamily.GrocerySis.dataset;

import GroceryFamily.GrocerySis.GrocerySisConfig;
import GroceryFamily.GrocerySis.dataset.io.CSVDatasetIO;
import GroceryFamily.GrocerySis.dataset.io.progress.ProgressBarFactory;
import GroceryFamily.GrocerySis.model.Code;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.function.Consumer;

@Slf4j
@Component
public class OFFProductDataset {
    private static final int CODE = 0;
    private static final int NAME = 8;
    private static final int DESCRIPTION = 10;
    private static final int QUANTITY = 11;
    private static final int BRANDS = 16;
    private static final int BRAND_TAGS = 17;
    private static final int CATEGORIES = 18;
    private static final int CATEGORY_TAGS = 18;

    private final Path dataset;

    OFFProductDataset(GrocerySisConfig config) {
        dataset = config.openFoodFacts.dataset;
    }

    public void read(Consumer<OFFProduct> handler, boolean interactive) {
        log.info("Reading {}...", dataset);
        var io = CSVDatasetIO
                .builder()
                .separator("\t")
                .progressBarFactory(interactive ? ProgressBarFactory.CONSOLE : ProgressBarFactory.DUMMY)
                //.skip(1)
                .build();
        io.read(dataset, row -> handler.accept(product(row)));
    }

    private static OFFProduct product(String[] row) {
        return OFFProduct
                .builder()
                .code(Code.parse(row[CODE]))
                .name(row[NAME])
                .build();
    }
}