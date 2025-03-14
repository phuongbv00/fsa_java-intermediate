package lecture.oop.factory.repository;

import lecture.oop.factory.RepositoryFactory;
import lecture.oop.repository.OrderRepository;
import lecture.oop.repository.impl.OrderJpaRepository;

public class DatabaseRepositoryFactory implements RepositoryFactory {
    @Override
    public OrderRepository getOrderRepository() {
        return OrderJpaRepository.getInstance();
    }
}
