package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArrayUtilsTest {

    @Test
    void firstReturnsFirstElement() {
        String[] values = {"Ivan", "Petr", "Anna"};

        assertEquals("Ivan", ArrayUtils.first(values));
    }

    @Test
    void lastReturnsLastElement() {
        String[] values = {"Ivan", "Petr", "Anna"};

        assertEquals("Anna", ArrayUtils.last(values));
    }

    @Test
    void containsUsesEquals() {
        String[] values = {"Java", "C++"};

        assertTrue(ArrayUtils.contains(values, new String("Java")));
    }

    @Test
    void containsReturnsFalseForMissingElement() {
        Integer[] values = {1, 2, 3};

        assertFalse(ArrayUtils.contains(values, 4));
    }

    @Test
    void firstAndLastRejectEmptyArray() {
        String[] values = {};

        assertThrows(IllegalArgumentException.class, () -> ArrayUtils.first(values));
        assertThrows(IllegalArgumentException.class, () -> ArrayUtils.last(values));
    }
}
