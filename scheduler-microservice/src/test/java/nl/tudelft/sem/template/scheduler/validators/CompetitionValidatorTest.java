package nl.tudelft.sem.template.scheduler.validators;

import nl.tudelft.sem.template.scheduler.models.CustomPair;
import nl.tudelft.sem.template.scheduler.models.EventModel;
import nl.tudelft.sem.template.scheduler.models.UserRequest;
import nl.tudelft.sem.template.scheduler.validators.CompetitionValidator;
import org.h2.engine.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertFalse;

public class CompetitionValidatorTest {

    CompetitionValidator validator = new CompetitionValidator();
    CustomPair<Date, Date> userDate;
    CustomPair<Date, Date> eventDate;
    boolean[] positions;
    Date current = new Date();
    Map<String, String> competitionRequirements;
    Map<String, String> trainingRequirements;

    @BeforeEach
    void setUp() {
        positions = new boolean[]{true, true, false, true, false};
        userDate = new CustomPair<>(new Date(1), new Date(2));
        eventDate = new CustomPair<>(new Date(1), new Date(2));
        competitionRequirements = Map.of("Certificate", "C4", "Gender", "F",
                "Organization", "org", "Professional", "false");
        trainingRequirements = Map.of("Certificate", "C4");
    }

    @Test
    void testTraining() {
        UserRequest user = new UserRequest("id", List.of(userDate), positions, trainingRequirements);
        EventModel event = new EventModel(1, "owner", eventDate, EventModel.Type.TRAINING,
                trainingRequirements, "loc");
        validator = mock(CompetitionValidator.class);
        when(validator.superWrapper(user, event)).thenReturn(true);
        when(validator.handle(user, event)).thenCallRealMethod();
        assertEquals(null, true, validator.handle(user, event));
    }

    @Test
    void testCertificate() {
        // Use the training requirements, so we only have the "certificate" field for competition testing
        UserRequest user = new UserRequest("id", List.of(userDate), positions, trainingRequirements);
        EventModel event = new EventModel(1, "owner", eventDate, EventModel.Type.COMPETITION,
                trainingRequirements, "loc");
        validator = mock(CompetitionValidator.class);
        when(validator.superWrapper(user, event)).thenReturn(true);
        when(validator.handle(user, event)).thenCallRealMethod();
        assertEquals(null, true, validator.handle(user, event));
    }

    @Test
    void testRequirementNotInUser() {
        UserRequest user = new UserRequest("id", List.of(userDate), positions, competitionRequirements);
        Map<String, String> competitionRequirementsExtra = Map.of("Certificate", "C4", "Gender", "F",
                "Organization", "org", "Professional", "true", "NewReq", "req");
        EventModel event = new EventModel(1, "owner", eventDate, EventModel.Type.COMPETITION,
                competitionRequirementsExtra, "loc");

        assertEquals(null, false, validator.handle(user, event));
    }

    @Test
    void requirementNotInEvent() {
        UserRequest user = new UserRequest("id", List.of(userDate), positions, trainingRequirements);
        Map<String, String> trainingRequirementsNone = Map.of();
        EventModel event = new EventModel(1, "owner", eventDate, EventModel.Type.COMPETITION,
                trainingRequirementsNone, "loc");
        validator = mock(CompetitionValidator.class);
        when(validator.superWrapper(user, event)).thenReturn(true);
        when(validator.handle(user, event)).thenCallRealMethod();
        assertEquals(null, true, validator.handle(user, event));
    }

    @Test
    void testGoodRequirements() {
        UserRequest user = new UserRequest("id", List.of(userDate), positions, competitionRequirements);
        EventModel event = new EventModel(1, "owner", eventDate, EventModel.Type.COMPETITION,
                competitionRequirements, "loc");
        validator = mock(CompetitionValidator.class);
        when(validator.superWrapper(user, event)).thenReturn(true);
        when(validator.handle(user, event)).thenCallRealMethod();
        assertEquals(null, true, validator.handle(user, event));
    }

    @Test
    void testBadRequirements() {
        Map<String, String> badUserRequirements = Map.of("Certificate", "C4", "Gender", "M",
                "Organization", "org", "Professional", "true");
        UserRequest user = new UserRequest("id", List.of(userDate), positions, badUserRequirements);
        EventModel event = new EventModel(1, "owner", eventDate, EventModel.Type.COMPETITION,
                competitionRequirements, "loc");

        assertEquals(null, false, validator.handle(user, event));
    }

    @Test
    void testProfessionalTrue() {
        Map<String, String> competitionProfessional = Map.of("Certificate", "C4", "Gender", "F",
                "Organization", "org", "Professional", "true");
        UserRequest user = new UserRequest("id", List.of(userDate), positions, competitionProfessional);
        EventModel event = new EventModel(1, "owner", eventDate, EventModel.Type.COMPETITION,
                competitionProfessional, "loc");
        validator = mock(CompetitionValidator.class);
        when(validator.superWrapper(user, event)).thenReturn(true);
        when(validator.handle(user, event)).thenCallRealMethod();
        assertEquals(null, true, validator.handle(user, event));
    }

    @Test
    void testUserNotProfessional() {
        Map<String, String> userNotProfessional = Map.of("Professional", "false");
        Map<String, String> competitionProfessional = Map.of("Professional", "true");
        UserRequest user = new UserRequest("id", List.of(userDate), positions, userNotProfessional);
        EventModel event = new EventModel(1, "owner", eventDate, EventModel.Type.COMPETITION,
                competitionProfessional, "loc");

        assertFalse(null, validator.handle(user, event));
    }

    @Test
    void testBooleanDifferentRequirement() {
        Map<String, String> userNotProfessional = Map.of("notProfessional", "false");
        Map<String, String> competitionProfessional = Map.of("notProfessional", "true");
        UserRequest user = new UserRequest("id", List.of(userDate), positions, userNotProfessional);
        EventModel event = new EventModel(1, "owner", eventDate, EventModel.Type.COMPETITION,
                competitionProfessional, "loc");

        assertFalse(null, validator.handle(user, event));
    }
}
