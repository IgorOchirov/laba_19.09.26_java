package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DebitAccountTest {

    @Test
    void constructorKeepsInitialBalance() {
        // Arrange / Act
        DebitAccount account = new DebitAccount(new AccountNumber("4000000005"), "Иванов", 10_000);

        // Assert
        assertEquals(10_000, account.getBalance(), 0.0001);
    }

    @Test
    void depositIncreasesBalance() {
        // Arrange
        DebitAccount account = new DebitAccount(new AccountNumber("4000000005"), "Иванов", 10_000);

        // Act
        account.deposit(2_000);

        // Assert
        assertEquals(12_000, account.getBalance(), 0.0001);
    }

    @Test
    void zeroDepositThrowsException() {
        // Arrange
        DebitAccount account = new DebitAccount(new AccountNumber("4000000005"), "Иванов", 10_000);

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> account.deposit(0));
    }

    @Test
    void negativeDepositThrowsException() {
        // Arrange
        DebitAccount account = new DebitAccount(new AccountNumber("4000000005"), "Иванов", 10_000);

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> account.deposit(-100));
    }

    @Test
    void withdrawDecreasesBalanceWhenEnoughMoney() {
        // Arrange
        DebitAccount account = new DebitAccount(new AccountNumber("4000000005"), "Иванов", 10_000);

        // Act
        account.withdraw(8_000);

        // Assert
        assertEquals(2_000, account.getBalance(), 0.0001);
    }

    @Test
    void cannotWithdrawMoreThanBalance() {
        // Arrange
        DebitAccount account = new DebitAccount(new AccountNumber("4000000005"), "Иванов", 10_000);
        account.withdraw(8_000);

        // Act / Assert
        assertAll(
                () -> assertThrows(InsufficientFundsException.class, () -> account.withdraw(3_000)),
                () -> assertEquals(2_000, account.getBalance(), 0.0001)
        );
    }

    @Test
    void canWithdrawEntireBalance() {
        // Arrange
        DebitAccount account = new DebitAccount(new AccountNumber("4000000005"), "Иванов", 10_000);

        // Act
        account.withdraw(10_000);

        // Assert
        assertEquals(0, account.getBalance(), 0.0001);
    }

    @Test
    void zeroWithdrawIsForbidden() {
        // Arrange
        DebitAccount account = new DebitAccount(new AccountNumber("4000000005"), "Иванов", 10_000);

        // Act / Assert
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> account.withdraw(0)),
                () -> assertEquals(10_000, account.getBalance(), 0.0001)
        );
    }

    @Test
    void negativeWithdrawIsForbidden() {
        // Arrange
        DebitAccount account = new DebitAccount(new AccountNumber("4000000005"), "Иванов", 10_000);

        // Act / Assert
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> account.withdraw(-100)),
                () -> assertEquals(10_000, account.getBalance(), 0.0001)
        );
    }

    @Test
    void constructorRejectsNegativeInitialBalance() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class,
                () -> new DebitAccount(new AccountNumber("4000000005"), "Иванов", -1));
    }

    @Test
    void numberAndOwnerRemainUnchanged() {
        // Arrange
        DebitAccount account = new DebitAccount(new AccountNumber("4000000005"), "Иванов", 10_000);

        // Act
        account.deposit(1_000);
        account.withdraw(500);

        // Assert
        assertAll(
                () -> assertEquals(new AccountNumber("4000000005"), account.getNumber()),
                () -> assertEquals("Иванов", account.getOwner())
        );
    }
}
