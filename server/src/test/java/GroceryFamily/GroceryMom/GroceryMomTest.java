package GroceryFamily.GroceryMom;

import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import GroceryFamily.GroceryElders.domain.Price;
import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.GroceryMom.repository.PriceRepository;
import GroceryFamily.GroceryMom.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/*
 This is the main integration test that confirms the correctness of the
 implementation of the optimistic locking approach for the product update
 method. It uses 3 parallel threads, each doing 9 consecutive updates of the
 same product. Each update uses unique markers for the product and its prices.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
class GroceryMomTest {
    static final int NUMBER_OF_THREADS = 3;
    static final int NUMBER_OF_UPDATES_PER_THREAD = 99;

    @LocalServerPort
    int port;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    PriceRepository priceRepository;

    ProductAPIClient client;

    @BeforeEach
    void beforeEach() {
        client = new ProductAPIClient("http://localhost:" + port);
    }

    @Test
    void test() {
        var updates = new AtomicInteger();
        var threadPool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        try {
            var start = new CountDownLatch(1);
            var finish = new CountDownLatch(NUMBER_OF_THREADS);
            IntStream.rangeClosed(1, NUMBER_OF_THREADS)
                    .boxed()
                    .map(no -> thread(start, finish, no + 1, updates))
                    .forEach(threadPool::submit);
            start.countDown();
            finish.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            threadPool.shutdown();
        }

        assertThat(updates).hasValue(NUMBER_OF_THREADS * NUMBER_OF_UPDATES_PER_THREAD);

        var expectedProductNameRegex = "GroceryMom test product \\(threadNo=[0-9]+, updateNo=" + NUMBER_OF_UPDATES_PER_THREAD + "\\)";

        var domainProduct = client.get("grocery-mom-test::product");
        assertThat(domainProduct.namespace).isEqualTo("grocery-mom-test");
        assertThat(domainProduct.code).isEqualTo("product");
        assertThat(domainProduct.name).matches(Pattern.compile(expectedProductNameRegex));
        assertThat(domainProduct.prices).hasSize(2);

        var expectedThreadNo = threadNo(domainProduct);
        var expectedPriceAmount = expectedThreadNo + "." + NUMBER_OF_UPDATES_PER_THREAD;

        var pcDomainPrice = domainProduct.identifiablePrices().get("grocery-mom-test::product::pc::usd");
        assertThat(pcDomainPrice.unit).isEqualTo("pc");
        assertThat(pcDomainPrice.currency).isEqualTo("usd");
        assertThat(pcDomainPrice.amount).isEqualByComparingTo(expectedPriceAmount);

        var mlDomainPrice = domainProduct.identifiablePrices().get("grocery-mom-test::product::ml::usd");
        assertThat(mlDomainPrice.unit).isEqualTo("ml");
        assertThat(mlDomainPrice.currency).isEqualTo("usd");
        assertThat(mlDomainPrice.amount).isEqualByComparingTo(expectedPriceAmount);

        var expectedProductName = "GroceryMom test product (threadNo=" + expectedThreadNo + ", updateNo=" + NUMBER_OF_UPDATES_PER_THREAD + ")";
        var expectedVersion = NUMBER_OF_THREADS * NUMBER_OF_UPDATES_PER_THREAD - 1;

        assertModelProduct("grocery-mom-test::product", expectedProductName, expectedVersion);
        assertModelPrice("grocery-mom-test::product::pc::usd", expectedPriceAmount, expectedVersion);
        assertModelPrice("grocery-mom-test::product::ml::usd", expectedPriceAmount, expectedVersion);
    }

    void assertModelProduct(String id, String expectedName, int expectedVersion) {
        var modelProduct = productRepository.findById(id).orElseThrow();
        assertThat(modelProduct.getName()).isEqualTo(expectedName);
        assertThat(modelProduct.getVersion()).isEqualTo(expectedVersion);
    }

    void assertModelPrice(String id, String expectedAmount, int expectedVersion) {
        var modelPrice = priceRepository.findById(id).orElseThrow();
        assertThat(modelPrice.getId()).isEqualTo(id);
        assertThat(modelPrice.getAmount()).isEqualByComparingTo(expectedAmount);
        assertThat(modelPrice.getVersion()).isEqualTo(expectedVersion);
    }

    Runnable thread(CountDownLatch start, CountDownLatch finish, int no, AtomicInteger updates) {
        Thread.currentThread().setName("update-thread-" + no);
        return () -> {
            try {
                start.await();
                int updateNo = 0;
                while (updateNo++ < NUMBER_OF_UPDATES_PER_THREAD) {
                    client.update(product(no, updateNo));
                    updates.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                finish.countDown();
            }
        };
    }

    static int threadNo(Product product) {
        return Integer.parseInt(product.name.substring(product.name.indexOf('=') + 1, product.name.indexOf(',')));
    }

    static Product product(int threadNo, int updateNo) {
        return Product
                .builder()
                .namespace("grocery-mom-test")
                .code("product")
                .name(format("GroceryMom test product (threadNo=%s, updateNo=%s)", threadNo, updateNo))
                .prices(prices(threadNo, updateNo))
                .build();
    }

    static Set<Price> prices(int threadNo, int updateNo) {
        var amount = threadNo + "." + updateNo;
        return Set.of(price("pc", amount), price("ml", amount));
    }

    static Price price(String unit, String amount) {
        return Price
                .builder()
                .unit(unit)
                .currency("usd")
                .amount(new BigDecimal(amount))
                .build();
    }
}