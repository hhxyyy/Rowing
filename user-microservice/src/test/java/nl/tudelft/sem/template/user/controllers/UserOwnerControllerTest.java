package nl.tudelft.sem.template.user.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import nl.tudelft.sem.template.user.authentication.AuthManager;
import nl.tudelft.sem.template.user.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.user.domain.CustomPair;
import nl.tudelft.sem.template.user.exceptions.InvalidModelException;
import nl.tudelft.sem.template.user.models.EventModel;
import nl.tudelft.sem.template.user.services.OwnerService;
import nl.tudelft.sem.template.user.util.MockHttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@AutoConfigureMockMvc
public class UserOwnerControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    private Date before;
    private Date after;
    @MockBean
    private OwnerService ownerService;
    private Map<String, String> requirements;

    /**
     * Sets up basic utilities used for testing.
     */
    @BeforeEach
    public void setup() {
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken("ExampleUser", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");

        this.requirements = new HashMap<>();
        this.requirements.put("Certificate", "C4");
        this.requirements.put("Gender", "M ale");

        before = new Date(2022, Calendar.DECEMBER, 13, 4, 30);

        after = new Date(2022, Calendar.DECEMBER, 13, 5, 0);

        boolean[] positions = new boolean[]{true, false, false, false, false};

    }

    @Test
    void createEventTestInvalidModel() throws Exception {
        EventModel eventModel = new EventModel("Competition", "location",
                new CustomPair<>(before, after), requirements);

        String json = new ObjectMapper()
                .writerWithDefaultPrettyPrinter().writeValueAsString(eventModel);

        when(ownerService.createEvent(eventModel)).thenThrow(new InvalidModelException("msg"));

        ResultActions result = mockMvc.perform(post("/createEvent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());
    }

    @Test
    void createEventTest() throws Exception {
        EventModel eventModel = new EventModel("Competition", "location",
                new CustomPair<>(before, after), requirements);

        String json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(eventModel);

        when(ownerService.createEvent(eventModel)).thenReturn(new MockHttpResponse(200, json));

        ResultActions result = mockMvc.perform(post("/createEvent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("Authorization", "Bearer MockedToken"));
        assertFalse(result.andReturn().getResponse().getContentAsString().isEmpty());
        result.andExpect(status().isOk());
    }

    @Test
    void editEventTestInvalidModel() throws Exception {
        EventModel eventModel = new EventModel("competition", "location",
                new CustomPair<>(before, after), requirements);

        String json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(eventModel);

        when(ownerService.editEvent(1, eventModel)).thenThrow(new InvalidModelException("msg"));

        ResultActions result = mockMvc.perform(patch("/editEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());
    }

    @Test
    void editEventTest() throws Exception {
        EventModel eventModel = new EventModel("competition", "location",
                new CustomPair<>(before, after), requirements);

        String json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(eventModel);

        when(ownerService.editEvent(1, eventModel)).thenReturn(new MockHttpResponse(200, json));

        ResultActions result = mockMvc.perform(patch("/editEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("Authorization", "Bearer MockedToken"));
        assertFalse(result.andReturn().getResponse().getContentAsString().isEmpty());

        result.andExpect(status().isOk());
    }

    @Test
    void cancelEventTest() throws Exception {
        when(ownerService.cancelEvent(1)).thenReturn(new MockHttpResponse(200, ""));

        ResultActions result = mockMvc.perform(delete("/cancelEvent/" + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));
        assertNotNull(result.andReturn().getResponse());
        result.andExpect(status().isOk());
    }
}
