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
        BankAccount debit = new DebitAccount(new AccountNumber("0010000000"), "Ivan", 10_000);
        BankAccount savings = new SavingsAccount(new AccountNumber("0010000000"), "Ivan", 5_000, 1_000);
        BankAccount credit = new CreditAccount(new AccountNumber("0010000000"), "Ivan", 1_000, 5_000);

        // Act / Assert
        assertAll(
                () -> assertEquals(debit, savings),
                () -> assertEquals(savings, credit),
                () -> assertEquals(credit, debit),
                () -> assertEquals(debit, new DebitAccount(new AccountNumber("0010000000"), "Another owner", 0))
        );
    }

    @Test
    void accountsWithDifferentNumbersAreNotEqual() {
        // Arrange
        BankAccount first = new DebitAccount(new AccountNumber("0010000000"), "Ivan", 10_000);
        BankAccount second = new SavingsAccount(new AccountNumber("0020000000"), "Ivan", 10_000, 1_000);

        // Act / Assert
        assertAll(
                () -> assertNotEquals(first, second),
                () -> assertNotEquals(second, first)
        );
    }

    @Test
    void accountEqualsItself() {
        // Arrange
        BankAccount account = new DebitAccount(new AccountNumber("0010000000"), "Ivan", 10_000);

        // Act / Assert
        assertTrue(account.equals(account));
    }

    @Test
    void accountDoesNotEqualNull() {
        // Arrange
        BankAccount account = new DebitAccount(new AccountNumber("0010000000"), "Ivan", 10_000);

        // Act / Assert
        assertFalse(account.equals(null));
    }

    @Test
    void equalAccountsHaveSameHashCode() {
        // Arrange
        BankAccount debit = new DebitAccount(new AccountNumber("0010000000"), "Ivan", 10_000);
        BankAccount credit = new CreditAccount(new AccountNumber("0010000000"), "Another owner", 0, 5_000);

        // Act / Assert
        assertAll(
                () -> assertEquals(debit, credit),
                () -> assertEquals(debit.hashCode(), credit.hashCode())
        );
    }

    @Test
    void accountIdIsItsNumber() {
        // Arrange
        AccountNumber number = new AccountNumber("0010000000");
        BankAccount account = new DebitAccount(number, "Ivan", 10_000);

        // Act / Assert
        assertEquals(number, account.getId());
    }

    @Test
    void toStringShowsConcreteClassAndState() {
        // Arrange
        BankAccount debit = new DebitAccount(new AccountNumber("0010000000"), "Ivan", 10_000);
        String expected = "DebitAccount{\n" +
                " number='0010000000',\n" +
                " owner='Ivan',\n" +
                " balance=10000.0\n" +
                "}";

        // Act / Assert
        assertAll(
                () -> assertEquals(expected, debit.toString()),
                () -> assertTrue(new SavingsAccount(new AccountNumber("0020000000"), "Ivan", 0, 100)
                        .toString().startsWith("SavingsAccount{")),
                () -> assertTrue(new CreditAccount(new AccountNumber("0030000000"), "Ivan", 0, 100)
                        .toString().startsWith("CreditAccount{"))
        );
    }
}
