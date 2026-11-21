package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountNumberTest {

    @Test
    void validNumberIsCreated() {
        // Arrange / Act
        AccountNumber number = new AccountNumber("1234567890");

        // Assert
        assertEquals("1234567890", number.value());
    }

    @Test
    void shortNumberIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber("123"));
    }

    @Test
    void nullNumberIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber(null));
    }

    @Test
    void emptyNumberIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber(""));
    }

    @Test
    void nineDigitsAreRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber("123456789"));
    }

    @Test
    void elevenDigitsAreRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber("12345678901"));
    }

    @Test
    void lettersInNumberAreRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber("12345abc90"));
    }
}
