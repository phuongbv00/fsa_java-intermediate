package solid.repository;

public interface OrderRepository {
    int getStatus(String orderId);

    void updateStatus(String orderId, int status);
}
