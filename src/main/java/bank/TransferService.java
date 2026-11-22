package bank;

public class TransferService {

    private static final double TRANSFER_LIMIT = 50_000;

    private final CommissionPolicy commissionPolicy;
    private final NotificationService notificationService;

    public TransferService(
            CommissionPolicy commissionPolicy,
            NotificationService notificationService) {
        if (commissionPolicy == null) {
            throw new IllegalArgumentException("Политика комиссии не может быть null");
        }
        if (notificationService == null) {
            throw new IllegalArgumentException("Сервис уведомлений не может быть null");
        }
        this.commissionPolicy = commissionPolicy;
        this.notificationService = notificationService;
    }

    public void transfer(BankAccount from, BankAccount to, double amount) {
        // всё проверяем до любых изменений балансов
        if (from == null || to == null) {
            throw new IllegalArgumentException("Accounts must not be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (from == to) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        if (amount > TRANSFER_LIMIT) {
            throw new TransferLimitExceededException("Transfer limit exceeded");
        }

        double commission = commissionPolicy.calculate(amount);
        from.withdraw(amount + commission);

        to.deposit(amount);
        notificationService.notify("Transfer " + amount + " completed");
    }
}
