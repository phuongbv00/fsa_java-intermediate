package solid.service;

public class OrderService {
    public void processOrder(String orderId) {
        System.out.println("Processing order: " + orderId);
        int status = getOrderStatus(orderId);
        if (status == 0) {
            updateOrderStatus(orderId, 1);
            sendEmail(orderId);
        }
    }

    private int getOrderStatus(String orderId) {
        return 0;
    }

    public void updateOrderStatus(String orderId, int status) {
        System.out.println("Updating order " + orderId + " status: " + status);
    }

    public void sendEmail(String orderId) {
        System.out.println("Sending email for order: " + orderId);
    }
}


