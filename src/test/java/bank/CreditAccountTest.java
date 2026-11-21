package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreditAccountTest {

    @Test
    void withdrawCanMakeBalanceNegativeDownToCreditLimit() {
        // Arrange
        CreditAccount account = new CreditAccount(new AccountNumber("4000000009"), "Сидоров", 1_000, 5_000);

        // Act
        account.withdraw(4_000);

        // Assert
        assertEquals(-3_000, account.getBalance(), 0.0001);
    }

    @Test
    void cannotWithdrawBeyondCreditLimit() {
        // Arrange
        CreditAccount account = new CreditAccount(new AccountNumber("4000000009"), "Сидоров", 1_000, 5_000);
        account.withdraw(4_000);

        // Act / Assert
        assertAll(
                () -> assertThrows(InsufficientFundsException.class, () -> account.withdraw(3_000)),
                () -> assertEquals(-3_000, account.getBalance(), 0.0001)
        );
    }

    @Test
    void canUseEntireCreditLimit() {
        // Arrange
        CreditAccount account = new CreditAccount(new AccountNumber("4000000009"), "Сидоров", 1_000, 5_000);

        // Act
        account.withdraw(6_000);

        // Assert
        assertEquals(-5_000, account.getBalance(), 0.0001);
    }

    @Test
    void cannotWithdrawZeroOrNegativeAmount() {
        // Arrange
        CreditAccount account = new CreditAccount(new AccountNumber("4000000009"), "Сидоров", 1_000, 5_000);

        // Act / Assert
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> account.withdraw(0)),
                () -> assertThrows(IllegalArgumentException.class, () -> account.withdraw(-100)),
                () -> assertEquals(1_000, account.getBalance(), 0.0001)
        );
    }

    @Test
    void failedWithdrawDoesNotChangeBalance() {
        // Arrange
        CreditAccount account = new CreditAccount(new AccountNumber("4000000009"), "Сидоров", 1_000, 5_000);
        account.withdraw(4_000);

        // Act / Assert
        assertAll(
                () -> assertThrows(InsufficientFundsException.class, () -> account.withdraw(3_000)),
                () -> assertEquals(-3_000, account.getBalance(), 0.0001)
        );
    }

    @Test
    void depositRepaysCredit() {
        // Arrange
        CreditAccount account = new CreditAccount(new AccountNumber("4000000009"), "Сидоров", 1_000, 5_000);
        account.withdraw(4_000);

        // Act
        account.deposit(1_500);

        // Assert
        assertEquals(-1_500, account.getBalance(), 0.0001);
    }

    @Test
    void creditLimitIsStoredInAccount() {
        // Arrange
        CreditAccount account = new CreditAccount(new AccountNumber("4000000009"), "Сидоров", 1_000, 5_000);

        // Act / Assert
        assertEquals(5_000, account.getCreditLimit(), 0.0001);
    }

    @Test
    void constructorRejectsNegativeCreditLimit() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class,
                () -> new CreditAccount(new AccountNumber("4000000009"), "Сидоров", 0, -1));
    }
}
