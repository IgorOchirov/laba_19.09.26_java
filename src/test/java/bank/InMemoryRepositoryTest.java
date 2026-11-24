package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryRepositoryTest {

    @Test
    void savesAndFindsAccountById() {
        InMemoryRepository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        BankAccount account = new DebitAccount(
                new AccountNumber("1111111111"),
                "Ivan",
                1_000
        );

        repository.save(account);

        assertAll(
                () -> assertSame(account, repository.findById(account.getId())),
                () -> assertTrue(repository.existsById(account.getId())),
                () -> assertEquals(1, repository.size())
        );
    }

    @Test
    void repeatedSaveReplacesEntityWithSameId() {
        InMemoryRepository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        AccountNumber number = new AccountNumber("1111111111");
        BankAccount first = new DebitAccount(number, "Ivan", 1_000);
        BankAccount second = new DebitAccount(number, "Petr", 2_000);

        repository.save(first);
        repository.save(second);

        assertAll(
                () -> assertEquals(1, repository.size()),
                () -> assertSame(second, repository.findById(number)),
                () -> assertEquals("Petr", repository.findById(number).getOwner())
        );
    }

    @Test
    void saveNullThrowsException() {
        InMemoryRepository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();

        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }

    @Test
    void findUnknownIdThrowsException() {
        InMemoryRepository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        AccountNumber unknown = new AccountNumber("9999999999");

        assertThrows(EntityNotFoundException.class, () -> repository.findById(unknown));
    }

    @Test
    void existsByIdReturnsFalseForUnknownId() {
        InMemoryRepository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        repository.save(new DebitAccount(
                new AccountNumber("1111111111"),
                "Ivan",
                1_000
        ));

        assertFalse(repository.existsById(new AccountNumber("9999999999")));
    }

    @Test
    void worksWithTransactions() {
        InMemoryRepository<Long, Transaction> repository = new InMemoryRepository<>();
        Transaction transaction = new Transaction(
                10L,
                TransactionType.DEPOSIT,
                new AccountNumber("1111111111"),
                500,
                TransactionStatus.SUCCESS
        );

        repository.save(transaction);

        assertAll(
                () -> assertSame(transaction, repository.findById(10L)),
                () -> assertTrue(repository.existsById(10L)),
                () -> assertEquals(1, repository.size())
        );
    }
}
