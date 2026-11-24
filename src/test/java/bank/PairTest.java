package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PairTest {

    @Test
    void pairStoresStringAndInteger() {
        Pair<String, Integer> age = new Pair<>("Ivan", 20);

        assertEquals("Ivan", age.key());
        assertEquals(20, age.value());
    }

    @Test
    void pairWorksWithAccountNumberAsKey() {
        AccountNumber number = new AccountNumber("1234567890");

        Pair<AccountNumber, String> owner = new Pair<>(number, "Ivan");

        assertEquals(number, owner.key());
        assertEquals("Ivan", owner.value());
    }
}
