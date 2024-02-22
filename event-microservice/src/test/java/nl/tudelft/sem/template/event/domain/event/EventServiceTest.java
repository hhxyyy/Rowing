package nl.tudelft.sem.template.event.domain.event;

import nl.tudelft.sem.template.event.domain.event.repo.Event;
import nl.tudelft.sem.template.event.domain.event.repo.EventRepository;
import nl.tudelft.sem.template.event.exceptions.InvalidModelException;
import nl.tudelft.sem.template.event.models.AddEventModel;
import nl.tudelft.sem.template.event.models.CustomPair;
import nl.tudelft.sem.template.event.models.EditEventModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class EventServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventService eventService;

    @MockBean
    private transient EventRepository eventRepository;

    private Date before;
    private Date after;

    @BeforeEach
    public void setup() {
        before = new Date(2022, Calendar.DECEMBER, 13, 4, 30);
        after = new Date(2022, Calendar.DECEMBER, 13, 5, 0);
    }

    @Test
    void getAllEventsEmpty() {
        when(eventRepository.findAll()).thenReturn(List.of());

        assertThat(eventService.getAllEvents()).isEmpty();
    }

    @Test
    void getAllEvents() {
        CustomPair<Date, Date> pair = new CustomPair<>(before, after);
        Event event1 = new Event(Event.Type.TRAINING, "owner", "location", pair, Map.of());
        Event event2 = new Event(Event.Type.TRAINING, "owner", "location", pair, Map.of());
        Event event3 = new Event(Event.Type.COMPETITION, "owner", "location", pair, Map.of(
                "Gender", "M",
                "Organization", "abc"
        ));

        List<Event> events = List.of(event1, event2, event3);
        when(eventRepository.findAll()).thenReturn(events);

        assertThat(eventService.getAllEvents()).containsAll(events);
    }

    @Test
    void addEvent() {
        try {
            CustomPair<Date, Date> pair = new CustomPair<>(before, after);
            AddEventModel model = new AddEventModel("type", "location", pair, Map.of());
            Event event = new Event(Event.Type.COMPETITION, "owner", "location", pair, Map.of(
                    "Gender", "M",
                    "Organization", "abc"
            ));

            when(eventRepository.save(event)).thenReturn(event);

            try (MockedStatic<Event> eventClass = mockStatic(Event.class)) {
                eventClass.when(() -> Event.parseModel(model, null)).thenReturn(event);

                assertThat(eventService.addEvent(model, null)).isEqualTo(event);
            }
        } catch (InvalidModelException e) {
            fail();
        }
    }

    @Test
    void editEvent() {
        try {
            CustomPair<Date, Date> pair = new CustomPair<>(before, after);
            CustomPair<Date, Date> newPair = new CustomPair<>(new Date(12), new Date(1234));
            EditEventModel model = new EditEventModel(Event.Type.TRAINING, "newLocation", newPair, Map.of());
            Event event = new Event(Event.Type.COMPETITION, "owner", "location", pair, Map.of(
                    "Gender", "M",
                    "Organization", "abc"
            ));

            when(eventRepository.findById(1)).thenReturn(Optional.of(event));

            Event edited = eventService.editEvent(1, model);

            assertThat(edited.getRequirements()).isEqualTo(Map.of());
            assertThat(edited.getLocation()).isEqualTo("newLocation");
            assertThat(edited.getTimeFrame()).isEqualTo(newPair);
        } catch (InvalidModelException e) {
            fail();
        }
    }

    @Test
    void searchEventByIdNonExistent() {
        when(eventRepository.findById(1)).thenReturn(Optional.empty());

        assertThat(eventService.searchEventById(1)).isEqualTo(Optional.empty());
    }

    @Test
    void searchEventById() {
        CustomPair<Date, Date> pair = new CustomPair<>(before, after);
        Event event = new Event(Event.Type.COMPETITION, "owner", "location", pair, Map.of(
                "Gender", "M",
                "Organization", "abc"
        ));

        when(eventRepository.findById(1)).thenReturn(Optional.of(event));

        assertThat(eventService.searchEventById(1)).isEqualTo(Optional.of(event));
    }

    @Test
    void getEventByIdNonExistent() {
        when(eventRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventById(1))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void getEventById() {
        CustomPair<Date, Date> pair = new CustomPair<>(before, after);
        Event event = new Event(Event.Type.COMPETITION, "owner", "location", pair, Map.of(
                "Gender", "M",
                "Organization", "abc"
        ));

        when(eventRepository.findById(1)).thenReturn(Optional.of(event));

        assertThat(eventService.getEventById(1)).isEqualTo(event);
    }

    @Test
    void existsByIdFalse() {
        when(eventRepository.existsById(1)).thenReturn(false);

        assertThat(eventService.existsById(1)).isFalse();
    }

    @Test
    void existsByIdTrue() {
        when(eventRepository.existsById(1)).thenReturn(true);

        assertThat(eventService.existsById(1)).isTrue();
    }

    @Test
    void deleteEvent() {
        eventService.deleteEvent(1);

        verify(eventRepository, times(1)).deleteById(1L);
    }
}