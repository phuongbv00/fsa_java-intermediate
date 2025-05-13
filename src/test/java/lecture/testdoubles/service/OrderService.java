package lecture.testdoubles.service;

public interface OrderService {
    void processOrder(String orderId);

    Integer getOrderStatus(String orderId, boolean readOnly);
}
