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
        DebitAccount account = new DebitAccount("D1", "Иванов", 10_000);

        // Assert
        assertEquals(10_000, account.getBalance(), 0.0001);
    }

    @Test
    void depositIncreasesBalance() {
        // Arrange
        DebitAccount account = new DebitAccount("D1", "Иванов", 10_000);

        // Act
        account.deposit(2_000);

        // Assert
        assertEquals(12_000, account.getBalance(), 0.0001);
    }

    @Test
    void depositOfZeroDoesNotChangeBalance() {
        // Arrange
        DebitAccount account = new DebitAccount("D1", "Иванов", 10_000);

        // Act
        account.deposit(0);

        // Assert
        assertEquals(10_000, account.getBalance(), 0.0001);
    }

    @Test
    void depositOfNegativeAmountDoesNotChangeBalance() {
        // Arrange
        DebitAccount account = new DebitAccount("D1", "Иванов", 10_000);

        // Act
        account.deposit(-500);

        // Assert
        assertEquals(10_000, account.getBalance(), 0.0001);
    }

    @Test
    void withdrawDecreasesBalanceWhenEnoughMoney() {
        // Arrange
        DebitAccount account = new DebitAccount("D1", "Иванов", 10_000);

        // Act
        boolean withdrawn = account.withdraw(8_000);

        // Assert
        assertAll(
                () -> assertTrue(withdrawn),
                () -> assertEquals(2_000, account.getBalance(), 0.0001)
        );
    }

    @Test
    void cannotWithdrawMoreThanBalance() {
        // Arrange
        DebitAccount account = new DebitAccount("D1", "Иванов", 10_000);
        account.withdraw(8_000);

        // Act
        boolean withdrawn = account.withdraw(3_000);

        // Assert
        assertAll(
                () -> assertFalse(withdrawn),
                () -> assertEquals(2_000, account.getBalance(), 0.0001)
        );
    }

    @Test
    void canWithdrawEntireBalance() {
        // Arrange
        DebitAccount account = new DebitAccount("D1", "Иванов", 10_000);

        // Act
        boolean withdrawn = account.withdraw(10_000);

        // Assert
        assertAll(
                () -> assertTrue(withdrawn),
                () -> assertEquals(0, account.getBalance(), 0.0001)
        );
    }

    @Test
    void zeroWithdrawIsForbidden() {
        // Arrange
        DebitAccount account = new DebitAccount("D1", "Иванов", 10_000);

        // Act
        boolean withdrawn = account.withdraw(0);

        // Assert
        assertAll(
                () -> assertFalse(withdrawn),
                () -> assertEquals(10_000, account.getBalance(), 0.0001)
        );
    }

    @Test
    void negativeWithdrawIsForbidden() {
        // Arrange
        DebitAccount account = new DebitAccount("D1", "Иванов", 10_000);

        // Act
        boolean withdrawn = account.withdraw(-100);

        // Assert
        assertAll(
                () -> assertFalse(withdrawn),
                () -> assertEquals(10_000, account.getBalance(), 0.0001)
        );
    }

    @Test
    void constructorRejectsNegativeInitialBalance() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class,
                () -> new DebitAccount("D1", "Иванов", -1));
    }

    @Test
    void numberAndOwnerRemainUnchanged() {
        // Arrange
        DebitAccount account = new DebitAccount("D1", "Иванов", 10_000);

        // Act
        account.deposit(1_000);
        account.withdraw(500);

        // Assert
        assertAll(
                () -> assertEquals("D1", account.getNumber()),
                () -> assertEquals("Иванов", account.getOwner())
        );
    }
}
