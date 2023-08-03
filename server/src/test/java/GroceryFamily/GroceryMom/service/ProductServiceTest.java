package GroceryFamily.GroceryMom.service;

import GroceryFamily.GroceryElders.domain.Price;
import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.GroceryMom.repository.ProductRepository;
import GroceryFamily.GroceryMom.service.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@DataJpaTest
@ActiveProfiles("h2")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class ProductServiceTest {
    public static final String CURRENCY = "usd";
    private static final Instant NOW = Instant.now();

    @Autowired
    ProductRepository repository;
    @Autowired
    TestEntityManager entityManager;

    ProductService service;

    @BeforeEach
    void beforeEach() {
        service = new ProductService(repository);
    }

    /*
     Slurmesso is a delicious and highly addictive drink. You'll need to mix one
     portion of slurm and one portion of espresso. You won't be able to resist
     another slurmesso right after you've had your current one.
     */
    @Test
    void test() {
        assertThatThrownBy(() -> service.get("futurama::slurmesso"))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product 'futurama::slurmesso' not found");

        { // create new product
            service.update("futurama::slurmesso", product(
                    "futurama",
                    "slurmesso",
                    "Slurmesso 360ml",
                    price("pc", "5.49"),
                    price("ml", "0.1525")), NOW);

            assertThat(service.get("futurama::slurmesso"))
                    .isEqualTo(product(
                            "futurama",
                            "slurmesso",
                            "Slurmesso 360ml",
                            price("pc", "5.49"),
                            price("ml", "0.1525")));

            assertModelProduct("futurama::slurmesso", "futurama", "slurmesso", "Slurmesso 360ml", 0);
            assertModelPrice("futurama::slurmesso", "futurama::slurmesso::pc::usd", "pc", "5.49", 0);
            assertModelPrice("futurama::slurmesso", "futurama::slurmesso::ml::usd", "ml", "0.1525", 0);
        }

        { // namespace and code cannot be changed
            service.update("futurama::slurmesso", product(
                    "x",
                    "y",
                    "Slurmesso 0.36l"), NOW);

            assertThat(service.get("futurama::slurmesso"))
                    .isEqualTo(product(
                            "futurama",
                            "slurmesso",
                            "Slurmesso 0.36l",
                            price("pc", "5.49"),
                            price("ml", "0.1525")));

            assertModelProduct("futurama::slurmesso", "futurama", "slurmesso", "Slurmesso 0.36l", 1);
            assertModelPrice("futurama::slurmesso", "futurama::slurmesso::pc::usd", "pc", "5.49", 0);
            assertModelPrice("futurama::slurmesso", "futurama::slurmesso::ml::usd", "ml", "0.1525", 0);
        }

        { // update product name and add new price
            service.update("futurama::slurmesso", product(
                    "futurama",
                    "slurmesso",
                    "Slurmesso 0.36l",
                    price("l", "15.25")), NOW);

            assertThat(service.get("futurama::slurmesso"))
                    .isEqualTo(product(
                            "futurama",
                            "slurmesso",
                            "Slurmesso 0.36l",
                            price("pc", "5.49"),
                            price("ml", "0.1525"),
                            price("l", "15.25")));

            assertModelProduct("futurama::slurmesso", "futurama", "slurmesso", "Slurmesso 0.36l", 1);
            assertModelPrice("futurama::slurmesso", "futurama::slurmesso::pc::usd", "pc", "5.49", 0);
            assertModelPrice("futurama::slurmesso", "futurama::slurmesso::ml::usd", "ml", "0.1525", 0);
            assertModelPrice("futurama::slurmesso", "futurama::slurmesso::l::usd", "l", "15.25", 0);
        }

        { // update product name and one price
            service.update("futurama::slurmesso", product(
                    "futurama",
                    "slurmesso",
                    "Slurmesso 0.13l",
                    price("pc", "1.83")), NOW);

            assertThat(service.get("futurama::slurmesso"))
                    .isEqualTo(product(
                            "futurama",
                            "slurmesso",
                            "Slurmesso 0.13l",
                            price("pc", "1.83"),
                            price("ml", "0.1525"),
                            price("l", "15.25")));

            assertModelProduct("futurama::slurmesso", "futurama", "slurmesso", "Slurmesso 0.13l", 2);
            assertModelPrice("futurama::slurmesso", "futurama::slurmesso::pc::usd", "pc", "1.83", 1);
            assertModelPrice("futurama::slurmesso", "futurama::slurmesso::ml::usd", "ml", "0.1525", 0);
            assertModelPrice("futurama::slurmesso", "futurama::slurmesso::l::usd", "l", "15.25", 0);
        }
    }

    void assertModelProduct(String id, String namespace, String code, String name, int version) {
        entityManager.flush();
        var modelProduct = repository.findById(id).orElseThrow();
        assertThat(modelProduct.getId()).isEqualTo(id);
        assertThat(modelProduct.getNamespace()).isEqualTo(namespace);
        assertThat(modelProduct.getCode()).isEqualTo(code);
        assertThat(modelProduct.getName()).isEqualTo(name);
        assertThat(modelProduct.getTs()).isEqualTo(NOW);
        assertThat(modelProduct.getVersion()).isEqualTo(version);

    }

    void assertModelPrice(String productId, String id, String unit, String amount, int version) {
        entityManager.flush();
        var modelProduct = repository.findById(productId).orElseThrow();
        var modelPrice = modelProduct
                .getPrices()
                .stream()
                .filter(mp -> mp.getId().equals(id))
                .findAny()
                .orElseThrow();
        assertThat(modelPrice.getId()).isEqualTo(id);
        assertThat(modelPrice.getUnit()).isEqualTo(unit);
        assertThat(modelPrice.getCurrency()).isEqualTo(CURRENCY);
        assertThat(modelPrice.getAmount()).isEqualByComparingTo(amount);
        assertThat(modelPrice.getTs()).isEqualTo(NOW);
        assertThat(modelPrice.getVersion()).isEqualTo(version);
        assertThat(modelPrice.getProduct().getId()).isEqualTo(productId);
    }

    public static Product product(String namespace, String code, String name, Price... prices) {
        return Product
                .builder()
                .namespace(namespace)
                .code(code)
                .name(name)
                .prices(Set.of(prices))
                .build();
    }

    public static Price price(String unit, String amount) {
        return Price
                .builder()
                .unit(unit)
                .currency(CURRENCY)
                .amount(new BigDecimal(amount))
                .build();
    }
}