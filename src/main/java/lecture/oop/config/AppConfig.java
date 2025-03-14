package lecture.oop.config;

public class AppConfig {
    public static final NotificationMode NOTIFICATION_MODE = NotificationMode.SMS;
    public static final PersistenceMode PERSISTENCE_MODE = PersistenceMode.DATABASE;

    public enum NotificationMode {
        EMAIL, SMS
    }

    public enum PersistenceMode {
        DATABASE, LOCAL_FILE
    }
}
