package solid.factory.service;

import solid.factory.RepositoryFactory;
import solid.factory.ServiceFactory;
import solid.factory.repository.MockRepositoryFactory;
import solid.service.NotificationMockService;
import solid.service.NotificationService;
import solid.service.OrderService;
import solid.service.impl.OrderServiceImpl;

public class MockServiceFactory implements ServiceFactory {
    private final RepositoryFactory repositoryFactory = new MockRepositoryFactory();

    @Override
    public OrderService getOrderService() {
        return new OrderServiceImpl(repositoryFactory.getOrderRepository(), getNotificationService());
    }

    @Override
    public NotificationService getNotificationService() {
        return new NotificationMockService();
    }
}
