package lecture.oop.factory;

import lecture.oop.service.NotificationService;
import lecture.oop.service.OrderService;

public interface ServiceFactory {
    OrderService getOrderService();

    NotificationService getNotificationService();
}
