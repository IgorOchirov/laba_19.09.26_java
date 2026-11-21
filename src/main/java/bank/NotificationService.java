package bank;

/**
 * Сервис уведомлений о событиях банковской системы.
 */
public interface NotificationService {

    /**
     * Отправляет уведомление.
     *
     * @param message текст уведомления
     */
    void notify(String message);
}
