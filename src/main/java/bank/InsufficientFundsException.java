package bank;

/**
 * На счёте не хватает денег для операции.
 */
public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String message) {
        super(message);
    }
}
