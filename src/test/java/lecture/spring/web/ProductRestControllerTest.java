package lecture.spring.web;

import lecture.spring.web.model.Product;
import lecture.spring.web.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductRestControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/products";
        productRepository.deleteAll();
    }

    @Test
    void testCreateAndReadProduct() {
        Product product = new Product();
        product.setName("iPhone 15");
        product.setPrice(1200.0);
        product.setDescription("Apple flagship phone");
        product.setStockQuantity(10);
        product.setImageUrl("http://image");

        ResponseEntity<Product> createResponse = restTemplate.postForEntity(baseUrl, product, Product.class);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());

        Product created = createResponse.getBody();
        assertNotNull(created);
        assertNotNull(created.getId());

        ResponseEntity<Product> readResponse = restTemplate.getForEntity(baseUrl + "/" + created.getId(), Product.class);
        assertEquals(HttpStatus.OK, readResponse.getStatusCode());

        Product fetched = readResponse.getBody();
        assertNotNull(fetched);
        assertEquals("iPhone 15", fetched.getName());
    }

    @Test
    void testUpdateProduct() {
        Product product = new Product();
        product.setName("Galaxy S24");
        product.setPrice(1100.0);
        product.setStockQuantity(5);
        product = productRepository.save(product);

        product.setPrice(999.0);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> entity = new HttpEntity<>(product, headers);

        ResponseEntity<Product> response = restTemplate.exchange(
                baseUrl + "/" + product.getId(), HttpMethod.PUT, entity, Product.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Product updated = response.getBody();
        assertNotNull(updated);
        assertEquals(999.0, updated.getPrice());
    }

    @Test
    void testDeleteProduct() {
        Product product = new Product();
        product.setName("Delete Me");
        product.setPrice(100.0);
        product = productRepository.save(product);

        restTemplate.delete(baseUrl + "/" + product.getId());

        ResponseEntity<Product> response = restTemplate.getForEntity(baseUrl + "/" + product.getId(), Product.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
