package GroceryFamily.GroceryMom.api;

import GroceryFamily.GroceryElders.domain.Page;
import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.GroceryMom.service.ProductService;
import GroceryFamily.GroceryMom.service.exception.ProductNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("products")
public class ProductAPI {
    private final ProductService service;

    ProductAPI(ProductService service) {
        this.service = service;
    }

    @ResponseStatus(OK)
    @GetMapping
    Page<Product> list() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @ResponseStatus(OK)
    @GetMapping("{id}")
    Product get(@PathVariable String id) {
        return service.get(id);
    }

    @ResponseStatus(OK)
    @PostMapping("{id}")
    void update(@PathVariable String id, @RequestBody Product product) {
        service.update(id, product, Instant.now());
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(ProductNotFoundException.class)
    String notFound(ProductNotFoundException e) {
        return e.getMessage();
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    String error(Exception e) {
        return e.getMessage();
    }
}