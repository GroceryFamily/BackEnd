package GroceryFamily.GroceryMom.service;

import GroceryFamily.GroceryMom.repository.PriceRepository;
import GroceryFamily.GroceryMom.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class ProductServiceTest {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    PriceRepository priceRepository;
    @Autowired
    TestEntityManager entityManager;

    @Test
    void test() {

    }
}