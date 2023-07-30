package GroceryFamily.GroceryMom.resource;

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

    @ResponseStatus(OK)
    @PostMapping("{id}") // todo: PATCH method
    Product patch(@PathVariable String id, @RequestBody Product patch) {
        return service.update(id, patch, Instant.now());
    }
}