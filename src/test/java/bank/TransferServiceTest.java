package bank;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransferServiceTest {

    private static class CollectingNotifications implements NotificationService {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void notify(String message) {
            messages.add(message);
        }
    }

    @Test
    void transferMovesMoneyBetweenAccounts() {
        // Arrange
        CollectingNotifications notifications = new CollectingNotifications();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount from = new DebitAccount("A", "Иванов", 10_000);
        BankAccount to = new DebitAccount("B", "Петров", 2_000);

        // Act
        boolean transferred = service.transfer(from, to, 3_000);

        // Assert
        assertAll(
                () -> assertTrue(transferred),
                () -> assertEquals(7_000, from.getBalance(), 0.0001),
                () -> assertEquals(5_000, to.getBalance(), 0.0001),
                () -> assertEquals(List.of("Transfer 3000.0 completed"), notifications.messages)
        );
    }

    @Test
    void failedTransferLeavesBalancesUnchangedAndSendsNoNotification() {
        // Arrange
        CollectingNotifications notifications = new CollectingNotifications();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount from = new DebitAccount("A", "Иванов", 1_000);
        BankAccount to = new DebitAccount("B", "Петров", 2_000);

        // Act
        boolean transferred = service.transfer(from, to, 3_000);

        // Assert
        assertAll(
                () -> assertFalse(transferred),
                () -> assertEquals(1_000, from.getBalance(), 0.0001),
                () -> assertEquals(2_000, to.getBalance(), 0.0001),
                () -> assertTrue(notifications.messages.isEmpty())
        );
    }

    @Test
    void cannotTransferToSameAccount() {
        // Arrange
        CollectingNotifications notifications = new CollectingNotifications();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount account = new DebitAccount("A", "Иванов", 10_000);

        // Act
        boolean transferred = service.transfer(account, account, 1_000);

        // Assert
        assertAll(
                () -> assertFalse(transferred),
                () -> assertEquals(10_000, account.getBalance(), 0.0001),
                () -> assertTrue(notifications.messages.isEmpty())
        );
    }

    @Test
    void cannotTransferZeroOrNegativeAmount() {
        // Arrange
        CollectingNotifications notifications = new CollectingNotifications();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount from = new DebitAccount("A", "Иванов", 10_000);
        BankAccount to = new DebitAccount("B", "Петров", 0);

        // Act
        boolean zeroTransfer = service.transfer(from, to, 0);
        boolean negativeTransfer = service.transfer(from, to, -100);

        // Assert
        assertAll(
                () -> assertFalse(zeroTransfer),
                () -> assertFalse(negativeTransfer),
                () -> assertEquals(10_000, from.getBalance(), 0.0001),
                () -> assertEquals(0, to.getBalance(), 0.0001),
                () -> assertTrue(notifications.messages.isEmpty())
        );
    }

    @Test
    void percentCommissionIsDeductedFromSenderOnly() {
        // Arrange
        CollectingNotifications notifications = new CollectingNotifications();
        TransferService service = new TransferService(new PercentCommission(1), notifications);
        BankAccount from = new DebitAccount("A", "Иванов", 10_100);
        BankAccount to = new DebitAccount("B", "Петров", 0);

        // Act
        boolean transferred = service.transfer(from, to, 10_000);

        // Assert
        assertAll(
                () -> assertTrue(transferred),
                () -> assertEquals(0, from.getBalance(), 0.0001),
                () -> assertEquals(10_000, to.getBalance(), 0.0001),
                () -> assertEquals(List.of("Transfer 10000.0 completed"), notifications.messages)
        );
    }

    @Test
    void transferFailsWhenSenderCannotPayCommission() {
        // Arrange
        CollectingNotifications notifications = new CollectingNotifications();
        TransferService service = new TransferService(new PercentCommission(1), notifications);
        BankAccount from = new DebitAccount("A", "Иванов", 10_000);
        BankAccount to = new DebitAccount("B", "Петров", 2_000);

        // Act
        boolean transferred = service.transfer(from, to, 10_000);

        // Assert
        assertAll(
                () -> assertFalse(transferred),
                () -> assertEquals(10_000, from.getBalance(), 0.0001),
                () -> assertEquals(2_000, to.getBalance(), 0.0001),
                () -> assertTrue(notifications.messages.isEmpty())
        );
    }

    @Test
    void transferWorksWithDifferentAccountTypes() {
        // Arrange
        CollectingNotifications notifications = new CollectingNotifications();
        TransferService service = new TransferService(new NoCommission(), notifications);
        BankAccount from = new CreditAccount("C", "Сидоров", 1_000, 5_000);
        BankAccount to = new SavingsAccount("S", "Петров", 2_000, 500);

        // Act
        boolean transferred = service.transfer(from, to, 4_000);

        // Assert
        assertAll(
                () -> assertTrue(transferred),
                () -> assertEquals(-3_000, from.getBalance(), 0.0001),
                () -> assertEquals(6_000, to.getBalance(), 0.0001),
                () -> assertEquals(List.of("Transfer 4000.0 completed"), notifications.messages)
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
