package bank;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransferServiceTest {

    @Test
    void transferMovesMoneyBetweenAccounts() {
        // Arrange
        FakeNotificationService notifications = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount from = new DebitAccount(new AccountNumber("4000000001"), "Иванов", 10_000);
        BankAccount to = new DebitAccount(new AccountNumber("4000000002"), "Петров", 2_000);

        // Act
        service.transfer(from, to, 3_000);

        // Assert
        assertAll(
                () -> assertEquals(7_000, from.getBalance(), 0.0001),
                () -> assertEquals(5_000, to.getBalance(), 0.0001),
                () -> assertEquals(1, notifications.getNotificationCount()),
                () -> assertEquals("Transfer 3000.0 completed", notifications.getLastMessage())
        );
    }

    @Test
    void negativeTransferThrowsException() {
        // Arrange
        FakeNotificationService notifications = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount from = new DebitAccount(new AccountNumber("4000000001"), "Иванов", 10_000);
        BankAccount to = new DebitAccount(new AccountNumber("4000000002"), "Петров", 2_000);

        // Act / Assert
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> service.transfer(from, to, -100)),
                () -> assertEquals(10_000, from.getBalance(), 0.0001),
                () -> assertEquals(2_000, to.getBalance(), 0.0001),
                () -> assertEquals(0, notifications.getNotificationCount())
        );
    }

    @Test
    void zeroTransferThrowsException() {
        // Arrange
        FakeNotificationService notifications = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount from = new DebitAccount(new AccountNumber("4000000001"), "Иванов", 10_000);
        BankAccount to = new DebitAccount(new AccountNumber("4000000002"), "Петров", 2_000);

        // Act / Assert
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> service.transfer(from, to, 0)),
                () -> assertEquals(10_000, from.getBalance(), 0.0001),
                () -> assertEquals(2_000, to.getBalance(), 0.0001),
                () -> assertEquals(0, notifications.getNotificationCount())
        );
    }

    @Test
    void transferToSameAccountThrowsException() {
        // Arrange
        FakeNotificationService notifications = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount account = new DebitAccount(new AccountNumber("4000000001"), "Иванов", 10_000);

        // Act / Assert
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> service.transfer(account, account, 1_000)),
                () -> assertEquals(10_000, account.getBalance(), 0.0001),
                () -> assertEquals(0, notifications.getNotificationCount())
        );
    }

    @Test
    void transferWithoutEnoughMoneyThrowsException() {
        // Arrange
        FakeNotificationService notifications = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount from = new DebitAccount(new AccountNumber("4000000001"), "Иванов", 1_000);
        BankAccount to = new DebitAccount(new AccountNumber("4000000002"), "Петров", 2_000);

        // Act
        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
                () -> service.transfer(from, to, 5_000));

        // Assert
        assertAll(
                () -> assertEquals("Insufficient funds", exception.getMessage()),
                () -> assertEquals(1_000, from.getBalance(), 0.0001),
                () -> assertEquals(2_000, to.getBalance(), 0.0001),
                () -> assertEquals(0, notifications.getNotificationCount())
        );
    }

    @Test
    void transferOverLimitThrowsException() {
        // Arrange
        FakeNotificationService notifications = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount from = new DebitAccount(new AccountNumber("4000000001"), "Иванов", 100_000);
        BankAccount to = new DebitAccount(new AccountNumber("4000000002"), "Петров", 0);

        // Act
        TransferLimitExceededException exception = assertThrows(TransferLimitExceededException.class,
                () -> service.transfer(from, to, 50_001));

        // Assert
        assertAll(
                () -> assertEquals("Transfer limit exceeded", exception.getMessage()),
                () -> assertEquals(100_000, from.getBalance(), 0.0001),
                () -> assertEquals(0, to.getBalance(), 0.0001),
                () -> assertEquals(0, notifications.getNotificationCount())
        );
    }

    @Test
    void percentCommissionIsDeductedFromSenderOnly() {
        // Arrange
        FakeNotificationService notifications = new FakeNotificationService();
        TransferService service = new TransferService(new PercentCommission(1), notifications);
        BankAccount from = new DebitAccount(new AccountNumber("4000000001"), "Иванов", 10_100);
        BankAccount to = new DebitAccount(new AccountNumber("4000000002"), "Петров", 0);

        // Act
        service.transfer(from, to, 10_000);

        // Assert
        assertAll(
                () -> assertEquals(0, from.getBalance(), 0.0001),
                () -> assertEquals(10_000, to.getBalance(), 0.0001),
                () -> assertEquals("Transfer 10000.0 completed", notifications.getLastMessage())
        );
    }

    @Test
    void recipientReceivesExactlyTransferAmountWithoutCommission() {
        // Arrange
        FakeNotificationService notifications = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount from = new DebitAccount(new AccountNumber("4000000001"), "Иванов", 10_000);
        BankAccount to = new DebitAccount(new AccountNumber("4000000002"), "Петров", 500);

        // Act
        service.transfer(from, to, 3_000);

        // Assert
        assertEquals(3_500, to.getBalance(), 0.0001);
    }

    @Test
    void transferFailsWhenSenderCannotPayCommission() {
        // Arrange
        FakeNotificationService notifications = new FakeNotificationService();
        TransferService service = new TransferService(new PercentCommission(1), notifications);
        BankAccount from = new DebitAccount(new AccountNumber("4000000001"), "Иванов", 10_000);
        BankAccount to = new DebitAccount(new AccountNumber("4000000002"), "Петров", 2_000);

        // Act / Assert
        assertAll(
                () -> assertThrows(InsufficientFundsException.class,
                        () -> service.transfer(from, to, 10_000)),
                () -> assertEquals(10_000, from.getBalance(), 0.0001),
                () -> assertEquals(2_000, to.getBalance(), 0.0001),
                () -> assertEquals(0, notifications.getNotificationCount())
        );
    }

    @Test
    void failedTransferDoesNotChangeBalances() {
        // Arrange
        FakeNotificationService notifications = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount from = new DebitAccount(new AccountNumber("4000000001"), "Иванов", 1_000);
        BankAccount to = new DebitAccount(new AccountNumber("4000000002"), "Петров", 2_000);

        // Act
        assertThrows(InsufficientFundsException.class,
                () -> service.transfer(from, to, 5_000));

        // Assert
        assertAll(
                () -> assertEquals(1_000, from.getBalance(), 0.0001),
                () -> assertEquals(2_000, to.getBalance(), 0.0001),
                () -> assertEquals(0, notifications.getNotificationCount())
        );
    }

    @Test
    void transferFromDebitToDebitAccount() {
        // Arrange
        FakeNotificationService notifications = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount from = new DebitAccount(new AccountNumber("4000000005"), "Иванов", 10_000);
        BankAccount to = new DebitAccount(new AccountNumber("4000000006"), "Петров", 2_000);

        // Act
        service.transfer(from, to, 3_000);

        // Assert
        assertAll(
                () -> assertEquals(7_000, from.getBalance(), 0.0001),
                () -> assertEquals(5_000, to.getBalance(), 0.0001)
        );
    }

    @Test
    void transferFromDebitToSavingsAccount() {
        // Arrange
        FakeNotificationService notifications = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount from = new DebitAccount(new AccountNumber("4000000005"), "Иванов", 5_000);
        BankAccount to = new SavingsAccount(new AccountNumber("4000000007"), "Петров", 2_000, 500);

        // Act
        service.transfer(from, to, 3_000);

        // Assert
        assertAll(
                () -> assertEquals(2_000, from.getBalance(), 0.0001),
                () -> assertEquals(5_000, to.getBalance(), 0.0001)
        );
    }

    @Test
    void transferFromCreditToDebitAccount() {
        // Arrange
        FakeNotificationService notifications = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount from = new CreditAccount(new AccountNumber("4000000003"), "Сидоров", 1_000, 5_000);
        BankAccount to = new DebitAccount(new AccountNumber("4000000004"), "Петров", 2_000);

        // Act
        service.transfer(from, to, 4_000);

        // Assert
        assertAll(
                () -> assertEquals(-3_000, from.getBalance(), 0.0001),
                () -> assertEquals(6_000, to.getBalance(), 0.0001),
                () -> assertEquals("Transfer 4000.0 completed", notifications.getLastMessage())
        );
    }

    @Test
    void transferFromSavingsToDebitAccount() {
        // Arrange
        FakeNotificationService notifications = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount from = new SavingsAccount(new AccountNumber("4000000007"), "Петров", 5_000, 1_000);
        BankAccount to = new DebitAccount(new AccountNumber("4000000005"), "Иванов", 1_000);

        // Act
        service.transfer(from, to, 3_000);

        // Assert
        assertAll(
                () -> assertEquals(2_000, from.getBalance(), 0.0001),
                () -> assertEquals(4_000, to.getBalance(), 0.0001)
        );
    }

    @Test
    void consoleNotificationServicePrintsMessage() {
        // Arrange
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        NotificationService notifications = new ConsoleNotificationService();

        // Act
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            notifications.notify("Transfer 3000.0 completed");
        } finally {
            System.setOut(originalOut);
        }

        // Assert
        assertEquals("Transfer 3000.0 completed", output.toString(StandardCharsets.UTF_8).trim());
    }
}
