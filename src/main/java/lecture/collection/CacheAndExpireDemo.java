package lecture.collection;

import java.time.Instant;
import java.util.*;

public class CacheAndExpireDemo {
    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();

        // Store cache keys
        Map<Integer, CacheKey<Integer>> cacheKeys = new HashMap<>();

        // Get data from DB - simulate
        List<Product> dbProducts = List.of(
                new Product(1, "a"),
                new Product(2, "b"),
                new Product(3, "b2"),
                new Product(4, "4"),
                new Product(5, "www"),
                new Product(6, "www2")
        );

        // Init cache
        Map<CacheKey<Integer>, CacheItem> cache = new TreeMap<>();

        // Put products list into cache, each element has specific expires
        dbProducts.forEach(product -> {
            Instant now = Instant.now();
            long expires = now.getEpochSecond() + random.nextLong(30);
            CacheItem item = new CacheItem(product.toString(), expires);
            CacheKey<Integer> key = new CacheKey<>(product.id, expires);
            cacheKeys.put(product.id, key);
            cache.put(key, item);
        });

        System.out.println("Initial:");
        System.out.println("Cache size: " + cache.size());
        System.out.println(cache);

        // Get from cache
        System.out.println("===");
        System.out.println("Get by key=" + 3 + ":");
        System.out.println(cache.get(cacheKeys.get(3)));
        System.out.println("===");

        // OBJECTIVE: expire the cache item most efficiently!

        // Wait for some items to be expired
        long sleepTime = random.nextLong(20);
        System.out.printf("Sleep %d seconds...%n", sleepTime);
        Thread.sleep(sleepTime * 1000);
        System.out.println("===");

        // Bad solution -> forEach -> remove -> O(n)

        // Better solution: using Comparator -> sort TreeMap -> remove in sequence from the root of tree -> O(log n)
        // In real implementation: for in a sorted collection!
        Set<CacheKey<Integer>> keySet = new HashSet<>(cache.keySet());  // Avoid ConcurrentModificationException
        for (CacheKey<Integer> key : keySet) {
            if (key.expires < Instant.now().getEpochSecond()) {
                cache.remove(key);
            } else {
                // important: have to break, or else it's same as the bad solution!
                break;
            }
        }
        System.out.println("After being expired:");
        System.out.println("Cache size: " + cache.size());
        System.out.println(cache);
    }

    static class CacheKey<ID> implements Comparable<CacheKey<ID>> {
        ID id;
        long expires;

        public CacheKey(ID id, long expires) {
            this.id = id;
            this.expires = expires;
        }

        @Override
        public int compareTo(CacheKey<ID> o) {
            return (int) (expires - o.expires);
        }

        @Override
        public String toString() {
            return "CacheKey{" +
                    "id=" + id +
                    ", expires=" + expires +
                    '}';
        }
    }

    static class CacheItem {
        private final Random random = new Random();

        String state;
        long expires;

        public CacheItem(String state, long expires) {
            this.state = state;
            this.expires = expires;
        }

        @Override
        public String toString() {
            return "CacheItem{" +
                    "state='" + state + '\'' +
                    '}';
        }
    }

    static class Product {
        private final int id;
        private final String name;

        public Product(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "Product{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
