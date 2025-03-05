package solid;

import solid.service.OrderService;

public class Test {
    public static void main(String[] args) {
        processOrder();
    }

    private static void processOrder() {
        OrderService orderService = new OrderService();
        orderService.processOrder("1");
    }
}
