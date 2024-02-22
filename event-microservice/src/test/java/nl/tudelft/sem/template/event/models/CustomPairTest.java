package nl.tudelft.sem.template.event.models;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class CustomPairTest {
    @Test
    void customPairTest() {
        CustomPair<Integer, String> c = new CustomPair<>(5, "test");

        assertEquals(5, (int) c.getFirst());
        assertEquals("test", c.getSecond());
    }

    @Test
    void customPairTestWithObjects() {
        CustomPair<Date, Date> dates = new CustomPair<>(new Date(1), new Date(2));

        assertEquals(1, dates.getFirst().getTime());
        assertEquals(2, dates.getSecond().getTime());
    }
}
