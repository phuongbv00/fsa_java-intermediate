package lecture.testdoubles.service;

public class NotificationServiceImpl implements NotificationService {
    @Override
    public void send(String content) {
        System.out.println(content);
    }
}
