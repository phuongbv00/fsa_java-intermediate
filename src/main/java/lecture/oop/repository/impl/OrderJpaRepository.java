package lecture.oop.repository.impl;

import lecture.oop.repository.OrderRepository;

public class OrderJpaRepository implements OrderRepository {
    private static OrderRepository instance;

    private OrderJpaRepository() {
    }

    // Thread-safe + Lazy Loading
    public static OrderRepository getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (OrderJpaRepository.class) {
            if (instance == null) {
                instance = new OrderJpaRepository();
            }
            return instance;
        }
    }

    @Override
    public int getStatus(String orderId) {
        return 0;
    }

    @Override
    public void updateStatus(String orderId, int status) {

    }
}
