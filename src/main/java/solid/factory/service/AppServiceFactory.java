package solid.factory.service;

import solid.config.AppConfig;
import solid.factory.RepositoryFactory;
import solid.factory.ServiceFactory;
import solid.factory.repository.DatabaseRepositoryFactory;
import solid.factory.repository.LocalFileRepositoryFactory;
import solid.repository.OrderRepository;
import solid.service.NotificationService;
import solid.service.OrderService;
import solid.service.impl.NotificationMailService;
import solid.service.impl.NotificationSmsService;
import solid.service.impl.OrderServiceImpl;

public class AppServiceFactory implements ServiceFactory {
    private final RepositoryFactory repositoryFactory = switch (AppConfig.PERSISTENCE_MODE) {
        case DATABASE -> new DatabaseRepositoryFactory();
        case LOCAL_FILE -> new LocalFileRepositoryFactory();
    };

    @Override
    public OrderService getOrderService() {
        OrderRepository orderRepository = repositoryFactory.getOrderRepository();
        NotificationService notificationService = getNotificationService();
        return new OrderServiceImpl(orderRepository, notificationService);
    }

    @Override
    public NotificationService getNotificationService() {
        return switch (AppConfig.NOTIFICATION_MODE) {
            case EMAIL -> new NotificationMailService();
            case SMS -> new NotificationSmsService();
        };
    }
}
