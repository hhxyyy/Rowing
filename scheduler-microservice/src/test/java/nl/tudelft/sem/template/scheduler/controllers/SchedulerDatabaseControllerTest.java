package nl.tudelft.sem.template.scheduler.controllers;

import nl.tudelft.sem.template.scheduler.authentication.AuthManager;
import nl.tudelft.sem.template.scheduler.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.scheduler.domains.Match;
import nl.tudelft.sem.template.scheduler.domains.Notification;
import nl.tudelft.sem.template.scheduler.models.CustomPair;
import nl.tudelft.sem.template.scheduler.repos.MatchRepository;
import nl.tudelft.sem.template.scheduler.repos.NotificationRepository;
import nl.tudelft.sem.template.scheduler.services.CommunicationHandlerImpl;
import nl.tudelft.sem.template.scheduler.services.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@AutoConfigureMockMvc
public class SchedulerDatabaseControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @MockBean
    EventServiceImpl service;

    @MockBean
    MatchRepository matchRepository;

    @MockBean
    NotificationRepository notificationRepository;

    @MockBean
    CommunicationHandlerImpl handler;

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
    void getOwnerDecisionNotFound() throws Exception {
        when(notificationRepository.findNotificationById(7L)).thenReturn(Optional.empty());
        ResultActions result = mockMvc.perform(get("/getOwnerDecision/15/true")
                .header("Authorization", "Bearer MockedToken"));
        result.andExpect(status().isNotFound());

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Notification not found!");
    }

    @Test
    void getOwnerDecisionBadRequest() throws Exception {
        when(notificationRepository.findNotificationById(7L))
                .thenReturn(Optional.of(new Notification("owner", "ExampleUser", 15L, 4)));
        when(handler.getEventOwner(15L)).thenReturn("user");
        ResultActions result = mockMvc.perform(get("/getOwnerDecision/7/false")
                .header("Authorization", "Bearer MockedToken"));
        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("You are the owner of this event!");
    }

    @Test
    void getOwnerDecisionPresentMatchNegative() throws Exception {
        when(notificationRepository.findNotificationById(7L))
                .thenReturn(Optional.of(new Notification("ExampleUser", "user", 15L, 4)));
        when(handler.getEventOwner(15L)).thenReturn("ExampleUser");
        Match match = new Match("user", 15L, 4, true);
        when(matchRepository.findByUserIdAndEventId("user", 15L))
                .thenReturn(Optional.of(match));
        ResultActions result = mockMvc.perform(get("/getOwnerDecision/7/false")
                .header("Authorization", "Bearer MockedToken"));
        result.andExpect(status().isOk());

        verify(matchRepository).deleteById(match.getId());
        verify(notificationRepository, times(0))
                .save(new Notification(null, "user", 15L, 4));
        verify(matchRepository, times(0))
                .save(new Match("user", 15L, 4, false));

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("The user will be announced regarding your decision.");
    }

    @Test
    void getOwnerDecisionPresentMatchPositive() throws Exception {
        when(notificationRepository.findNotificationById(7L))
                .thenReturn(Optional.of(new Notification("ExampleUser", "user", 15L, 4)));
        when(handler.getEventOwner(15L)).thenReturn("ExampleUser");
        Match match = new Match("user", 15L, 4, true);
        when(matchRepository.findByUserIdAndEventId("user", 15L))
                .thenReturn(Optional.of(match));
        ResultActions result = mockMvc.perform(get("/getOwnerDecision/7/true")
                .header("Authorization", "Bearer MockedToken"));
        result.andExpect(status().isOk());

        verify(matchRepository, times(1)).deleteById(match.getId());
        verify(notificationRepository).save(new Notification(null, "user", 15L, 4));
        verify(matchRepository).save(new Match("user", 15L, 4, false));

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("The user will be announced regarding your decision.");
    }
}
