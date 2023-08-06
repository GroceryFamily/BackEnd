package GroceryFamily.GroceryMom.service;

import GroceryFamily.GroceryElders.domain.Page;
import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.GroceryMom.model.PageToken;
import GroceryFamily.GroceryMom.repository.ProductRepository;
import GroceryFamily.GroceryMom.repository.entity.CategoryEntity;
import GroceryFamily.GroceryMom.repository.entity.PriceEntity;
import GroceryFamily.GroceryMom.repository.entity.ProductEntity;
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

import static org.springframework.transaction.annotation.Isolation.READ_UNCOMMITTED;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
public class ProductService {
    private final ProductRepository repository;

    ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Page<Product> list(int pageSize) {
        var entities = repository.list(pageSize + 1);
        return ProductEntity.toDomainProductPage(entities, pageSize);
    }

    public Page<Product> list(String pageToken) {
        var token = PageToken.decode(pageToken);
        var entities = repository.list(token.pageHeadId, token.pageSize + 1);
        return ProductEntity.toDomainProductPage(entities, token.pageSize);
    }

    public Product get(String id) {
        return repository.findById(id).map(ProductEntity::toDomainProduct).orElseThrow(notFound(id));
    }

    @Transactional(propagation = REQUIRES_NEW, isolation = READ_UNCOMMITTED)
    @Retryable(retryFor = {
            StaleObjectStateException.class,
            DataIntegrityViolationException.class
    }, maxAttempts = 5, backoff = @Backoff(delay = 100, multiplier = 2))
    public void update(String id, Product product, Instant ts) {
        var entity = repository
                .findById(id)
                .map(update(product, ts))
                .orElseGet(() -> ProductEntity.fromDomainProduct(product, ts));
        repository.save(entity);
    }

    private static UnaryOperator<ProductEntity> update(Product product, Instant ts) {
        return productEntity -> {
            var priceEntities = new HashMap<String, PriceEntity>();
            productEntity.getPrices().forEach(priceEntity -> priceEntities.put(priceEntity.getId(), priceEntity));
            product.identifiablePrices().forEach(price -> {
                int version = Optional
                        .ofNullable(priceEntities.get(price.id))
                        .map(PriceEntity::getVersion)
                        .orElse(0);
                priceEntities.put(price.id, PriceEntity.fromDomainPrice(price.id, price.data, ts, version, productEntity));
            });

            var categoryEntities = new HashMap<String, CategoryEntity>();
            productEntity.getCategories().forEach(categoryEntity -> categoryEntities.put(categoryEntity.getId(), categoryEntity));
            product.identifiableCategories().forEach(category -> {
                int version = Optional
                        .ofNullable(categoryEntities.get(category.id))
                        .map(CategoryEntity::getVersion)
                        .orElse(0);
                categoryEntities.put(category.id, CategoryEntity.fromDomainCategory(category.id, category.data, ts, version, productEntity));
            });

            return productEntity
                    .setName(product.name)
                    .setTs(ts)
                    .setPrices(new ArrayList<>(priceEntities.values()))
                    .setCategories(new ArrayList<>(categoryEntities.values()));
        };
    }

    private static Supplier<ProductNotFoundException> notFound(String id) {
        return () -> new ProductNotFoundException(id);
    }
}