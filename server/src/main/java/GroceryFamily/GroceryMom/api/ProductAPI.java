package GroceryFamily.GroceryMom.api;

import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.GroceryElders.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("products")
public class ProductAPI {
    private final ProductService service;

    ProductAPI(ProductService service) {
        this.service = service;
    }

    // todo: list methods

    @ResponseStatus(OK)
    @GetMapping("{id}")
    Product get(@PathVariable String id) {
        return service.get(id);
    }

    @ResponseStatus(OK)
    @PostMapping("{id}")
    Product update(@PathVariable String id, @RequestBody Product product) {
        return service.update(id, product, Instant.now());
    }
}