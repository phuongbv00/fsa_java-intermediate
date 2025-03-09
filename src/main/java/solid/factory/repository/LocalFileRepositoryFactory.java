package solid.factory.repository;

import solid.factory.RepositoryFactory;
import solid.repository.OrderRepository;
import solid.repository.impl.OrderLocalFileRepository;

public class LocalFileRepositoryFactory implements RepositoryFactory {
    @Override
    public OrderRepository getOrderRepository() {
        return new OrderLocalFileRepository();
    }
}
