package GroceryFamily.GroceryElders.service;

import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.GroceryElders.repository.PriceRepository;
import GroceryFamily.GroceryElders.repository.ProductRepository;
import GroceryFamily.GroceryElders.service.exception.ProductNotFound;
import GroceryFamily.GroceryElders.service.mapper.DomainMapper;
import GroceryFamily.GroceryElders.service.mapper.ModelMapper;
import jakarta.transaction.Transactional;
import org.hibernate.StaleObjectStateException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;

    ProductService(ProductRepository productRepository, PriceRepository priceRepository) {
        this.productRepository = productRepository;
        this.priceRepository = priceRepository;
    }

    @Transactional
    @Retryable(retryFor = StaleObjectStateException.class, maxAttempts = 10, backoff = @Backoff(delay = 100))
    public Product update(String id, Product patch, Instant ts) {
        var product = productRepository
                .findById(id)
                .map(existing -> existing.patch(patch, ts))
                .orElse(ModelMapper.product(patch, ts));
        var prices = product.getPrices();
        productRepository.save(product.setPrices(List.of()));
        priceRepository.saveAll(prices);
        return DomainMapper.product(productRepository.findById(id).orElseThrow(notFound(id)));
    }

    private static Supplier<ProductNotFound> notFound(String id) {
        return () -> new ProductNotFound(id);
    }
}