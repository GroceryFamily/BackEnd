package GroceryFamily.GroceryMom.api;

import GroceryFamily.GroceryElders.domain.Page;
import GroceryFamily.GroceryElders.domain.Price;
import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.GroceryMom.service.ProductService;
import GroceryFamily.GroceryMom.service.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductAPITest {

    @InjectMocks
    private ProductAPI productAPI;
    @Mock
    private ProductService mockService;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        productAPI = new ProductAPI(mockService);

    }

    @Test
    void testListProductsByPageSize() throws Exception {

//region creating Products and Page
        Product apple = Product.builder().namespace("Apple").name("Golden").code("202").url("www.barbora.ee").prices(Set.of(Price.builder().unit("t").currency("$").amount(new BigDecimal("1.99")).build())).categories(Map.of("categoryKey", "categoryValue")).details(Map.of("detailKey", "detailValue")).build();
        Product banana = Product.builder().namespace("Banana").name("Diamond Big Banana").code("1").url("www.barbora.ee").prices(Set.of(Price.builder().unit("st").currency("$").amount(new BigDecimal("4.60")).build())).categories(Map.of("categoryKey", "categoryValue")).details(Map.of("detailKey", "detailValue")).build();

        List<Product> productList = List.of(apple, banana);
        Page<Product> barboraPage = Page.<Product>builder().content(productList).nextPageToken("fruitsToken").build();
        //endregion

        when(productAPI.list(2)).thenReturn(barboraPage);

        var products = productAPI.list(2);

        assertEquals(barboraPage, products, "checking for the correct size");
        assertNotEquals(barboraPage, 3, "checking for wrong number");
        assertNotEquals(barboraPage, -1, "check for negative number");


    }

    @Test
    void testListProductsByPageToken() throws Exception {

//region creating Products and Page
        Product apple = Product.builder().namespace("Apple").name("Golden").code("202").url("www.barbora.ee").prices(Set.of(Price.builder().unit("t").currency("$").amount(new BigDecimal("1.99")).build())).categories(Map.of("categoryKey", "categoryValue")).details(Map.of("detailKey", "detailValue")).build();
        Product banana = Product.builder().namespace("Banana").name("Diamond Big Banana").code("1").url("www.barbora.ee").prices(Set.of(Price.builder().unit("st").currency("$").amount(new BigDecimal("4.60")).build())).categories(Map.of("categoryKey", "categoryValue")).details(Map.of("detailKey", "detailValue")).build();

        List<Product> productList = List.of(apple, banana);
        Page<Product> barboraPage = Page.<Product>builder().content(productList).nextPageToken("fruitsToken").build();
        //endregion

        when(productAPI.list(anyString())).thenReturn(barboraPage);

        var token = productAPI.list("fruitsToken");

        assertEquals(barboraPage, token, "must be fruitToken");
        assertNotEquals(barboraPage, 2, "check for number");
        assertNotEquals(barboraPage, "token", "check for wrong string");

    }

    @Test
    void getProductByIdTest() {

//region creating Products
        Product apple = Product.builder().namespace("Apple").name("Golden").code("202").url("www.barbora.ee").prices(Set.of(Price.builder().unit("t").currency("$").amount(new BigDecimal("1.99")).build())).categories(Map.of("categoryKey", "categoryValue")).details(Map.of("detailKey", "detailValue")).build();


        Product banana = Product.builder().namespace("Banana").name("Diamond Big Banana").code("1").url("www.barbora.ee").prices(Set.of(Price.builder().unit("st").currency("$").amount(new BigDecimal("4.60")).build())).categories(Map.of("categoryKey", "categoryValue")).details(Map.of("detailKey", "detailValue")).build();
//endregion

        String bananaId = banana.id();
        String appleId = apple.id();

        when(productAPI.get(bananaId)).thenReturn(banana);

        Product actualProduct = productAPI.get(bananaId);

        assertEquals(banana, actualProduct, "check true productId");
        assertNotEquals(banana, appleId, "check another id");
        assertNotEquals(banana, "Banana::goAway", "check another string");
        assertNotEquals(banana, 2, "check for number");
        assertNotEquals(banana, -2, "check for negative number");

    }

    @Test
    void postProductByIdTest() {

        //region creating Product
        Product banana = Product.builder().namespace("Banana").name("Diamond Big Banana").code("1").url("www.barbora.ee").prices(Set.of(Price.builder().unit("st").currency("$").amount(new BigDecimal("4.60")).build())).categories(Map.of("categoryKey", "categoryValue")).details(Map.of("detailKey", "detailValue")).build();
//endregion
        String bananaId = banana.id();


        productAPI.update(bananaId, banana);
        verify(mockService).update(eq(bananaId), eq(banana), any(Instant.class));

    }

    @Test
    void ifProductNotFoundException() {

        String productId = "some::id";

        when(productAPI.get(productId)).thenThrow(new ProductNotFoundException("Product not found"));

        ProductNotFoundException thrown = assertThrows(ProductNotFoundException.class, () -> productAPI.get(productId)
                , "There were no exceptions");

        assertTrue(thrown.getMessage().contains("Product not found"));

    }

    @Test
    void internalServerErrorTest() {

        String productId = "Product::Id";

        when(mockService.get(productId)).thenThrow(new RuntimeException("Internal server error"));

        Exception exception = assertThrows(RuntimeException.class,
                () -> productAPI.get(productId));

        assertEquals("Internal server error", exception.getMessage());

    }

}

