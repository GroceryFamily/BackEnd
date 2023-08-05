package GroceryFamily.GroceryMom.service.mapper;


import GroceryFamily.GroceryMom.model.PageToken;
import GroceryFamily.GroceryMom.repository.entity.ProductEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static GroceryFamily.GroceryMom.service.mapper.PriceMapper.modelPrices;

public class ProductMapper {
    public static ProductEntity
    modelProduct(GroceryFamily.GroceryElders.domain.Product domainProduct, Instant ts) {
        var modelProduct = new ProductEntity();
        return modelProduct
                .setId(domainProduct.id())
                .setNamespace(domainProduct.namespace)
                .setCode(domainProduct.code)
                .setName(domainProduct.name)
                .setPrices(modelPrices(domainProduct, ts, modelProduct))
                .setTs(ts)
                .setVersion(0);
    }

    public static GroceryFamily.GroceryElders.domain.Page<GroceryFamily.GroceryElders.domain.Product>
    domainProductPage(List<ProductEntity> modelProducts, int pageSize) {
        if (modelProducts.isEmpty()) return GroceryFamily.GroceryElders.domain.Page.empty();
        var nextPageToken = Optional
                .ofNullable(modelProducts.size() > pageSize ? modelProducts.get(modelProducts.size() - 1) : null)
                .map(nextPageHead -> PageToken.builder().pageHeadId(nextPageHead.getId()).pageSize(pageSize).build())
                .map(PageToken::encode)
                .orElse(null);
        return GroceryFamily.GroceryElders.domain.Page
                .<GroceryFamily.GroceryElders.domain.Product>builder()
                .content(modelProducts.stream().limit(pageSize).map(ProductMapper::domainProduct).toList())
                .nextPageToken(nextPageToken)
                .build();
    }

    public static GroceryFamily.GroceryElders.domain.Product
    domainProduct(ProductEntity modelProduct) {
        return GroceryFamily.GroceryElders.domain.Product
                .builder()
                .namespace(modelProduct.getNamespace())
                .code(modelProduct.getCode())
                .name(modelProduct.getName())
                .prices(PriceMapper.domainPrices(modelProduct.getPrices()))
                .build();
    }
}