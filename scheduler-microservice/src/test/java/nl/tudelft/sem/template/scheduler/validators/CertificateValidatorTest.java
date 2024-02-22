package nl.tudelft.sem.template.scheduler.validators;

import nl.tudelft.sem.template.scheduler.models.CustomPair;
import nl.tudelft.sem.template.scheduler.models.EventModel;
import nl.tudelft.sem.template.scheduler.models.UserRequest;
import nl.tudelft.sem.template.scheduler.validators.CertificateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

public class CertificateValidatorTest {

    Map<String, Set<String>> certificates;
    CertificateValidator validator;
    Map<String, String> userRequirements;
    Map<String, String> eventRequirements;
    CustomPair<Date, Date> dateUser;
    CustomPair<Date, Date> dateEvent;
    Date current = new Date();

    @BeforeEach
    void setUp() {
        certificates = new HashMap<>() {{
                put("8+", Set.of("8+", "4+", "C4"));
                put("4+", Set.of("4+", "C4"));
                put("C4", Set.of("C4"));
            }};

        userRequirements = new HashMap<>() {{
                put("Certificate", "4+");
                put("Gender", "F");
                put("Organization", "org");
                put("Professional", "true");
            }};
        eventRequirements = new HashMap<>(userRequirements);

        dateUser = new CustomPair<>(new Date(1), new Date(2));
        dateEvent = new CustomPair<>(new Date(1), new Date(2));
    }

    private void setDefaultCertificates(CertificateValidator validator) {
        try {
            Field field = CertificateValidator.class.getDeclaredField("certificates");
            field.setAccessible(true);

            field.set(validator, certificates);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testCertificateNullRequirement() {
        validator = mock(CertificateValidator.class);
        setDefaultCertificates(validator);

        boolean[] positions = {true, false, true, true, true};

        eventRequirements.put("Certificate", "4+");

        UserRequest user = new UserRequest("id", List.of(dateUser), positions, null);
        EventModel event = new EventModel(1, "owner", dateEvent,
                EventModel.Type.COMPETITION, eventRequirements, "loc");

        when(validator.superWrapper(user, event)).thenReturn(true);
        when(validator.handle(user, event)).thenCallRealMethod();

        assertThat(validator.handle(user, event)).isFalse();
    }

    @Test
    void testNoCertificateNeededEvent() {
        validator = mock(CertificateValidator.class);
        setDefaultCertificates(validator);

        boolean[] positions = {true, false, true, true, true};

        userRequirements.put("Certificate", "4+");
        eventRequirements.remove("Certificate");

        UserRequest user = new UserRequest("id", List.of(dateUser), positions, userRequirements);
        EventModel event = new EventModel(1, "owner", dateEvent,
                EventModel.Type.COMPETITION, eventRequirements, "loc");

        when(validator.superWrapper(user, event)).thenReturn(true);
        when(validator.handle(user, event)).thenCallRealMethod();

        assertThat(validator.handle(user, event)).isTrue();
    }

    @Test
    void testNoCertificateNeededPosition() {
        for (int position = 1; position < 5; position++) {
            validator = mock(CertificateValidator.class);
            setDefaultCertificates(validator);

            boolean[] positions = {true, false, true, true, true};

            userRequirements.put("Certificate", "4+");
            eventRequirements.put("Certificate", "4+");

            UserRequest user = new UserRequest("id", List.of(dateUser), positions, userRequirements);
            EventModel event = new EventModel(1, "owner", dateEvent,
                    EventModel.Type.COMPETITION, eventRequirements, "loc");

            when(validator.superWrapper(user, event)).thenReturn(true);
            when(validator.handle(user, event)).thenCallRealMethod();

            assertThat(validator.handle(user, event)).isTrue();
        }
    }

    @Test
    void testValidCertificateEqual() {
        validator = mock(CertificateValidator.class);
        setDefaultCertificates(validator);

        boolean[] positions = {true, true, true, true, true};

        userRequirements.put("Certificate", "4+");
        eventRequirements.put("Certificate", "4+");

        UserRequest user = new UserRequest("id", List.of(dateUser), positions, userRequirements);
        EventModel event = new EventModel(1, "owner", dateEvent,
                EventModel.Type.COMPETITION, eventRequirements, "loc");

        when(validator.superWrapper(user, event)).thenReturn(true);
        when(validator.handle(user, event)).thenCallRealMethod();

        assertThat(validator.handle(user, event)).isTrue();
    }

    @Test
    void testValidCertificateHigher() {
        validator = mock(CertificateValidator.class);
        setDefaultCertificates(validator);

        boolean[] positions = {true, true, true, true, true};

        userRequirements.put("Certificate", "8+");
        eventRequirements.put("Certificate", "C4");

        UserRequest user = new UserRequest("id", List.of(dateUser), positions, userRequirements);
        EventModel event = new EventModel(1, "owner", dateEvent,
                EventModel.Type.COMPETITION, eventRequirements, "loc");

        when(validator.superWrapper(user, event)).thenReturn(true);
        when(validator.handle(user, event)).thenCallRealMethod();

        assertThat(validator.handle(user, event)).isTrue();
    }

    @Test
    void testInvalidCertificateLower() {
        validator = mock(CertificateValidator.class);
        setDefaultCertificates(validator);

        boolean[] positions = {true, true, true, true, true};

        userRequirements.put("Certificate", "4+");
        eventRequirements.put("Certificate", "8+");

        UserRequest user = new UserRequest("id", List.of(dateUser), positions, userRequirements);
        EventModel event = new EventModel(1, "owner", dateEvent,
                EventModel.Type.COMPETITION, eventRequirements, "loc");

        when(validator.superWrapper(user, event)).thenReturn(true);
        when(validator.handle(user, event)).thenCallRealMethod();

        assertThat(validator.handle(user, event)).isFalse();
    }

    @Test
    void testInvalidCertificateUnknown() {
        validator = mock(CertificateValidator.class);
        setDefaultCertificates(validator);

        boolean[] positions = {true, true, true, true, true};

        userRequirements.put("Certificate", "SomeUnknownCertificate");
        eventRequirements.put("Certificate", "8+");

        UserRequest user = new UserRequest("id", List.of(dateUser), positions, userRequirements);
        EventModel event = new EventModel(1, "owner", dateEvent,
                EventModel.Type.COMPETITION, eventRequirements, "loc");

        when(validator.superWrapper(user, event)).thenReturn(true);
        when(validator.handle(user, event)).thenCallRealMethod();

        assertThat(validator.handle(user, event)).isFalse();
    }

    @Test
    public void testAddCertificate() {
        assertThat(CertificateValidator.addCertificate("A", Set.of("B", "C", "D")))
                .extracting("A").isEqualTo(Set.of("A", "B", "C", "D"));
    }
}
