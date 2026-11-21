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
        SavingsAccount account = new SavingsAccount("S1", "Петров", 10_000, 1_000);

        // Act
        boolean withdrawn = account.withdraw(8_500);

        // Assert
        assertAll(
                () -> assertTrue(withdrawn),
                () -> assertEquals(1_500, account.getBalance(), 0.0001)
        );
    }

    @Test
    void cannotWithdrawBelowMinimumBalance() {
        // Arrange
        SavingsAccount account = new SavingsAccount("S1", "Петров", 10_000, 1_000);
        account.withdraw(8_500);

        // Act
        boolean withdrawn = account.withdraw(1_000);

        // Assert
        assertAll(
                () -> assertFalse(withdrawn),
                () -> assertEquals(1_500, account.getBalance(), 0.0001)
        );
    }

    @Test
    void failedWithdrawDoesNotChangeBalance() {
        // Arrange
        SavingsAccount account = new SavingsAccount("S1", "Петров", 10_000, 1_000);
        account.withdraw(8_500);

        // Act
        boolean withdrawn = account.withdraw(1_000);

        // Assert
        assertAll(
                () -> assertFalse(withdrawn),
                () -> assertEquals(1_500, account.getBalance(), 0.0001)
        );
    }

    @Test
    void canWithdrawExactlyToMinimumBalance() {
        // Arrange
        SavingsAccount account = new SavingsAccount("S1", "Петров", 10_000, 1_000);
        account.withdraw(8_500);

        // Act
        boolean withdrawn = account.withdraw(500);

        // Assert
        assertAll(
                () -> assertTrue(withdrawn),
                () -> assertEquals(1_000, account.getBalance(), 0.0001)
        );
    }

    @Test
    void cannotWithdrawZeroOrNegativeAmount() {
        // Arrange
        SavingsAccount account = new SavingsAccount("S1", "Петров", 10_000, 1_000);

        // Act
        boolean zeroWithdrawn = account.withdraw(0);
        boolean negativeWithdrawn = account.withdraw(-50);

        // Assert
        assertAll(
                () -> assertFalse(zeroWithdrawn),
                () -> assertFalse(negativeWithdrawn),
                () -> assertEquals(10_000, account.getBalance(), 0.0001)
        );
    }

    @Test
    void depositIncreasesBalance() {
        // Arrange
        SavingsAccount account = new SavingsAccount("S1", "Петров", 10_000, 1_000);

        // Act
        account.deposit(2_000);

        // Assert
        assertEquals(12_000, account.getBalance(), 0.0001);
    }

    @Test
    void minimumBalanceIsStoredInAccount() {
        // Arrange
        SavingsAccount account = new SavingsAccount("S1", "Петров", 10_000, 1_000);

        // Act / Assert
        assertEquals(1_000, account.getMinimumBalance(), 0.0001);
    }

    @Test
    void constructorRejectsNegativeMinimumBalance() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class,
                () -> new SavingsAccount("S1", "Петров", 10_000, -1));
    }
}
