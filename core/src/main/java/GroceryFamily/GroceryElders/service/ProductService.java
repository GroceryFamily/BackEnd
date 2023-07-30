package GroceryFamily.GroceryElders.service;

import GroceryFamily.GroceryElders.model.Product;
import GroceryFamily.GroceryElders.repo.ProductRepository;
import jakarta.transaction.Transactional;
import org.hibernate.StaleObjectStateException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    @Retryable(retryFor = StaleObjectStateException.class, maxAttempts = 10, backoff = @Backoff(delay = 100))
    public void patch(String id, GroceryFamily.GroceryElders.domain.Product product) {
        var now = Instant.now();
        var entity = productRepository
                .findById(id)
                .map(e -> e.patch(product, now))
                .orElse(Product.map(product, now));
        productRepository.save(entity);
    }
}