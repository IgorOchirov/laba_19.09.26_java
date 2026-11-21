package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionTest {

    @Test
    void transactionStoresItsComponents() {
        // Arrange
        AccountNumber number = new AccountNumber("1234567890");

        // Act
        Transaction transaction = new Transaction(
                TransactionType.DEPOSIT,
                number,
                5_000,
                TransactionStatus.SUCCESS
        );

        // Assert
        assertAll(
                () -> assertEquals(TransactionType.DEPOSIT, transaction.type()),
                () -> assertEquals(number, transaction.account()),
                () -> assertEquals(5_000, transaction.amount(), 0.0001),
                () -> assertEquals(TransactionStatus.SUCCESS, transaction.status())
        );
    }

    @Test
    void transactionPrintsReadableState() {
        // Arrange
        Transaction transaction = new Transaction(
                TransactionType.TRANSFER,
                new AccountNumber("0987654321"),
                3_000,
                TransactionStatus.REJECTED
        );

        // Act
        String printed = transaction.toString();

        // Assert
        assertAll(
                () -> assertTrue(printed.contains("TRANSFER")),
                () -> assertTrue(printed.contains("0987654321")),
                () -> assertTrue(printed.contains("REJECTED"))
        );
    }
}
