package nl.tudelft.sem.template.scheduler.validators;

import nl.tudelft.sem.template.scheduler.models.CustomPair;
import nl.tudelft.sem.template.scheduler.models.EventModel;
import nl.tudelft.sem.template.scheduler.models.UserRequest;
import nl.tudelft.sem.template.scheduler.validators.AvailabilityValidator;
import nl.tudelft.sem.template.scheduler.validators.BaseValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;


public class AvailabilityValidatorTest {
    AvailabilityValidator validator = new AvailabilityValidator();
    boolean[] positions;
    Map<String, String> requirements;
    Map<String, String> trainingRequirements;

    long current;
    long minute;
    long hour;
    Date currentDate;

    @BeforeEach
    void setUp() {
        positions = new boolean[]{true, true, false, true, false};
        requirements = Map.of("Certificate", "C4", "Gender", "F",
                "Organization", "org", "Professional", "true");
        trainingRequirements = Map.of("Certificate", "C4");
        currentDate = new Date();
        current = new Date().getTime();
        minute = 60 * 1000; // milliseconds converted to one minute
        hour = 60 * minute; // minutes converted to one hour
    }

    @Test
    void testNullAvailability() {
        UserRequest user = new UserRequest("userId", null, positions, requirements);
        CustomPair<Date, Date> eventDate = new CustomPair<>(new Date(1), new Date(2));
        EventModel event = new EventModel(1, "owner", eventDate, EventModel.Type.COMPETITION,
                requirements, "loc");

        assertEquals(null, false, validator.handle(user, event));
    }

    @Test
    void testEmptyAvailability() {
        UserRequest user = new UserRequest("userId", List.of(), positions, requirements);
        CustomPair<Date, Date> eventDate = new CustomPair<>(new Date(1), new Date(2));
        EventModel event = new EventModel(1, "owner", eventDate, EventModel.Type.COMPETITION,
                requirements, "loc");

        assertEquals(null, false, validator.handle(user, event));
    }

    @Test
    void testNullEventTime() {
        CustomPair<Date, Date> ownerDate = new CustomPair<>(new Date(1), new Date(2));
        List<CustomPair<Date, Date>> userAv = List.of(ownerDate);
        UserRequest user = new UserRequest("userId", userAv, positions, requirements);
        EventModel event = new EventModel(1, "owner", null, EventModel.Type.COMPETITION,
                requirements, "loc");

        assertEquals(null, false, validator.handle(user, event));
    }

    @Test
    void testCompetitionConditionNotAvailable() {
        //User is available from the next hour to the next 3 hours
        CustomPair<Date, Date> userDate = new CustomPair<>(new Date(current + hour),
                new Date(current + 3 * hour));
        List<CustomPair<Date, Date>> userAv = List.of(userDate);
        UserRequest user = new UserRequest("userId", userAv, positions, requirements);

        //Competition could have been available, but it's too recent
        CustomPair<Date, Date> eventDate = new CustomPair<>(new Date(current + hour + 30 * minute),
                new Date(current + 2 * hour));
        EventModel event = new EventModel(1, "owner", eventDate, EventModel.Type.COMPETITION,
                requirements, "loc");

        assertEquals(null, false, validator.handle(user, event));
    }

    @Test
    void testTrainingConditionNotAvailable() {
        //User is available from the next hour to the next 3 hours
        CustomPair<Date, Date> userDate = new CustomPair<>(new Date(current),
                new Date(current + 3 * hour));
        List<CustomPair<Date, Date>> userAv = List.of(userDate);
        UserRequest user = new UserRequest("userId", userAv, positions, trainingRequirements);

        //Training could have been available, but it's too recent
        CustomPair<Date, Date> eventDate = new CustomPair<>(new Date(current + 20 * minute),
                new Date(current + 2 * hour));
        EventModel event = new EventModel(1, "owner", eventDate, EventModel.Type.TRAINING,
                requirements, "loc");

        assertEquals(null, false, validator.handle(user, event));
    }

    @Test
    void testEqualDates() {
        //User is available from tomorrow, for 6 hours
        CustomPair<Date, Date> userDate = new CustomPair<>(new Date(current + 25 * hour),
                new Date(current + 30 * hour));
        List<CustomPair<Date, Date>> userAv = List.of(userDate);
        UserRequest user = new UserRequest("userId", userAv, positions, requirements);

        //Competition has exactly the same availability dates as the user
        CustomPair<Date, Date> eventDate = new CustomPair<>(new Date(current + 25 * hour),
                new Date(current + 30 * hour));
        EventModel event = new EventModel(1, "owner", eventDate, EventModel.Type.COMPETITION,
                requirements, "loc");

        validator = mock(AvailabilityValidator.class);
        when(validator.handle(user, event)).thenCallRealMethod();
        when(validator.checkIfAvailable(userDate, eventDate)).thenCallRealMethod();
        when(validator.superWrapper(user, event)).thenReturn(true);
        assertEquals(null, true, validator.handle(user, event));
    }

    @Test
    void testDifferentDates() {
        //User is available from tomorrow, for 6 hours
        CustomPair<Date, Date> userDate = new CustomPair<>(new Date(current + 25 * hour),
                new Date(current + 30 * hour));
        List<CustomPair<Date, Date>> userAv = List.of(userDate);
        UserRequest user = new UserRequest("userId", userAv, positions, requirements);

        //Event time matches with the users, but both dates are different
        CustomPair<Date, Date> eventDate = new CustomPair<>(new Date(current + 26 * hour),
                new Date(current + 28 * hour));
        EventModel event = new EventModel(1, "owner", eventDate, EventModel.Type.COMPETITION,
                requirements, "loc");

        validator = mock(AvailabilityValidator.class);
        when(validator.handle(user, event)).thenCallRealMethod();
        when(validator.checkIfAvailable(userDate, eventDate)).thenCallRealMethod();
        when(validator.superWrapper(user, event)).thenReturn(true);
        assertEquals(null, true, validator.handle(user, event));
    }

    @Test
    void testBadAvailability() {
        //User is available from the next hour to the next 3 hours
        CustomPair<Date, Date> userDate = new CustomPair<>(new Date(current),
                new Date(current + 3 * hour));
        List<CustomPair<Date, Date>> userAv = List.of(userDate);
        UserRequest user = new UserRequest("userId", userAv, positions, trainingRequirements);

        //Event happened in the past
        CustomPair<Date, Date> eventDate = new CustomPair<>(new Date(1), new Date(2));
        EventModel event = new EventModel(1, "owner", eventDate, EventModel.Type.TRAINING,
                requirements, "loc");

        assertEquals(null, false, validator.handle(user, event));
    }
}
