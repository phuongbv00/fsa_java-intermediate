package challenge.quiz02;

import challenge.quiz02.model.Product;
import challenge.quiz02.repository.ProductInMemoryRepository;
import challenge.quiz02.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class ProductInMemoryRepositoryTest {
    private Repository<Integer, Product> repository;

    @BeforeEach
    void setUp() {
        repository = new ProductInMemoryRepository();
    }

    @Test
    void testSaveAndFindById() {
        Product product = new Product(1, "Test Product", 10.0, Instant.now());
        repository.save(product);
        Optional<Product> retrieved = repository.findById(1);
        assertTrue(retrieved.isPresent());
        assertEquals(product, retrieved.get());
    }

    @Test
    void testFindAll() {
        Product product1 = new Product(1, "Product 1", 20.0, Instant.now());
        Product product2 = new Product(2, "Product 2", 30.0, Instant.now());
        repository.save(product1);
        repository.save(product2);
        Collection<Product> products = repository.findAll();
        assertEquals(2, products.size());
        assertTrue(products.contains(product1));
        assertTrue(products.contains(product2));
    }

    @Test
    void testFindAllWithFilterAndSorting() {
        Product product1 = new Product(1, "Apple", 15.0, Instant.now().minusSeconds(100));
        Product product2 = new Product(2, "Banana", 10.0, Instant.now().minusSeconds(200));
        Product product3 = new Product(3, "Cherry", 25.0, Instant.now().minusSeconds(50));
        Product product4 = new Product(4, "Date", 20.0, Instant.now().minusSeconds(300));
        repository.save(product1);
        repository.save(product2);
        repository.save(product3);
        repository.save(product4);

        Collection<Product> filteredSortedProducts = repository.findAll(
                p -> p.getPrice() > 12.0 && p.getName().startsWith("A"),
                Comparator.comparing(Product::getCreatedAt).reversed()
        );

        assertEquals(1, filteredSortedProducts.size());
        assertTrue(filteredSortedProducts.contains(product1));
    }

    @Test
    void testUpdate() {
        Product product = new Product(1, "Old Name", 40.0, Instant.now());
        repository.save(product);
        Product updatedProduct = new Product(1, "New Name", 50.0, Instant.now());
        repository.update(updatedProduct);
        Optional<Product> retrieved = repository.findById(1);
        assertTrue(retrieved.isPresent());
        assertEquals("New Name", retrieved.get().getName());
        assertEquals(50.0, retrieved.get().getPrice());
    }

    @Test
    void testUpdateNotFound() {
        Product notFoundProduct = new Product(2, "New Name", 60.0, Instant.now());
        assertThrows(NoSuchElementException.class, () -> repository.update(notFoundProduct));
    }

    @Test
    void testDelete() {
        Product product = new Product(1, "Test Product", 25.0, Instant.now());
        repository.save(product);
        repository.delete(1);
        Optional<Product> retrieved = repository.findById(1);
        assertFalse(retrieved.isPresent());
    }

    @Test
    void testConcurrentModification() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            int id = i;
            executorService.execute(() -> {
                try {
                    repository.save(new Product(id, "Product " + id, 5.0 * id, Instant.now()));
                    repository.findById(id);
                    repository.update(new Product(id, "Updated Product " + id, 10.0 * id, Instant.now()));
                    repository.delete(id);
                } catch (Throwable t) {
                    exceptions.add(t);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
        assertTrue(exceptions.isEmpty());
    }
}
