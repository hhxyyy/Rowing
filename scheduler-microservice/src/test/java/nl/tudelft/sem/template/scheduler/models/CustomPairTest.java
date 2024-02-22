package nl.tudelft.sem.template.scheduler.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.tudelft.sem.template.scheduler.models.CustomPair;
import org.junit.jupiter.api.Test;

import java.util.Date;


public class CustomPairTest {
    @Test
    void customPairTest() {
        CustomPair<Integer, String> c = new CustomPair<Integer, String>(5, "test");
        assertEquals(5, (int) c.getFirst());
        assertEquals("test", c.getSecond());
    }

    @Test
    void customPairTestWithObjects() {
        Date d1 = new Date(1);
        Date d2 = new Date(2);
        CustomPair<Date, Date> dates = new CustomPair<>(d1, d2);
        assertEquals(1, dates.getFirst().getTime());
        assertEquals(2, dates.getSecond().getTime());
    }
}
