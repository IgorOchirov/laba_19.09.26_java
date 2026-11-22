package bank;

/**
 * Демонстрация обработки ошибок на стороне вызывающего кода.
 */
public class TransferDemo {

    public static void main(String[] args) {
        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );
        BankAccount from = new DebitAccount(new AccountNumber("1234567890"), "Ivan", 1_000);
        BankAccount to = new DebitAccount(new AccountNumber("0987654321"), "Petr", 2_000);

        try {
            service.transfer(from, to, 500);
            System.out.println("Transfer completed");
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }

        try {
            service.transfer(from, to, 5_000);
            System.out.println("Transfer completed");
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }

        try {
            service.transfer(from, to, 60_000);
            System.out.println("Transfer completed");
        } catch (TransferLimitExceededException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }
}
