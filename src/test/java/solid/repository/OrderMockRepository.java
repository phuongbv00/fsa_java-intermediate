package solid.repository;

public class OrderMockRepository implements OrderRepository {

    @Override
    public int getStatus(String orderId) {
        return 0;
    }

    @Override
    public void updateStatus(String orderId, int status) {

    }
}
