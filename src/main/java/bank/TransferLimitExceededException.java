package bank;

/**
 * Перевод не может быть выполнен из-за ограничений счёта отправителя.
 */
public class TransferLimitExceededException extends RuntimeException {

    public TransferLimitExceededException(String message) {
        super(message);
    }
}
