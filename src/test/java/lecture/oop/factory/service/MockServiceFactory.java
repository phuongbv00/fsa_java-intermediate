package lecture.oop.factory.service;

import lecture.oop.factory.RepositoryFactory;
import lecture.oop.factory.ServiceFactory;
import lecture.oop.factory.repository.MockRepositoryFactory;
import lecture.oop.service.NotificationMockService;
import lecture.oop.service.NotificationService;
import lecture.oop.service.OrderService;
import lecture.oop.service.impl.OrderServiceImpl;

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
