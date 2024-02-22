package nl.tudelft.sem.template.user.services;

import nl.tudelft.sem.template.user.domain.CustomPair;
import nl.tudelft.sem.template.user.exceptions.InvalidModelException;
import nl.tudelft.sem.template.user.models.EventModel;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class OwnerServiceTest {
    @Autowired
    private transient MockMvc mockMvc;

    OwnerServiceImpl service;

    @BeforeEach
    void setup() {
        service = new OwnerServiceImpl();
    }

    @Test
    void testValidModelCompetition() {
        assertThat(service.isValidEventType("cOMpeTItion")).isTrue();
    }

    @Test
    void testValidModelTraining() {
        assertThat(service.isValidEventType("traininG")).isTrue();
    }

    @Test
    void testInValidModel() {
        assertThat(service.isValidEventType("abc")).isFalse();
    }

    @Test
    void testCreateEventInvalidEvent1() {
        Date before = new Date(2022, Calendar.DECEMBER, 13, 4, 30);
        Date after = new Date(2022, Calendar.DECEMBER, 13, 5, 0);
        EventModel eventModel = new EventModel("snack", "here", new CustomPair<>(before, after), new HashMap<>());
        assertThrows(InvalidModelException.class,
                () -> {
                service.createEvent(eventModel); }, "The type of an event can only be \"competition\" or \"training\".");
    }

    @Test
    void testEditEventInvalidEvent() {
        Date before = new Date(2022, Calendar.DECEMBER, 13, 4, 30);
        Date after = new Date(2022, Calendar.DECEMBER, 13, 5, 0);
        EventModel eventModel = new EventModel("snack", "here", new CustomPair<>(before, after), new HashMap<>());
        assertThrows(InvalidModelException.class,
                () -> {
                service.editEvent(1L, eventModel); }, "The type of an event can only be \"competition\" or \"training\".");
    }

    @Test
    void testCreateEventInvalidEvent2() {
        Date before = new Date(2022, Calendar.DECEMBER, 13, 4, 30);
        Date after = new Date(2022, Calendar.DECEMBER, 13, 5, 0);
        EventModel eventModel = new EventModel("Training", "here", new CustomPair<>(before, after), new HashMap<>());
        assertThrows(InvalidModelException.class,
                () -> {
                service.createEvent(eventModel); }, "The requirements field cannot be empty.");
    }

    @Test
    void testCreateEventInvalidEvent3() {
        Date before = new Date(2022, Calendar.DECEMBER, 13, 4, 30);
        Date after = new Date(2022, Calendar.DECEMBER, 13, 5, 0);
        EventModel eventModel = new EventModel("Trainings", "here", new CustomPair<>(before, after), new HashMap<>());
        assertThrows(InvalidModelException.class,
                () -> {
                service.createEvent(eventModel); }, "The type of an event can only be \"competition\" or \"training\".");
    }

    @Test
    void testCreateEventInvalidEvent4() {
        Map<String, String> requirements = new HashMap<>();
        requirements.put("Certificate", "C4");
        requirements.put("Gender", "M ale");
        Date before = new Date(2022, Calendar.DECEMBER, 13, 4, 30);
        Date after = new Date(2022, Calendar.DECEMBER, 13, 5, 0);
        EventModel eventModel = new EventModel("Training", "here", new CustomPair<>(before, after), requirements);
        try {
            Exception e = (Exception) service.createEvent(eventModel);
            assertNotEquals(e.getMessage(), "The type of an event can only be \"competition\" or \"training\".");
            assertNotEquals(e.getMessage(), "The requirements field cannot be empty.");
        } catch (IOException | InterruptedException | NullPointerException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
