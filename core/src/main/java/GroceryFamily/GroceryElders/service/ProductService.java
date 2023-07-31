package GroceryFamily.GroceryElders.service;

import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.GroceryElders.repository.ProductRepository;
import GroceryFamily.GroceryElders.service.exception.ProductNotFound;
import jakarta.transaction.Transactional;
import org.hibernate.StaleObjectStateException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static GroceryFamily.GroceryElders.service.mapper.PriceMapper.modelPrices;
import static GroceryFamily.GroceryElders.service.mapper.ProductMapper.domainProduct;
import static GroceryFamily.GroceryElders.service.mapper.ProductMapper.modelProduct;

@Service
public class ProductService {
    private final ProductRepository repository;

    ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product get(String id) {
        return domainProduct(repository.findById(id).orElseThrow(notFound(id)));
    }

    @Transactional
    @Retryable(retryFor = StaleObjectStateException.class, maxAttempts = 10, backoff = @Backoff(delay = 100))
    public Product update(String id, Product domainProduct, Instant ts) {
        var modelProduct = repository
                .findById(id)
                .map(update(domainProduct, ts))
                .orElse(modelProduct(domainProduct, ts));
        repository.save(modelProduct);
        return get(id);
    }

    private static UnaryOperator<GroceryFamily.GroceryElders.model.Product> update(Product domainProduct, Instant ts) {
        return modelProduct -> modelProduct
                .setName(domainProduct.name)
                .setPrices(modelPrices(domainProduct.identifiablePrices(), ts))
                .setTs(ts);
    }

    private static Supplier<ProductNotFound> notFound(String id) {
        return () -> new ProductNotFound(id);
    }
}