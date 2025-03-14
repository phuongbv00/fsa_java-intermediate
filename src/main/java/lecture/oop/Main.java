package lecture.oop;

import lecture.oop.factory.ServiceFactory;
import lecture.oop.factory.service.AppServiceFactory;
import lecture.oop.service.OrderService;

public class Main {
    public static void main(String[] args) {
        ServiceFactory serviceFactory = new AppServiceFactory();
        OrderService orderService = serviceFactory.getOrderService();
        orderService.processOrder("1");
    }
}
