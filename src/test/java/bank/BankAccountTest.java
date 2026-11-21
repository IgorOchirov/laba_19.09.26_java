package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankAccountTest {

    @Test
    void accountsWithSameNumberAreEqual() {
        // Arrange
        BankAccount debit = new DebitAccount("001", "Ivan", 10_000);
        BankAccount savings = new SavingsAccount("001", "Ivan", 5_000, 1_000);
        BankAccount credit = new CreditAccount("001", "Ivan", 1_000, 5_000);

        // Act / Assert
        assertAll(
                () -> assertEquals(debit, savings),
                () -> assertEquals(savings, credit),
                () -> assertEquals(credit, debit),
                () -> assertEquals(debit, new DebitAccount("001", "Another owner", 0))
        );
    }

    @Test
    void accountsWithDifferentNumbersAreNotEqual() {
        // Arrange
        BankAccount first = new DebitAccount("001", "Ivan", 10_000);
        BankAccount second = new SavingsAccount("002", "Ivan", 10_000, 1_000);

        // Act / Assert
        assertAll(
                () -> assertNotEquals(first, second),
                () -> assertNotEquals(second, first)
        );
    }

    @Test
    void accountEqualsItself() {
        // Arrange
        BankAccount account = new DebitAccount("001", "Ivan", 10_000);

        // Act / Assert
        assertTrue(account.equals(account));
    }

    @Test
    void accountDoesNotEqualNull() {
        // Arrange
        BankAccount account = new DebitAccount("001", "Ivan", 10_000);

        // Act / Assert
        assertFalse(account.equals(null));
    }

    @Test
    void equalAccountsHaveSameHashCode() {
        // Arrange
        BankAccount debit = new DebitAccount("001", "Ivan", 10_000);
        BankAccount credit = new CreditAccount("001", "Another owner", 0, 5_000);

        // Act / Assert
        assertAll(
                () -> assertEquals(debit, credit),
                () -> assertEquals(debit.hashCode(), credit.hashCode())
        );
    }

    @Test
    void toStringShowsConcreteClassAndState() {
        // Arrange
        BankAccount debit = new DebitAccount("001", "Ivan", 10_000);
        String expected = "DebitAccount{\n" +
                " number='001',\n" +
                " owner='Ivan',\n" +
                " balance=10000.0\n" +
                "}";

        // Act / Assert
        assertAll(
                () -> assertEquals(expected, debit.toString()),
                () -> assertTrue(new SavingsAccount("002", "Ivan", 0, 100)
                        .toString().startsWith("SavingsAccount{")),
                () -> assertTrue(new CreditAccount("003", "Ivan", 0, 100)
                        .toString().startsWith("CreditAccount{"))
        );
    }
}
