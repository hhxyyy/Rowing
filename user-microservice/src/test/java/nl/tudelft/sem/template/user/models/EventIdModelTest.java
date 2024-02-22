package nl.tudelft.sem.template.user.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventIdModelTest {

    private EventIdModel eventIdModel;

    @BeforeEach
    public void setUp() {
        eventIdModel = new EventIdModel();
        eventIdModel.setEventId(1L);
        eventIdModel.setPosition(3);
    }

    @Test
    void isValid() {
        assertTrue(eventIdModel.isValid());
    }

    @Test
    void isNotValid1() {
        eventIdModel.setPosition(5);
        assertFalse(eventIdModel.isValid());
    }

    @Test
    void isNotValid2() {
        eventIdModel.setPosition(-1);
        assertFalse(eventIdModel.isValid());
    }

    @Test
    void isNotValid3() {
        eventIdModel.setEventId(-1);
        assertFalse(eventIdModel.isValid());
    }

    @Test
    void getEventId() {
        assertEquals(eventIdModel.getEventId(), 1L);
    }

    @Test
    void setEventId() {
        eventIdModel.setEventId(2L);
        assertEquals(eventIdModel.getEventId(), 2L);
    }

    @Test
    void getPosition() {
        assertEquals(eventIdModel.getPosition(), 3);
    }

    @Test
    void setPosition() {
        eventIdModel.setPosition(4);
        assertEquals(eventIdModel.getPosition(), 4);
    }

}