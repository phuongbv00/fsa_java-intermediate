package lecture.oop;

import lecture.oop.factory.ServiceFactory;
import lecture.oop.factory.service.MockServiceFactory;
import lecture.oop.service.OrderService;
import org.junit.Test;

public class OOPTest {
    @Test
    public void processOrder() {
        ServiceFactory serviceFactory = new MockServiceFactory();
        OrderService orderService = serviceFactory.getOrderService();
        orderService.processOrder("1");
    }
}
