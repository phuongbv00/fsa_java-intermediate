package solid;

import solid.factory.ServiceFactory;
import solid.factory.service.AppServiceFactory;
import solid.service.OrderService;

public class Main {
    public static void main(String[] args) {
        ServiceFactory serviceFactory = new AppServiceFactory();
        OrderService orderService = serviceFactory.getOrderService();
        orderService.processOrder("1");
    }
}
