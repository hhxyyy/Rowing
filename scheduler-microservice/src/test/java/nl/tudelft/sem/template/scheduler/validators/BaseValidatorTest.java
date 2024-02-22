package nl.tudelft.sem.template.scheduler.validators;

import nl.tudelft.sem.template.scheduler.models.CustomPair;
import nl.tudelft.sem.template.scheduler.models.EventModel;
import nl.tudelft.sem.template.scheduler.models.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

public class BaseValidatorTest {
    Map<String, String> requirements;
    CustomPair<Date, Date> timeFrame;
    boolean[] positions;

    BaseValidator validator;

    List<CustomPair<Date, Date>> availability;

    @BeforeEach
    void setUp() {
        this.requirements = new HashMap<>();
        this.requirements.put("Certificate", "C4");
        this.requirements.put("Gender", "Male");

        Date d1 = new Date(2022, Calendar.DECEMBER, 27, 14, 0, 0);
        Date d2 = new Date(2022, Calendar.DECEMBER, 27, 20, 0, 0);

        this.timeFrame = new CustomPair<>(d1, d2);

        List<CustomPair<Date, Date>> availability = new ArrayList<>();
        availability.add(timeFrame);

        validator = new AvailabilityValidator(); //choose a random implementation
    }

    @Test
    void getPositionsTestNone() {
        positions = new boolean[] {false, false, false, false, false};
        UserRequest userRequest = new UserRequest("user", availability, positions, requirements);
        assertTrue(null, validator.getPosition(userRequest) == -1);
    }

    @Test
    void getPositionsTestSingle() {
        positions = new boolean[] {false, false, true, false, false};
        UserRequest userRequest = new UserRequest("user", availability, positions, requirements);
        assertTrue(null, validator.getPosition(userRequest) == 2);
    }

    @Test
    void getPositionsTestMultiple() {
        positions = new boolean[] {true, false, true, false, true};
        UserRequest userRequest = new UserRequest("user", availability, positions, requirements);
        assertTrue(null, validator.getPosition(userRequest) == 0);
    }

    @Test
    void checkNextTestNull() {
        validator.setNext(null);
        positions = new boolean[] {true, false, true, false, true};
        UserRequest userRequest = new UserRequest("user", availability, positions, requirements);
        EventModel eventModel = new EventModel(1, "owner", timeFrame, EventModel.Type.COMPETITION,
                requirements, "Delft");
        assertTrue(null, validator.checkNext(userRequest, eventModel));
    }

    @Test
    void checkNextTestMockTrue() {
        Validator next = mock(Validator.class);
        validator.setNext(next);
        positions = new boolean[] {true, false, true, false, true};
        UserRequest userRequest = new UserRequest("user", availability, positions, requirements);
        EventModel eventModel = new EventModel(1, "owner", timeFrame, EventModel.Type.COMPETITION,
                requirements, "Delft");
        when(next.handle(userRequest, eventModel)).thenReturn(true);
        assertTrue(null, validator.checkNext(userRequest, eventModel));
    }

    @Test
    void checkNextTestMockFalse() {
        Validator next = mock(Validator.class);
        validator.setNext(next);
        positions = new boolean[] {true, false, true, false, true};
        UserRequest userRequest = new UserRequest("user", availability, positions, requirements);
        EventModel eventModel = new EventModel(1, "owner", timeFrame, EventModel.Type.COMPETITION,
                requirements, "Delft");
        when(next.handle(userRequest, eventModel)).thenReturn(false);
        assertFalse(null, validator.checkNext(userRequest, eventModel));
    }
}
