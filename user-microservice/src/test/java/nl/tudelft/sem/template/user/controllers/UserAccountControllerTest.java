package nl.tudelft.sem.template.user.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.user.authentication.AuthManager;
import nl.tudelft.sem.template.user.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.user.domain.user.User;
import nl.tudelft.sem.template.user.domain.user.UserRepository;
import nl.tudelft.sem.template.user.models.EventIdModel;
import nl.tudelft.sem.template.user.models.UserPersonalInformationSetUpModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@AutoConfigureMockMvc
public class UserAccountControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @MockBean
    private transient UserRepository userRepository;

    /**
     * Sets up the authorization and the variables that will be used in multiple tests.
     */
    @BeforeEach
    public void setUp() {

        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken("ExampleUser", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");

        Map<String, String> requirements = new HashMap<>();
        requirements.put("Certificate", "C4");
        requirements.put("Gender", "Male");

    }

    @Test
    public void testGetPersonalInfoNotAdded() throws Exception {

        User user = new User("lexi", "C4", "F", "TU Delft", true);

        when(mockAuthenticationManager.getUsername()).thenReturn(user.getUsername());
        when(userRepository.existsById("lexi")).thenReturn(false);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/getPersonalInfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isNotFound());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).contains("No personal information found for user \""
                    + user.getUsername() + "\".");
    }

    @Test
    public void testGetPersonalInfoAdded() throws Exception {

        User user = new User("lexi", "C4", "F", "TU Delft", true);

        Mockito.when(mockAuthenticationManager.getUsername()).thenReturn("lexi");
        Mockito.when(userRepository.existsById(anyString())).thenReturn(true);
        Mockito.when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

        ResultActions result = mockMvc.perform(get("/getPersonalInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).contains(user.toString());
    }

    @Test
    public void testGetPersonalEmpty() throws Exception {

        User user = new User("lexi", "C4", "F", "TU Delft", true);

        Mockito.when(mockAuthenticationManager.getUsername()).thenReturn("lexi");
        Mockito.when(userRepository.existsById(anyString())).thenReturn(true);
        Mockito.when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        ResultActions result = mockMvc.perform(get("/getPersonalInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isNotFound());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).contains("No personal information found for user \""
                + user.getUsername() + "\".");
    }

    @Test
    public void testSetPersonalInfoAdded() throws Exception {

        User user = new User("lexi", "C4", "F", "TU Delft", true);

        UserPersonalInformationSetUpModel userPersonalInformationSetUpModel =
                new UserPersonalInformationSetUpModel("C4", "F", "TU Delft", true);

        when(mockAuthenticationManager.getUsername()).thenReturn("lexi");

        ResultActions result = mockMvc.perform(post("/setPersonalInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(userPersonalInformationSetUpModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        verify(userRepository).save(user);

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Personal information was successfully stored.");
    }

    @Test
    public void testSetPersonalInfoBadGender() throws Exception {

        UserPersonalInformationSetUpModel userPersonalInformationSetUpModel =
                new UserPersonalInformationSetUpModel("C4", "female", "TU Delft", true);

        when(mockAuthenticationManager.getUsername()).thenReturn("lexi");

        ResultActions result = mockMvc.perform(post("/setPersonalInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(userPersonalInformationSetUpModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).contains("You entered invalid personal information");
    }

    @Test
    public void testSetPersonalInfoBadCertificate() throws Exception {

        UserPersonalInformationSetUpModel userPersonalInformationSetUpModel =
                new UserPersonalInformationSetUpModel("C4", "female", "TU Delft", true);

        when(mockAuthenticationManager.getUsername()).thenReturn("lexi");

        ResultActions result = mockMvc.perform(post("/setPersonalInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(userPersonalInformationSetUpModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).contains("You entered invalid personal information");
    }

    @Test
    public void testSetPersonalInfoBadOrganization() throws Exception {

        UserPersonalInformationSetUpModel userPersonalInformationSetUpModel =
                new UserPersonalInformationSetUpModel("C4", "female", "TU Delft", true);

        when(mockAuthenticationManager.getUsername()).thenReturn("lexi");

        ResultActions result = mockMvc.perform(post("/setPersonalInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(userPersonalInformationSetUpModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).contains("You entered invalid personal information");
    }

    @Test
    public void testSetPersonalInfoEmpty() throws Exception {

        when(mockAuthenticationManager.getUsername()).thenReturn("lexi");

        ResultActions result = mockMvc.perform(post("/setPersonalInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).contains("");
    }

    @Test
    public void testSetPersonalInfoException() throws Exception {

        UserPersonalInformationSetUpModel userPersonalInformationSetUpModel =
                new UserPersonalInformationSetUpModel("C4", "F", "TU Delft", true);

        when(mockAuthenticationManager.getUsername()).thenReturn("lexi");
        IllegalStateException e = new IllegalStateException("error");
        when(userRepository.save(any(User.class))).thenThrow(e);
        ResultActions result = mockMvc.perform(post("/setPersonalInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(userPersonalInformationSetUpModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

    }

    @Test
    void pickEventTestBadModel() throws Exception {
        EventIdModel eventIdModel = new EventIdModel();
        eventIdModel.setEventId(0);
        eventIdModel.setPosition(-1);

        String json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(eventIdModel);

        ResultActions result = mockMvc.perform(post("/pickEvent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());
    }
}
