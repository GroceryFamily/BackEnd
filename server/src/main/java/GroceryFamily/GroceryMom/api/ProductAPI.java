package GroceryFamily.GroceryMom.api;

import GroceryFamily.GroceryElders.domain.Page;
import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.GroceryMom.service.ProductService;
import GroceryFamily.GroceryMom.service.exception.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequestMapping("products")
public class ProductAPI {
    private final ProductService service;

    ProductAPI(ProductService service) {
        this.service = service;
    }

    @ResponseStatus(OK)
    @GetMapping(params = "!pageToken")
    Page<Product> list(@RequestParam int pageSize) {
        return service.list(pageSize);
    }

    @ResponseStatus(OK)
    @GetMapping(params = "!pageSize")
    Page<Product> list(@RequestParam String pageToken) {
        return service.list(pageToken);
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
        log.error("Internal server error", e);
        return e.getMessage();
    }
}