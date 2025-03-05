package solid;

import solid.repository.OrderRepository;
import solid.repository.impl.OrderJpaRepository;
import solid.service.NotificationService;
import solid.service.OrderService;
import solid.service.impl.NotificationSmsService;
import solid.service.impl.OrderServiceImpl;

public class Main {
    public static void main(String[] args) {
        OrderRepository orderRepository = new OrderJpaRepository();
        NotificationService notificationService = new NotificationSmsService();
        OrderService orderService = new OrderServiceImpl(orderRepository, notificationService);
        orderService.processOrder("1");
    }
}
