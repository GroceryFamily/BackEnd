package GroceryFamily.GroceryDad;

import GroceryFamily.GroceryDad.scraper.Scraper;
import GroceryFamily.GroceryDad.scraper.sample.TestSampleStorage;
import GroceryFamily.GroceryDad.scraper.worker.WorkerEventListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class GroceryDadTest {
    @Autowired
    Scraper scraper;
    @Autowired
    TestSampleStorage testSampleStorage;

    @Test
    void test() {
        var failures = new AtomicInteger();
        var listener = WorkerEventListener
                .builder()
                .productHandler(event -> {
                    var sample = testSampleStorage.load(event.platform, event.link);
                    assertThat(event.payload).isNotNull().isEqualTo(sample);
                })
                .errorHandler(event -> {
                    log.error(event.payload.getMessage());
                    failures.incrementAndGet();
                })
                .build();
        scraper.scrap(listener);
        assertThat(failures)
                .withFailMessage(() -> format("Non-zero number of errors occurred (%s)", failures))
                .hasValue(0)
        ;
    }
}