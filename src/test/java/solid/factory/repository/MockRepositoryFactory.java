package solid.factory.repository;

import solid.factory.RepositoryFactory;
import solid.repository.OrderMockRepository;
import solid.repository.OrderRepository;

public class MockRepositoryFactory implements RepositoryFactory {
    @Override
    public OrderRepository getOrderRepository() {
        return new OrderMockRepository();
    }
}
