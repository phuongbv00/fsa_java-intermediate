package solid.service.impl;

import solid.repository.OrderRepository;
import solid.service.NotificationService;
import solid.service.OrderService;

public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    public OrderServiceImpl(OrderRepository orderRepository, NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void processOrder(String orderId) {
        int status = orderRepository.getStatus(orderId);
        if (status == 0) {
            orderRepository.updateStatus(orderId, 1);
            notificationService.send("Updated order %s status to %d".formatted(orderId, 1));
        }
    }
}


