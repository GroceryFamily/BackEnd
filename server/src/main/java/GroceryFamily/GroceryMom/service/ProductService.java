package GroceryFamily.GroceryMom.service;

import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.GroceryElders.model.Price;
import GroceryFamily.GroceryElders.repository.ProductRepository;
import GroceryFamily.GroceryMom.service.exception.ProductNotFoundException;
import jakarta.transaction.Transactional;
import org.hibernate.StaleObjectStateException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static GroceryFamily.GroceryMom.service.mapper.PriceMapper.modelPrice;
import static GroceryFamily.GroceryMom.service.mapper.ProductMapper.domainProduct;
import static GroceryFamily.GroceryMom.service.mapper.ProductMapper.modelProduct;
import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;

@Service
public class ProductService {
    private final ProductRepository repository;

    ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional(SUPPORTS)
    public Product get(String id) {
        return domainProduct(repository.findById(id).orElseThrow(notFound(id)));
    }

    @Transactional(REQUIRED)
    @Retryable(retryFor = StaleObjectStateException.class, maxAttempts = 10, backoff = @Backoff(delay = 100))
    public Product update(String id, Product domainProduct, Instant ts) {
        var modelProduct = repository
                .findById(id)
                .map(update(domainProduct, ts))
                .orElseGet(() -> modelProduct(domainProduct, ts));
        repository.save(modelProduct);
        return get(id);
    }

    private static UnaryOperator<GroceryFamily.GroceryElders.model.Product> update(Product domainProduct, Instant ts) {
        return modelProduct -> {
            var modelPrices = new HashMap<String, GroceryFamily.GroceryElders.model.Price>();
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