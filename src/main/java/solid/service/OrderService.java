package solid.service;

public class OrderService {
    public void processOrder(String orderId) {
        System.out.println("Processing order: " + orderId);
        updateOrder(orderId);
        sendEmail(orderId);
    }

    public void updateOrder(String orderId) {
        System.out.println("Updating order: " + orderId);
    }

    public void sendEmail(String orderId) {
        System.out.println("Sending email for order: " + orderId);
    }
}


