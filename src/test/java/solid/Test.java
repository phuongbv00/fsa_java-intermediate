package solid;

import solid.repository.OrderMockRepository;
import solid.repository.OrderRepository;
import solid.service.NotificationMockService;
import solid.service.NotificationService;
import solid.service.OrderService;
import solid.service.impl.OrderServiceImpl;

public class Test {
    public static void main(String[] args) {
        processOrder();
    }

    private static void processOrder() {
        OrderRepository orderRepository = new OrderMockRepository();
        NotificationService notificationService = new NotificationMockService();
        OrderService orderService = new OrderServiceImpl(orderRepository, notificationService);
        orderService.processOrder("1");
    }
}
