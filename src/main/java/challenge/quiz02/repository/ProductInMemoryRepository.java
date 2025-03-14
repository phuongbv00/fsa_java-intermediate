package challenge.quiz02.repository;

import challenge.quiz02.model.Product;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ProductInMemoryRepository implements Repository<Integer, Product> {
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
}
