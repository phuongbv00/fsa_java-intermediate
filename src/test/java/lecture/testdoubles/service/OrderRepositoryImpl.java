package lecture.testdoubles.service;

import java.util.concurrent.ConcurrentHashMap;

public class OrderRepositoryImpl implements OrderRepository {
    private static final ConcurrentHashMap<String, Integer> statusMap = new ConcurrentHashMap<>();

    @Override
    public Integer getStatus(String orderId) {
        return statusMap.get(orderId);
    }

    @Override
    public void updateStatus(String orderId, int status) {
        statusMap.compute(orderId, (k, present) -> {
            if (present == null) {
                return status;
            } else {
                return status;
            }
        });
    }
}
