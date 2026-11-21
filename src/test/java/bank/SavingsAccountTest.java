package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SavingsAccountTest {

    @Test
    void withdrawKeepsBalanceAboveMinimum() {
        // Arrange
        SavingsAccount account = new SavingsAccount(new AccountNumber("4000000008"), "Петров", 10_000, 1_000);

        // Act
        account.withdraw(8_500);

        // Assert
        assertEquals(1_500, account.getBalance(), 0.0001);
    }

    @Test
    void cannotWithdrawBelowMinimumBalance() {
        // Arrange
        SavingsAccount account = new SavingsAccount(new AccountNumber("4000000008"), "Петров", 10_000, 1_000);
        account.withdraw(8_500);

        // Act / Assert
        assertAll(
                () -> assertThrows(InsufficientFundsException.class, () -> account.withdraw(1_000)),
                () -> assertEquals(1_500, account.getBalance(), 0.0001)
        );
    }

    @Test
    void failedWithdrawDoesNotChangeBalance() {
        // Arrange
        SavingsAccount account = new SavingsAccount(new AccountNumber("4000000008"), "Петров", 10_000, 1_000);
        account.withdraw(8_500);

        // Act / Assert
        assertAll(
                () -> assertThrows(InsufficientFundsException.class, () -> account.withdraw(1_000)),
                () -> assertEquals(1_500, account.getBalance(), 0.0001)
        );
    }

    @Test
    void canWithdrawExactlyToMinimumBalance() {
        // Arrange
        SavingsAccount account = new SavingsAccount(new AccountNumber("4000000008"), "Петров", 10_000, 1_000);
        account.withdraw(8_500);

        // Act
        account.withdraw(500);

        // Assert
        assertEquals(1_000, account.getBalance(), 0.0001);
    }

    @Test
    void cannotWithdrawZeroOrNegativeAmount() {
        // Arrange
        SavingsAccount account = new SavingsAccount(new AccountNumber("4000000008"), "Петров", 10_000, 1_000);

        // Act / Assert
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> account.withdraw(0)),
                () -> assertThrows(IllegalArgumentException.class, () -> account.withdraw(-50)),
                () -> assertEquals(10_000, account.getBalance(), 0.0001)
        );
    }

    @Test
    void depositIncreasesBalance() {
        // Arrange
        SavingsAccount account = new SavingsAccount(new AccountNumber("4000000008"), "Петров", 10_000, 1_000);

        // Act
        account.deposit(2_000);

        // Assert
        assertEquals(12_000, account.getBalance(), 0.0001);
    }

    @Test
    void minimumBalanceIsStoredInAccount() {
        // Arrange
        SavingsAccount account = new SavingsAccount(new AccountNumber("4000000008"), "Петров", 10_000, 1_000);

        // Act / Assert
        assertEquals(1_000, account.getMinimumBalance(), 0.0001);
    }

    @Test
    void constructorRejectsNegativeMinimumBalance() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class,
                () -> new SavingsAccount(new AccountNumber("4000000008"), "Петров", 10_000, -1));
    }
}
