package solid.factory;

import solid.service.NotificationService;
import solid.service.OrderService;

public interface ServiceFactory {
    OrderService getOrderService();

    NotificationService getNotificationService();
}
