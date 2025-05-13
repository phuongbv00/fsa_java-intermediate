package lecture.testdoubles.service;

public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    public OrderServiceImpl(OrderRepository orderRepository, NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void processOrder(String orderId) {
        System.out.println(orderId);
        Integer status = orderRepository.getStatus(orderId);
        if (status == null) {
            orderRepository.updateStatus(orderId, 1);
        } else {
            orderRepository.updateStatus(orderId, status + 10);
        }
        notificationService.send("Order %s is processed with status %d".formatted(orderId, orderRepository.getStatus(orderId)));
    }

    @Override
    public Integer getOrderStatus(String orderId, boolean readOnly) {
        if ("123".equals(orderId)) {
            return 123;
        }
        return orderRepository.getStatus(orderId);
    }
}
