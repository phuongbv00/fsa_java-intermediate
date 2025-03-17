package challenge.quiz02.repository;

import challenge.quiz02.model.Product;

import java.util.Map;

public interface ProductRepository extends Repository<Integer, Product> {
    Map<Boolean, Integer> partitionByPrice(Double price);

    Map<String, Product> groupByName();
}
