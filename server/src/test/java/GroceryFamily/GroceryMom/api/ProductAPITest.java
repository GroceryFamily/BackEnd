package GroceryFamily.GroceryMom.api;

import GroceryFamily.GroceryElders.domain.Page;
import GroceryFamily.GroceryElders.domain.Price;
import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.GroceryMom.service.ProductService;
import GroceryFamily.GroceryMom.service.exception.ProductNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpServerErrorException;


import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductAPITest {

    private MockMvc mockMvc;
    @InjectMocks
    private ProductAPI productAPI;
    @Mock
    private ProductService service;
    private List<Product> products;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        this.mockMvc = MockMvcBuilders.standaloneSetup(productAPI).build();

        Set<Price> prices = new HashSet<>();
        Price price = Price.builder()
                .unit("pc")
                .currency("aden")
                .amount(new BigDecimal(100))
                .build();
        prices.add(price);
        Product product = Product.builder()
                .namespace("zoo.buy")
                .code("32133")
                .name("Tiger 100 kg")
                .prices(prices)
                .build();
        Product product1 = Product.builder()
                .namespace("zoo.gou")
                .code("42123")
                .name("Rock 100 kg")
                .prices(prices)
                .build();
        this.products = new ArrayList<>();
        products.add(product);
        products.add(product1);

    }

    @Test
    void testListProductsByPageSize() throws Exception {

        Page<Product> testPage = Page.<Product>builder()
                .content(products)
                .nextPageToken("some_Token")
                .build();

        when(service.list(anyInt())).thenReturn(testPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/products?pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nextPageToken", is("some_Token")));

    }

    @Test
    void testListProductsByPageToken() throws Exception {

        Page<Product> testPage = Page.<Product>builder()
                .content(products)
                .nextPageToken("some_Token")
                .build();

        when(service.list(anyString())).thenReturn(testPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/products?pageToken=some_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nextPageToken", is("some_Token")))
                .andExpect(jsonPath("$.content.length()", is(2)));
    }

    @Test
    void getProductByIdTest() {

        when(service.get("zoo.buy:32133")).thenReturn(products.get(0));

        Product actualProduct = productAPI.get("zoo.buy:32133");

        Assertions.assertEquals(products.get(0), actualProduct);

    }

    @Test
    void postProductByIdTest() throws Exception {

        Product product = products.get(0);

        ObjectMapper objectMapper = new ObjectMapper();
        String productJson = objectMapper.writeValueAsString(product);

        mockMvc.perform(MockMvcRequestBuilders.post("/products/{id}", "zoo.buy:32133")
                        .contentType("application/json")
                        .content(productJson))
                .andExpect(status().isOk());


        when(service.get("zoo.buy:32133")).thenReturn(product);
    }

    @Test
    void ifProductNotFound_then_comes_Exception() throws Exception {

        when(service.get("some_id")).thenThrow(new ProductNotFoundException("some_id"));

        mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", "some_id"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product 'some_id' not found"));

    }

    @Test
    void internalServerError_Test() throws Exception {

        when(service.get("some_id")).thenThrow(HttpServerErrorException.InternalServerError.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", "some_id"))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }
}
