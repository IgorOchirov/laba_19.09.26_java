package bank;

/**
 * Результат одной операции по счёту.
 */
public record Transaction(
        TransactionType type,
        AccountNumber account,
        double amount,
        TransactionStatus status) {
}
