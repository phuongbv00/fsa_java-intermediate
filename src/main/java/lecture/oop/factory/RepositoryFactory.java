package lecture.oop.factory;

import lecture.oop.repository.OrderRepository;

public interface RepositoryFactory {
    OrderRepository getOrderRepository();
}
