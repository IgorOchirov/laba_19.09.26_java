package bank;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        boolean transferred = service.transfer(from, to, 3_000);

        // Assert
        assertAll(
                () -> assertTrue(transferred),
                () -> assertEquals(7_000, from.getBalance(), 0.0001),
                () -> assertEquals(5_000, to.getBalance(), 0.0001),
                () -> assertEquals(1, notifications.getNotificationCount()),
                () -> assertEquals("Transfer 3000.0 completed", notifications.getLastMessage())
        );
    }

    @Test
    void failedTransferLeavesBalancesUnchangedAndSendsNoNotification() {
        // Arrange
        FakeNotificationService notifications = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount from = new DebitAccount(new AccountNumber("4000000001"), "Иванов", 1_000);
        BankAccount to = new DebitAccount(new AccountNumber("4000000002"), "Петров", 2_000);

        // Act
        boolean transferred = service.transfer(from, to, 3_000);

        // Assert
        assertAll(
                () -> assertFalse(transferred),
                () -> assertEquals(1_000, from.getBalance(), 0.0001),
                () -> assertEquals(2_000, to.getBalance(), 0.0001),
                () -> assertEquals(0, notifications.getNotificationCount())
        );
    }

    @Test
    void cannotTransferToSameAccount() {
        // Arrange
        FakeNotificationService notifications = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount account = new DebitAccount(new AccountNumber("4000000001"), "Иванов", 10_000);

        // Act
        boolean transferred = service.transfer(account, account, 1_000);

        // Assert
        assertAll(
                () -> assertFalse(transferred),
                () -> assertEquals(10_000, account.getBalance(), 0.0001),
                () -> assertEquals(0, notifications.getNotificationCount())
        );
    }

    @Test
    void cannotTransferZeroAmount() {
        // Arrange
        FakeNotificationService notifications = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount from = new DebitAccount(new AccountNumber("4000000001"), "Иванов", 10_000);
        BankAccount to = new DebitAccount(new AccountNumber("4000000002"), "Петров", 0);

        // Act
        boolean transferred = service.transfer(from, to, 0);

        // Assert
        assertAll(
                () -> assertFalse(transferred),
                () -> assertEquals(10_000, from.getBalance(), 0.0001),
                () -> assertEquals(0, to.getBalance(), 0.0001),
                () -> assertEquals(0, notifications.getNotificationCount())
        );
    }

    @Test
    void cannotTransferNegativeAmount() {
        // Arrange
        FakeNotificationService notifications = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount from = new DebitAccount(new AccountNumber("4000000001"), "Иванов", 10_000);
        BankAccount to = new DebitAccount(new AccountNumber("4000000002"), "Петров", 0);

        // Act
        boolean transferred = service.transfer(from, to, -100);

        // Assert
        assertAll(
                () -> assertFalse(transferred),
                () -> assertEquals(10_000, from.getBalance(), 0.0001),
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
        boolean transferred = service.transfer(from, to, 10_000);

        // Assert
        assertAll(
                () -> assertTrue(transferred),
                () -> assertEquals(0, from.getBalance(), 0.0001),
                () -> assertEquals(10_000, to.getBalance(), 0.0001),
                () -> assertEquals(1, notifications.getNotificationCount()),
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

        // Act
        boolean transferred = service.transfer(from, to, 10_000);

        // Assert
        assertAll(
                () -> assertFalse(transferred),
                () -> assertEquals(10_000, from.getBalance(), 0.0001),
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
        boolean transferred = service.transfer(from, to, 3_000);

        // Assert
        assertAll(
                () -> assertTrue(transferred),
                () -> assertEquals(7_000, from.getBalance(), 0.0001),
                () -> assertEquals(5_000, to.getBalance(), 0.0001)
        );
    }

    @Test
    void transferFromDebitToSavingsAccount() {
        // Arrange
        FakeNotificationService notifications = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount from = new DebitAccount(new AccountNumber("4000000004"), "Иванов", 5_000);
        BankAccount to = new SavingsAccount(new AccountNumber("4000000007"), "Петров", 2_000, 500);

        // Act
        boolean transferred = service.transfer(from, to, 3_000);

        // Assert
        assertAll(
                () -> assertTrue(transferred),
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
        boolean transferred = service.transfer(from, to, 4_000);

        // Assert
        assertAll(
                () -> assertTrue(transferred),
                () -> assertEquals(-3_000, from.getBalance(), 0.0001),
                () -> assertEquals(6_000, to.getBalance(), 0.0001),
                () -> assertEquals(1, notifications.getNotificationCount()),
                () -> assertEquals("Transfer 4000.0 completed", notifications.getLastMessage())
        );
    }

    @Test
    void transferFromSavingsToDebitAccount() {
        // Arrange
        FakeNotificationService notifications = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount from = new SavingsAccount(new AccountNumber("4000000007"), "Петров", 5_000, 1_000);
        BankAccount to = new DebitAccount(new AccountNumber("4000000004"), "Иванов", 1_000);

        // Act
        boolean transferred = service.transfer(from, to, 3_000);

        // Assert
        assertAll(
                () -> assertTrue(transferred),
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
