package GroceryFamily.GroceryElders.service;

import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.GroceryElders.repo.ProductRepository;
import GroceryFamily.GroceryElders.service.exception.ProductNotFound;
import GroceryFamily.GroceryElders.service.mapper.DomainMapper;
import GroceryFamily.GroceryElders.service.mapper.ModelMapper;
import jakarta.transaction.Transactional;
import org.hibernate.StaleObjectStateException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.function.Supplier;

@Service
public class ProductService {
    private final ProductRepository repository;

    ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @Retryable(retryFor = StaleObjectStateException.class, maxAttempts = 10, backoff = @Backoff(delay = 100))
    public Product patch(String id, Product patch, Instant ts) {
        var product = repository
                .findById(id)
                .map(existing -> existing.patch(patch, ts))
                .orElse(ModelMapper.product(patch, ts));
        repository.save(product);
        return DomainMapper.product(repository.findById(id).orElseThrow(notFound(id)));
    }

    private static Supplier<ProductNotFound> notFound(String id) {
        return () -> new ProductNotFound(id);
    }
}