package solid.repository.impl;

import solid.repository.OrderRepository;

public class OrderJpaRepository implements OrderRepository {
    @Override
    public int getStatus(String orderId) {
        return 0;
    }

    @Override
    public void updateStatus(String orderId, int status) {

    }
}
