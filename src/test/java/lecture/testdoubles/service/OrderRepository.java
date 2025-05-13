package lecture.testdoubles.service;

public interface OrderRepository {
    Integer getStatus(String orderId);

    void updateStatus(String orderId, int status);
}
