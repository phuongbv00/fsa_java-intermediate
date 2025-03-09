package solid.factory.repository;

import solid.factory.RepositoryFactory;
import solid.repository.OrderRepository;
import solid.repository.impl.OrderJpaRepository;

public class DatabaseRepositoryFactory implements RepositoryFactory {
    @Override
    public OrderRepository getOrderRepository() {
        return OrderJpaRepository.getInstance();
    }
}
