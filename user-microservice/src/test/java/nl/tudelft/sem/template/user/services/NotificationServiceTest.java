package nl.tudelft.sem.template.user.services;

import nl.tudelft.sem.template.user.authentication.AuthManager;
import nl.tudelft.sem.template.user.util.MockHttpResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class NotificationServiceTest {

    @Mock
    AuthManager authManager;

    @Mock
    NotificationServiceImpl service;

    @Test
    void testValidModelCompetitionBadResponse() {
        when(authManager.getUsername()).thenReturn("ExampleUser");

        try {
            when(service.requestAllNotifications(any())).thenReturn(new MockHttpResponse(400, ""));
            when(service.getNotifications(any())).thenCallRealMethod();

            assertThat(service.getNotifications(authManager))
                    .isEqualTo("You currently have no notifications.");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testValidModelCompetitionBlankResponse() {
        when(authManager.getUsername()).thenReturn("ExampleUser");

        try {
            when(service.requestAllNotifications(any())).thenReturn(new MockHttpResponse(200, ""));
            when(service.getNotifications(any())).thenCallRealMethod();

            assertThat(service.getNotifications(authManager))
                    .isEqualTo("You currently have no notifications.");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testValidModelCompetitionEmptyObjectResponse() {
        when(authManager.getUsername()).thenReturn("ExampleUser");

        try {
            when(service.requestAllNotifications(any())).thenReturn(new MockHttpResponse(200, "{}"));
            when(service.getNotifications(any())).thenCallRealMethod();

            assertThat(service.getNotifications(authManager))
                    .isEqualTo("You currently have no notifications.");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testValidModelCompetitionEmptyArrayResponse() {
        when(authManager.getUsername()).thenReturn("ExampleUser");

        try {
            when(service.requestAllNotifications(any())).thenReturn(new MockHttpResponse(200, "[]"));
            when(service.getNotifications(any())).thenCallRealMethod();

            assertThat(service.getNotifications(authManager))
                    .isEqualTo("You currently have no notifications.");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testValidModelCompetitionMissingId() {
        when(authManager.getUsername()).thenReturn("ExampleUser");

        String jsonInput = "[{\"ownerId\":\"robert\",\"userId\":\"admin\",\"eventId\":33,\"position\":0}]";

        try {
            when(service.requestAllNotifications(any())).thenReturn(new MockHttpResponse(200, jsonInput));
            when(service.getNotifications(any())).thenCallRealMethod();

            assertThat(service.getNotifications(authManager)).isEqualTo("You currently have no notifications.");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testValidModelCompetitionMissingPosition() {
        when(authManager.getUsername()).thenReturn("ExampleUser");

        String jsonInput = "[{\"id\":30,\"ownerId\":\"robert\",\"userId\":\"admin\",\"eventId\":33}]";

        try {
            when(service.requestAllNotifications(any())).thenReturn(new MockHttpResponse(200, jsonInput));
            when(service.getNotifications(any())).thenCallRealMethod();

            assertThat(service.getNotifications(authManager)).isEqualTo("You currently have no notifications.");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testValidModelCompetitionMissingUserId() {
        when(authManager.getUsername()).thenReturn("ExampleUser");

        String jsonInput = "[{\"id\":30,\"ownerId\":\"robert\",\"eventId\":33,\"position\":0}]";

        try {
            when(service.requestAllNotifications(any())).thenReturn(new MockHttpResponse(200, jsonInput));
            when(service.getNotifications(any())).thenCallRealMethod();

            assertThat(service.getNotifications(authManager)).isEqualTo("You currently have no notifications.");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testValidModelCompetitionMissingOwnerId() {
        when(authManager.getUsername()).thenReturn("ExampleUser");

        String jsonInput = "[{\"id\":30,\"userId\":\"admin\",\"eventId\":33,\"position\":0}]";

        try {
            when(service.requestAllNotifications(any())).thenReturn(new MockHttpResponse(200, jsonInput));
            when(service.getNotifications(any())).thenCallRealMethod();

            assertThat(service.getNotifications(authManager)).isEqualTo("You currently have no notifications.");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testValidModelCompetitionNoMatches() {
        when(authManager.getUsername()).thenReturn("ExampleUser");

        String jsonInput = "[{\"id\":30,\"ownerId\":\"abc\",\"userId\":\"admin\",\"eventId\":33,\"position\":0},"
                           + "{\"id\":32,\"ownerId\":\"admin\",\"userId\":\"abc\",\"eventId\":33,\"position\":0},"
                           + "{\"id\":33,\"ownerId\":\"admin\",\"userId\":\"admin\",\"eventId\":33,\"position\":0}]";

        try {
            when(service.requestAllNotifications(any())).thenReturn(new MockHttpResponse(200, jsonInput));
            when(service.getNotifications(any())).thenCallRealMethod();

            assertThat(service.getNotifications(authManager)).isEqualTo("You currently have no notifications.");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testValidModelCompetitionValidResponseOneOutgoingNotification() {
        when(authManager.getUsername()).thenReturn("admin");

        String jsonInput = "[{\"id\":30,\"ownerId\":\"abc\",\"userId\":\"admin\",\"eventId\":33,\"position\":0}]";

        String expectedOutput = "===== You have 0 users that want to join your event ====="
                + "===== You have 1 notification ====="
                + "You have one pending request to an event with the ID 33 for position 0.";

        try {
            when(service.requestAllNotifications(any())).thenReturn(new MockHttpResponse(200, jsonInput));
            when(service.getNotifications(any())).thenCallRealMethod();

            assertThat(service.getNotifications(authManager)).isEqualToIgnoringNewLines(expectedOutput);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testValidModelCompetitionValidResponseOneIncomingNotification() {
        when(authManager.getUsername()).thenReturn("admin");

        String jsonInput = "[{\"id\":33,\"ownerId\":\"admin\",\"userId\":\"admin\",\"eventId\":33,\"position\":0}]";

        String expectedOutput = "===== You have 1 user that want to join your event ====="
                + "Notification ID 33: The user \"admin\" is requesting to join your event with the ID 33 for position 0!"
                + "===== You have 0 notifications =====";

        try {
            when(service.requestAllNotifications(any())).thenReturn(new MockHttpResponse(200, jsonInput));
            when(service.getNotifications(any())).thenCallRealMethod();

            assertThat(service.getNotifications(authManager)).isEqualToIgnoringNewLines(expectedOutput);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testValidModelCompetitionValidResponse() {
        when(authManager.getUsername()).thenReturn("admin");

        String jsonInput = "[{\"id\":30,\"ownerId\":\"abc\",\"userId\":\"admin\",\"eventId\":33,\"position\":0},"
                + "{\"id\":31,\"ownerId\":\"\",\"userId\":\"admin\",\"eventId\":33,\"position\":0},"
                + "{\"id\":32,\"ownerId\":\"admin\",\"userId\":\"abc\",\"eventId\":33,\"position\":0},"
                + "{\"id\":33,\"ownerId\":\"admin\",\"userId\":\"admin\",\"eventId\":33,\"position\":0}]";

        String expectedOutput = "===== You have 2 users that want to join your event ====="
                + "Notification ID 33: The user \"admin\" is requesting to join your event with the ID 33 "
                + "for position 0!"
                + "Notification ID 32: The user \"abc\" is requesting to join your event with the ID 33 "
                + "for position 0!"
                + "===== You have 2 notifications ====="
                + "You have have been accepted to the event with the ID 33 for position 0."
                + "You have one pending request to an event with the ID 33 for position 0.";

        try {
            when(service.requestAllNotifications(any())).thenReturn(new MockHttpResponse(200, jsonInput));
            when(service.getNotifications(any())).thenCallRealMethod();

            assertThat(service.getNotifications(authManager)).isEqualToIgnoringNewLines(expectedOutput);
        } catch (Exception e) {
            fail();
        }
    }
}
