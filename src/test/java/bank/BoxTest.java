package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoxTest {

    @Test
    void boxWithStringCanBeCastBackToString() {
        Box box = new Box();
        box.set("Java");

        String value = (String) box.get();

        assertEquals("Java", value);
    }

    @Test
    void boxWithIntegerThrowsClassCastException() {
        Box box = new Box();
        box.set(123);

        assertThrows(ClassCastException.class, () -> {
            String value = (String) box.get();
        });
    }
}
