package bank;

/**
 * Уведомления выводятся в консоль.
 */
public class ConsoleNotificationService implements NotificationService {

    @Override
    public void notify(String message) {
        System.out.println(message);
    }
}
