package GroceryFamily.GroceryMom.service;

import GroceryFamily.GroceryElders.domain.Page;
import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.GroceryMom.model.Price;
import GroceryFamily.GroceryMom.repository.ProductRepository;
import GroceryFamily.GroceryMom.service.exception.ProductNotFoundException;
import org.hibernate.StaleObjectStateException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static GroceryFamily.GroceryMom.service.mapper.PriceMapper.modelPrice;
import static GroceryFamily.GroceryMom.service.mapper.ProductMapper.domainProduct;
import static GroceryFamily.GroceryMom.service.mapper.ProductMapper.modelProduct;
import static org.springframework.transaction.annotation.Isolation.READ_UNCOMMITTED;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
public class ProductService {
    private final ProductRepository repository;

    ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Page<Product> list() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Page<Product> list(String pageToken) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Product get(String id) {
        return domainProduct(repository.findById(id).orElseThrow(notFound(id)));
    }

    @Transactional(propagation = REQUIRES_NEW, isolation = READ_UNCOMMITTED)
    @Retryable(retryFor = {
            StaleObjectStateException.class,
            DataIntegrityViolationException.class
    }, maxAttempts = 5, backoff = @Backoff(delay = 100, multiplier = 2))
    public void update(String id, Product domainProduct, Instant ts) {
        var modelProduct = repository
                .findById(id)
                .map(update(domainProduct, ts))
                .orElseGet(() -> modelProduct(domainProduct, ts));
        repository.save(modelProduct);
    }

    private static UnaryOperator<GroceryFamily.GroceryMom.model.Product> update(Product domainProduct, Instant ts) {
        return modelProduct -> {
            var modelPrices = new HashMap<String, GroceryFamily.GroceryMom.model.Price>();
            modelProduct.getPrices().forEach(modelPrice -> modelPrices.put(modelPrice.getId(), modelPrice));
            domainProduct.identifiablePrices().forEach((id, domainPrice) -> {
                int version = Optional
                        .ofNullable(modelPrices.get(id))
                        .map(Price::getVersion)
                        .orElse(0);
                modelPrices.put(id, modelPrice(id, domainPrice, ts, modelProduct, version));
            });
            return modelProduct
                    .setName(domainProduct.name)
                    .setPrices(new ArrayList<>(modelPrices.values()))
                    .setTs(ts);
        };
    }

    private static Supplier<ProductNotFoundException> notFound(String id) {
        return () -> new ProductNotFoundException(id);
    }
}