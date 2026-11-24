package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoxTest {

    @Test
    void boxStoresStringWithoutCast() {
        Box<String> box = new Box<>();
        box.set("Java");

        String value = box.get();

        assertEquals("Java", value);
    }

    @Test
    void boxStoresIntegerWithoutCast() {
        Box<Integer> box = new Box<>();
        box.set(42);

        Integer value = box.get();

        assertEquals(42, value);
    }
}
