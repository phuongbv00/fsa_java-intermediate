package lecture.spring.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lecture.spring.web.model.Product;
import lecture.spring.web.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class ProductRestIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.7");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final String BASE_URL = "/api/products";

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ADMIN"})
    void testCreateAndReadProduct() throws Exception {
        Product product = new Product();
        product.setName("iPhone 15");
        product.setPrice(1200.0);
        product.setDescription("Apple flagship phone");
        product.setStockQuantity(10);
        product.setImageUrl("http://image");

        // Create product
        MvcResult createResult = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andReturn();

        Product created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), Product.class);
        assertNotNull(created);
        assertNotNull(created.getId());

        // Read product
        mockMvc.perform(get(BASE_URL + "/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("iPhone 15"))
                .andExpect(jsonPath("$.price").value(1200.0))
                .andExpect(jsonPath("$.description").value("Apple flagship phone"))
                .andExpect(jsonPath("$.stockQuantity").value(10))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.createdBy").value("user"))
                .andExpect(jsonPath("$.imageUrl").value("http://image"));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void testCreateAndReadProduct_Expect403() throws Exception {
        Product product = new Product();
        product.setName("iPhone 15");
        product.setPrice(1200.0);
        product.setDescription("Apple flagship phone");
        product.setStockQuantity(10);
        product.setImageUrl("http://image");

        // Create product
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ADMIN"})
    void testUpdateProduct() throws Exception {
        // Create product to update
        Product product = new Product();
        product.setName("Galaxy S24");
        product.setPrice(1100.0);
        product.setStockQuantity(5);
        product = productRepository.save(product);

        // Update the product
        product.setPrice(999.0);

        mockMvc.perform(put(BASE_URL + "/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Galaxy S24"))
                .andExpect(jsonPath("$.price").value(999.0))
                .andExpect(jsonPath("$.stockQuantity").value(5));

        // Verify the update was persisted
        Product updated = productRepository.findById(product.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals(999.0, updated.getPrice());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ADMIN"})
    void testDeleteProduct() throws Exception {
        // Create product to delete
        Product product = new Product();
        product.setName("Delete Me");
        product.setPrice(100.0);
        product = productRepository.save(product);

        // Delete the product
        mockMvc.perform(delete(BASE_URL + "/" + product.getId()))
                .andExpect(status().is2xxSuccessful());

        // Verify it was deleted
        mockMvc.perform(get(BASE_URL + "/" + product.getId()))
                .andExpect(status().isNotFound());
    }
}