package challenge.quiz02.repository;

import challenge.quiz02.model.Product;

import java.util.*;
import java.util.function.Predicate;

public class ProductInMemoryRepository implements ProductRepository {
    @Override
    public Optional<Product> findById(Integer integer) {
        // TODO
        return Optional.empty();
    }

    @Override
    public Collection<Product> findAll() {
        // TODO
        return List.of();
    }

    @Override
    public Collection<Product> findAll(Predicate<Product> filter, Comparator<Product> comparator) {
        // TODO
        return List.of();
    }

    @Override
    public Product save(Product product) {
        // TODO
        return null;
    }

    @Override
    public Product update(Product product) {
        // TODO
        return null;
    }

    @Override
    public void delete(Integer integer) {
        // TODO
    }

    @Override
    public int countBy(Predicate<Product> filter) {
        // TODO
        return 0;
    }

    @Override
    public Map<Boolean, Integer> partitionByPrice(Double price) {
        // TODO
        return Map.of();
    }

    @Override
    public Map<String, Product> groupByName() {
        // TODO
        return Map.of();
    }
}
