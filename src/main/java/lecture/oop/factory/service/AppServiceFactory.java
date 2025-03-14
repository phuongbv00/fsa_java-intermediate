package lecture.oop.factory.service;

import lecture.oop.config.AppConfig;
import lecture.oop.factory.RepositoryFactory;
import lecture.oop.factory.ServiceFactory;
import lecture.oop.factory.repository.DatabaseRepositoryFactory;
import lecture.oop.factory.repository.LocalFileRepositoryFactory;
import lecture.oop.repository.OrderRepository;
import lecture.oop.service.NotificationService;
import lecture.oop.service.OrderService;
import lecture.oop.service.impl.NotificationMailService;
import lecture.oop.service.impl.NotificationSmsService;
import lecture.oop.service.impl.OrderServiceImpl;

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
