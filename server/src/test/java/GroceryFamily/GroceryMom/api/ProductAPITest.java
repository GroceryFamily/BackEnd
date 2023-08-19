package GroceryFamily.GroceryMom.api;

import GroceryFamily.GroceryElders.domain.Page;
import GroceryFamily.GroceryElders.domain.Price;
import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.GroceryMom.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.util.*;

import static java.util.stream.DoubleStream.builder;
import static org.hamcrest.Matchers.is;
import static net.bytebuddy.matcher.ElementMatchers.isGenericGetter;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductAPITest {

    private MockMvc mockMvc;
    @InjectMocks
    private ProductAPI productAPI;
    @Mock
    private ProductService service;
    private List<Product> products;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(productAPI).build();

    }

    @Test
    void testListProductsByPageSize() throws Exception{

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
    void testListProductsByPageToken() throws Exception{
        Set<Price> prices= new HashSet<>();
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
        Product product1= Product.builder()
                .namespace("zoo.gou")
                .code("42123")
                .name("Rock 100 kg")
                .prices(prices)
                .build();
        List<Product> products=new ArrayList<>();
        products.add(product);
        products.add(product1);

        System.out.println(products.size());
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

}