package bank;

public abstract class BankAccount {

    private final AccountNumber number;
    private final String owner;
    private double balance;

    protected BankAccount(AccountNumber number, String owner, double initialBalance) {
        if (number == null) {
            throw new IllegalArgumentException("Номер счёта не может быть null");
        }
        if (owner == null || owner.isBlank()) {
            throw new IllegalArgumentException("Владелец счёта не может быть пустым");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Начальный баланс не может быть отрицательным");
        }

        this.number = number;
        this.owner = owner;
        this.balance = initialBalance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        increaseBalance(amount);
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount > getAvailableAmount()) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        decreaseBalance(amount);
    }

    protected abstract double getAvailableAmount();

    public double getBalance() {
        return balance;
    }

    public AccountNumber getNumber() {
        return number;
    }

    public String getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{\n" +
                " number='" + number.value() + "',\n" +
                " owner='" + owner + "',\n" +
                " balance=" + balance + "\n" +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        // instanceof, а не getClass: счета разных типов с одним номером равны
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BankAccount)) {
            return false;
        }
        BankAccount other = (BankAccount) obj;
        return number.equals(other.number);
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }

    protected void increaseBalance(double amount) {
        balance += amount;
    }

    protected void decreaseBalance(double amount) {
        balance -= amount;
    }
}
