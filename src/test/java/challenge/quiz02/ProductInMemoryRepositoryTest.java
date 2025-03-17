package challenge.quiz02;

import challenge.quiz02.model.Product;
import challenge.quiz02.repository.ProductInMemoryRepository;
import challenge.quiz02.repository.ProductRepository;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

public class ProductInMemoryRepositoryTest {
    private ProductRepository repository;

    private final List<Product> mock = List.of(
            new Product(1, "Apple", 15.0, Instant.now().minusSeconds(100)),
            new Product(2, "Banana", 70.0, Instant.now().minusSeconds(200)),
            new Product(3, "Cherry", 25.0, Instant.now().minusSeconds(50)),
            new Product(4, "Date", 90.0, Instant.now().minusSeconds(300)),
            new Product(5, "Apple", 35.0, Instant.now().minusSeconds(100)),
            new Product(6, "Banana", 60.0, Instant.now().minusSeconds(200)),
            new Product(7, "Cherry", 85.0, Instant.now().minusSeconds(50)),
            new Product(8, "Date", 20.0, Instant.now().minusSeconds(300)),
            new Product(9, "Date", 50.0, Instant.now().minusSeconds(1000)),
            new Product(10, "Dong", 50.0, Instant.now().minusSeconds(2000))
    );

    @Before
    public void setUp() {
        repository = new ProductInMemoryRepository();
    }

    @Test
    public void saveAndFindById() {
        Product product = new Product(1, "Test Product", 10.0, Instant.now());
        repository.save(product);
        Optional<Product> retrieved = repository.findById(1);
        assertTrue(retrieved.isPresent());
        assertEquals(product, retrieved.get());
    }

    @Test
    public void findAll() {
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
    public void findAllWithFilterAndSorting() {
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
    public void update() {
        Product product = new Product(1, "Old Name", 40.0, Instant.now());
        repository.save(product);
        Product updatedProduct = new Product(1, "New Name", 50.0, Instant.now());
        repository.update(updatedProduct);
        Optional<Product> retrieved = repository.findById(1);
        assertTrue(retrieved.isPresent());
        assertEquals("New Name", retrieved.get().getName());
        assertEquals(50.0, retrieved.get().getPrice(), 0);
    }

    @Test(expected = NoSuchElementException.class)
    public void updateNotFound() {
        Product notFoundProduct = new Product(2, "New Name", 60.0, Instant.now());
        repository.update(notFoundProduct);
    }

    @Test
    public void delete() {
        Product product = new Product(1, "Test Product", 25.0, Instant.now());
        repository.save(product);
        repository.delete(1);
        Optional<Product> retrieved = repository.findById(1);
        assertFalse(retrieved.isPresent());
    }

    @Test
    public void countBy() {
        mock.forEach(product -> repository.save(product));
        int count = repository.countBy(p -> p.getPrice() >= 50 && p.getName().startsWith("D"));
        assertEquals(3, count);
    }

    @Test
    public void partitionByPrice() {
        mock.forEach(product -> repository.save(product));
        Map<Boolean, Integer> partitions = repository.partitionByPrice(50.0);
        assertTrue(partitions.containsKey(true));
        assertTrue(partitions.containsKey(false));
        assertEquals(6, partitions.get(true).intValue());
        assertEquals(4, partitions.get(false).intValue());
    }

    @Test
    public void groupByName() {
        mock.forEach(product -> repository.save(product));
        Map<String, Product> groupByName = repository.groupByName();
        assertEquals(5, groupByName.size());
    }

    @Test
    public void concurrentModification() throws InterruptedException {
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
