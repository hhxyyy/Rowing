package nl.tudelft.sem.template.scheduler.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.scheduler.models.CustomPair;
import nl.tudelft.sem.template.scheduler.models.EventModel;
import nl.tudelft.sem.template.scheduler.models.UserRequest;
import nl.tudelft.sem.template.scheduler.validators.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class EventServiceTest {

    EventService service;
    Map<String, String> requirements;
    CustomPair<Date, Date> time;
    boolean[] positions = {false, false, true, true, false};
    EventModel eventModel;
    EventModel eventModel2;

    @BeforeEach
    void initialize() {
        this.service = new EventServiceImpl();
        this.requirements = new HashMap<>();
        this.requirements.put("Certificate", "C4");
        this.requirements.put("Gender", "M");
        this.requirements.put("Organization", "org");
        this.requirements.put("Professional", "true");
        Date d1 = new Date(2024, 5, 5);
        Date d2 = new Date(2025, 5, 5);
        this.time = new CustomPair<>(d1, d2);
        eventModel = new EventModel(1, "user", time, EventModel.Type.COMPETITION, requirements, "Slatina");
        eventModel2 = new EventModel(2, "user2", time, EventModel.Type.COMPETITION, requirements, "Bucharest");
    }

    @Test
    void testParseUserRequestInvalid() {
        assertThat(service.parseUserRequest("invalid json")).isNull();
    }

    @Test
    void testParsePickedEventInvalid() {
        assertThat(service.parsePickedEvent("invalid json")).isNull();
    }

    @Test
    void testParseEventsInvalid() {
        assertThat(service.parseEvents("invalid json")).isNull();
    }

    @Test
    void testParseUserRequestNull() {
        assertThat(service.parseUserRequest(null)).isNull();
    }

    @Test
    void testParsePickedEventNull() {
        assertThat(service.parsePickedEvent(null)).isNull();
    }

    @Test
    void testParseEventsNull() {
        assertThat(service.parseEvents(null)).isNull();
    }

    @Test
    void testFilterEventsNull() {
        UserRequest user = new UserRequest("id", List.of(time), new boolean[]{true, true, false, false, true},
                requirements);
        List<CustomPair<EventModel, Integer>> filteredEvents = service.filterEvents(user, null);
        assertTrue(filteredEvents.isEmpty());
    }

    @Test
    void testFilterEvents() {
        UserRequest user = new UserRequest("id", List.of(time), new boolean[]{true, true, false, false, true},
                requirements);
        List<CustomPair<EventModel, Integer>> filteredEvents = service.filterEvents(user, List.of(eventModel));
        assertEquals(3, filteredEvents.size());
        assertEquals(eventModel, filteredEvents.get(0).getFirst());
        assertEquals(0, filteredEvents.get(0).getSecond());
        assertEquals(eventModel, filteredEvents.get(1).getFirst());
        assertEquals(1, filteredEvents.get(1).getSecond());
    }

    @Test
    void testParseUserRequest() {
        List<CustomPair<Date, Date>> availability = new ArrayList<>();
        availability.add(time);
        UserRequest userRequest = new UserRequest("user", availability, positions, requirements);
        String jsonVersion = null;
        try {
            jsonVersion = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(userRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        assertEquals(service.parseUserRequest(jsonVersion), userRequest);
    }

    @Test
    void testParsePickedEvent() {
        CustomPair<EventModel, Integer> decision = new CustomPair<>(eventModel, 2);
        String jsonVersion = null;
        try {
            jsonVersion = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(decision);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        assertEquals(service.parsePickedEvent(jsonVersion), decision);
    }

    @Test
    void testParseEvents() {
        List<EventModel> list = new ArrayList<>();
        list.add(eventModel);
        list.add(eventModel2);
        String jsonVersion = null;
        try {
            jsonVersion = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        assertEquals(service.parseEvents(jsonVersion), list);
    }

    @Test
    public void testGetAllEvents() {
        //TODO: integration testing for this test
        assertTrue(true);
    }

    @Test
    public void testGetEventOwner() {
        //TODO: integration testing for this test
        assertTrue(true);
    }
}
