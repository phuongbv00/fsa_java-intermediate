package solid;

import solid.factory.ServiceFactory;
import solid.factory.service.MockServiceFactory;
import solid.service.OrderService;

public class Test {
    public static void main(String[] args) {
        processOrder();
    }

    private static void processOrder() {
        ServiceFactory serviceFactory = new MockServiceFactory();
        OrderService orderService = serviceFactory.getOrderService();
        orderService.processOrder("1");
    }
}
