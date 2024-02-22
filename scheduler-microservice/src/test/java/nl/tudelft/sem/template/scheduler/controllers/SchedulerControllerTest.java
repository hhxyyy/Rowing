package nl.tudelft.sem.template.scheduler.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import nl.tudelft.sem.template.scheduler.authentication.AuthManager;
import nl.tudelft.sem.template.scheduler.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.scheduler.domains.Match;
import nl.tudelft.sem.template.scheduler.domains.Notification;
import nl.tudelft.sem.template.scheduler.models.CustomPair;
import nl.tudelft.sem.template.scheduler.models.EventModel;
import nl.tudelft.sem.template.scheduler.models.NewCertificateModel;
import nl.tudelft.sem.template.scheduler.models.UserRequest;
import nl.tudelft.sem.template.scheduler.repos.MatchRepository;
import nl.tudelft.sem.template.scheduler.repos.NotificationRepository;
import nl.tudelft.sem.template.scheduler.services.AuthenticationHandler;
import nl.tudelft.sem.template.scheduler.services.CommunicationHandlerImpl;
import nl.tudelft.sem.template.scheduler.services.EventServiceImpl;
import nl.tudelft.sem.template.scheduler.validators.CertificateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@AutoConfigureMockMvc
class SchedulerControllerTest {
    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @MockBean
    EventServiceImpl service;

    @MockBean
    CommunicationHandlerImpl handler;

    @MockBean
    MatchRepository matchRepository;

    @MockBean
    AuthenticationHandler authenticationHandler;

    @MockBean
    NotificationRepository notificationRepository;

    Map<String, String> requirements;
    CustomPair<Date, Date> timeFrame;
    boolean[] positions = {false, false, true, true, false};

    @BeforeEach
    void initialize() {
        when(mockAuthenticationManager.getUserId()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken("ExampleUser", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        this.requirements = new HashMap<>();
        this.requirements.put("Certificate", "C4");
        this.requirements.put("Gender", "Male");

        Date d1 = new Date(2022, 11, 27, 14, 0, 0);
        Date d2 = new Date(2022, 11, 27, 20, 0, 0);

        this.timeFrame = new CustomPair<>(d1, d2);
    }

    @Test
    void matchUserWithEventEmptyTest() throws Exception {
        when(service.getAllEvents()).thenReturn(List.of());

        List<CustomPair<Date, Date>> availability = new ArrayList<>();
        availability.add(timeFrame);

        UserRequest userRequest = new UserRequest("user", availability, positions, requirements);
        String json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(userRequest);

        when(service.parseUserRequest(json)).thenCallRealMethod();

        ResultActions result = mockMvc.perform(post("/match")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());
    }

    @Test
    void matchUserWithEventBadRequest() throws Exception {
        when(service.getAllEvents()).thenReturn(List.of());

        String json = "abc";
        when(service.parseUserRequest(json)).thenCallRealMethod();

        ResultActions result = mockMvc.perform(post("/match")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());
    }

    @Test
    void matchUserWithEvent() throws Exception {
        String json = "mock json";
        when(service.parseUserRequest(json)).thenReturn(new UserRequest());
        when(service.getAllEvents()).thenReturn(List.of());

        when(service.filterEvents(any(), any())).thenReturn(List.of(new CustomPair<>(new EventModel(1, "owner",
                new CustomPair<>(new Date(1924257600000L), new Date(1924282800000L)), EventModel.Type.COMPETITION,
                Map.of(), "loc"), 1)));

        ResultActions result = mockMvc.perform(post("/match")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        assertThat(result.andReturn().getResponse().getContentAsString())
                .isEqualToIgnoringWhitespace("[{\"first\":{\"id\":1,\"ownerId\":\"owner\",\"timeFrame\":{\"first\""
                                             + ":1924257600000,\"second\":1924282800000},\"type\":\"COMPETITION\","
                                             + "\"requirements\":{},\"location\":\"loc\"},\"second\":1}]");
    }

    @Test
    void getUserChoiceBadRequest() throws Exception {
        ResultActions result = mockMvc.perform(get("/getChoice/user/15/0")
            .header("Authorization", "Bearer MockedToken"));
        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("You are allowed to chose only for yourself maximum 1 position!");
    }

    @Test
    void getUserChoiceBadRequest2() throws Exception {
        when(matchRepository.findByUserIdAndEventId("ExampleUser", 15L))
            .thenReturn(Optional.of(new Match("ExampleUser", 15L, 4, true)));

        ResultActions result = mockMvc.perform(get("/getChoice/ExampleUser/15/0")
            .header("Authorization", "Bearer MockedToken"));
        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("You are allowed to chose only for yourself maximum 1 position!");
    }

    @Test
    void getUserChoiceValid() throws Exception {
        when(matchRepository.findByUserIdAndEventId("ExampleUser", 15L))
            .thenReturn(Optional.empty());
        when(handler.getEventOwner(15)).thenReturn("owner");
        ResultActions result = mockMvc.perform(get("/getChoice/ExampleUser/15/4")
            .header("Authorization", "Bearer MockedToken"));

        verify(matchRepository).save(new Match("ExampleUser", 15L, 4, true));
        verify(notificationRepository).save(new Notification("owner", "ExampleUser", 15L, 4));
        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Your choice was registered. "
                + "You will receive a notification in case the request is approved.");
    }

    @Test
    void getUserChoiceInvalidEvent() throws Exception {
        when(matchRepository.findByUserIdAndEventId("ExampleUser", 15L))
            .thenReturn(Optional.empty());
        when(handler.getEventOwner(15)).thenThrow(new IOException());

        ResultActions result = mockMvc.perform(get("/getChoice/ExampleUser/15/4")
            .header("Authorization", "Bearer MockedToken"));

        verify(matchRepository).save(new Match("ExampleUser", 15L, 4, true));
        result.andExpect(status().isNotFound());

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Event not found");
    }

    @Test
    void getNotificationsBadRequest() throws Exception {
        ResultActions result = mockMvc.perform(get("/getNotifications/user")
            .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isUnauthorized());

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("You cannot get notification for others!");
    }

    @Test
    void getNotificationsEmpty() throws Exception {
        ResultActions result = mockMvc.perform(get("/getNotifications/ExampleUser")
            .header("Authorization", "Bearer MockedToken"));
        when(notificationRepository.findNotificationByUserId("ExampleUser")).thenReturn(Optional.empty());

        result.andExpect(status().isNotFound());

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("No notifications were found!");
    }

    @Test
    void getNotificationsOk() throws Exception {

        List<Notification> notifications = List.of(
            new Notification("owner", "ExampleUser", 15L, 4));
        when(notificationRepository.findNotificationByUserId("ExampleUser")).thenReturn(Optional.of(notifications));

        ResultActions result = mockMvc.perform(get("/getNotifications/ExampleUser")
            .header("Authorization", "Bearer MockedToken"));
        result.andExpect(status().isOk());

        String expectedResponse = new ObjectMapper().writerWithDefaultPrettyPrinter()
            .writeValueAsString(notifications);

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void testAddCertificateInvalidModel() throws Exception {
        NewCertificateModel model = new NewCertificateModel(null, Set.of("B", "C"));

        when(authenticationHandler.isAdmin()).thenReturn(true);

        ObjectWriter mapper = new ObjectMapper().writerWithDefaultPrettyPrinter();
        ResultActions result = mockMvc.perform(post("/addCertificate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isNotBlank();
    }


    @Test
    void testAddCertificateUnauthorized() throws Exception {
        NewCertificateModel model = new NewCertificateModel("A", Set.of("B", "C"));

        when(authenticationHandler.isAdmin()).thenReturn(false);

        ObjectWriter mapper = new ObjectMapper().writerWithDefaultPrettyPrinter();
        ResultActions result = mockMvc.perform(post("/addCertificate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isUnauthorized());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isBlank();
    }

    @Test
    void testAddCertificateValid() throws Exception {
        NewCertificateModel model = new NewCertificateModel("A", Set.of("B", "C"));

        when(authenticationHandler.isAdmin()).thenReturn(true);

        ObjectWriter mapper = new ObjectMapper().writerWithDefaultPrettyPrinter();
        ResultActions result = mockMvc.perform(post("/addCertificate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(model))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).containsIgnoringWhitespaces("\"A\":[\"A\",\"B\",\"C\"]");
    }
}
