package lecture.oop.factory.repository;

import lecture.oop.factory.RepositoryFactory;
import lecture.oop.repository.OrderMockRepository;
import lecture.oop.repository.OrderRepository;

public class MockRepositoryFactory implements RepositoryFactory {
    @Override
    public OrderRepository getOrderRepository() {
        return new OrderMockRepository();
    }
}
