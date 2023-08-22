package GroceryFamily.GrocerySis;

import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import GroceryFamily.GrocerySis.dataset.OFFProduct;
import GroceryFamily.GrocerySis.dataset.OFFProductDataset;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@SpringBootApplication
class GrocerySis implements CommandLineRunner {
    private final OFFProductDataset dataset;
    private final ProductAPIClient client;

    GrocerySis(OFFProductDataset dataset, ProductAPIClient client) {
        this.dataset = dataset;
        this.client = client;
    }

    @Override
    public void run(String... args) {
        /*
        AtomicInteger no = new AtomicInteger();
        client.listAll().forEach(product -> System.out.println(no.incrementAndGet() + ": " + product));
         */

        var cnt = new AtomicInteger();
        dataset.read(product -> {
            cnt.incrementAndGet();
        }, true);
        System.out.println("CNT: " + cnt);


//        var size = dataset.size();
//        var occurences = new HashMap<String, AtomicInteger>();
//        var fieldNames = dataset.columnNames();
//        for (var fieldName : fieldNames) {
//            occurences.put(fieldName, new AtomicInteger());
//        }
//        occurences.put("EAN13", new AtomicInteger());
//        occurences.put("EAN8", new AtomicInteger());
//        var unknown = new HashSet<String[]>();
//        try (var bar = new ConsoleProgressBar(size)) {
//            dataset.rows().peek(row -> bar.step()).forEach(fieldValues -> {
//                for (int i = 0; i < Math.min(fieldValues.length, fieldNames.length); ++i) {
//                    var fieldName = fieldNames[i];
//                    var fieldValue = fieldValues[i];
//                    if (fieldValue != null && !fieldValue.isEmpty()) {
//                        occurences.get(fieldName).incrementAndGet();
//                    }
//                    if ("code".equals(fieldName)) {
//                        if (isEAN13(fieldValue)) {
//                            occurences.get("EAN13").incrementAndGet();
//                        } else if (isEAN8(fieldValue)) {
//                            occurences.get("EAN8").incrementAndGet();
//                        } else {
//                            unknown.add(fieldValues);
//                        }
//                    }
//                }
//            });
//        }
//        final List<Occ> occs = new ArrayList<>();
//        occurences.forEach((fieldName, occ) -> occs.add(new Occ(fieldName, ((double) occ.get()) / size)));
//        var occsS = occs.stream()
//                .filter(o -> o.occ > 0.5)
//                .sorted(comparing(Occ::occ, reverseOrder()))
//                .toList();
//        log.info("Field occurrences: {}\n", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(occsS));
//        System.out.println("EAN13: " + occurences.get("EAN13"));
//        System.out.println("EAN8: " + occurences.get("EAN8"));
////        System.out.println("Unknown: " + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(unknown));
    }

    record Occ(String fieldName, double occ) {}

    static boolean isEAN13(String code) {
        return code != null && code.trim().length() == 13;
    }

    static boolean isEAN8(String code) {
        return code != null && code.trim().length() == 8;
    }

    public static void main(String... args) {
        SpringApplication.run(GrocerySis.class, args);
    }
}