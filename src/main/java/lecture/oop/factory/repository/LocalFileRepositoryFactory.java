package lecture.oop.factory.repository;

import lecture.oop.factory.RepositoryFactory;
import lecture.oop.repository.OrderRepository;
import lecture.oop.repository.impl.OrderLocalFileRepository;

public class LocalFileRepositoryFactory implements RepositoryFactory {
    @Override
    public OrderRepository getOrderRepository() {
        return new OrderLocalFileRepository();
    }
}
