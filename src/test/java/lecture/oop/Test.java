package lecture.oop;

import lecture.oop.factory.ServiceFactory;
import lecture.oop.factory.service.MockServiceFactory;
import lecture.oop.service.OrderService;

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
