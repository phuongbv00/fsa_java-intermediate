package solid.factory;

import solid.repository.OrderRepository;

public interface RepositoryFactory {
    OrderRepository getOrderRepository();
}
