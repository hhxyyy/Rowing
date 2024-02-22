package nl.tudelft.sem.template.event.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.event.authentication.AuthManager;
import nl.tudelft.sem.template.event.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.event.domain.event.repo.EventRepository;
import nl.tudelft.sem.template.event.domain.event.repo.Event;
import nl.tudelft.sem.template.event.integration.utils.JsonUtil;
import nl.tudelft.sem.template.event.models.AddEventModel;
import nl.tudelft.sem.template.event.models.CustomPair;
import nl.tudelft.sem.template.event.models.EditEventModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @MockBean
    private transient EventRepository eventRepository;

    private Date before;
    private Date after;
    private Date before2;
    private Date after2;

    @BeforeEach
    public void setup() {
        when(mockAuthenticationManager.getUserId()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");
        before = new Date(2022, Calendar.DECEMBER, 13, 4, 30);
        after = new Date(2022, Calendar.DECEMBER, 13, 5, 0);
        before2 = new Date(2022, Calendar.DECEMBER, 14, 4, 30);
        after2 = new Date(2022, Calendar.DECEMBER, 14, 5, 0);
    }

    @Test
    public void testGetAllEventsEmpty() throws Exception {
        ResultActions result = mockMvc.perform(get("/getAllEvents")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("[ ]");
    }

    @Test
    public void testGetAllEvents() throws Exception {
        Event trainingEvent = new Event(Event.Type.TRAINING, "ownerId",
                "location", new CustomPair<>(before, after), new HashMap<>());
        Event competitionEvent = new Event(Event.Type.TRAINING, "ownerId",
                "location", new CustomPair<>(before2, after2), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        when(eventRepository.findAll()).thenReturn(List.of(trainingEvent, competitionEvent));

        ResultActions result = mockMvc.perform(get("/getAllEvents")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo(new ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(List.of(trainingEvent, competitionEvent)));
    }

    @Test
    public void testGetEventInvalidParameter() throws Exception {
        when(eventRepository.findById(0)).thenReturn(Optional.empty());

        ResultActions result = mockMvc.perform(get("/getEvent/0")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Invalid eventId");
    }

    @Test
    public void testGetEventDoesntExist() throws Exception {
        when(eventRepository.findById(1)).thenReturn(Optional.empty());

        ResultActions result = mockMvc.perform(get("/getEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isNotFound());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isBlank();
    }

    @Test
    public void testGetEventExists() throws Exception {
        Event competitionEvent = new Event(Event.Type.TRAINING, "ownerId",
                "location", new CustomPair<>(before, after), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        when(eventRepository.existsById(1)).thenReturn(true);
        when(eventRepository.findById(1)).thenReturn(Optional.of(competitionEvent));

        ResultActions result = mockMvc.perform(get("/getEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualToIgnoringWhitespace(JsonUtil.serialize(competitionEvent));
    }

    @Test
    public void testGetOwnerInvalidParameter() throws Exception {
        when(eventRepository.findById(1)).thenReturn(Optional.empty());

        ResultActions result = mockMvc.perform(get("/getEventOwner/0")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Invalid eventId");
    }

    @Test
    public void testGetOwnerEventDoesntExist() throws Exception {
        when(eventRepository.findById(1)).thenReturn(Optional.empty());

        ResultActions result = mockMvc.perform(get("/getEventOwner/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isNotFound());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isBlank();
    }

    @Test
    public void testGetOwnerEventExists() throws Exception {
        Event competitionEvent = new Event(Event.Type.TRAINING, "ownerId",
                "location", new CustomPair<>(before, after), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        when(eventRepository.existsById(1)).thenReturn(true);
        when(eventRepository.findById(1)).thenReturn(Optional.of(competitionEvent));

        ResultActions result = mockMvc.perform(get("/getEventOwner/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("ownerId");
    }

    @Test
    public void testAddEvent() throws Exception {
        AddEventModel addEventModel = new AddEventModel("TRAINING", "location",
                new CustomPair<>(before, after), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        Event event = new Event(Event.Type.TRAINING, "ownerId",
                "location", new CustomPair<>(before, after), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        Event e = Event.parseModel(addEventModel, mockAuthenticationManager);

        when(eventRepository.save(e)).thenReturn(event);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/addEvent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(addEventModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isCreated());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualToIgnoringWhitespace(JsonUtil.serialize(event));
    }

    @Test
    public void testAddEventInvalidModel() throws Exception {
        AddEventModel addEventModel = new AddEventModel("", "location",
                new CustomPair<>(before, after), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/addEvent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(addEventModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isNotBlank();
    }

    @Test
    public void testAddEventSaveError() throws Exception {
        AddEventModel addEventModel = new AddEventModel("TRAINING", "location",
                new CustomPair<>(before, after), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        Event e = Event.parseModel(addEventModel, mockAuthenticationManager);

        when(eventRepository.save(e)).thenThrow(new IllegalArgumentException());

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/addEvent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(addEventModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isInternalServerError());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isBlank();
    }

    @Test
    public void testAddEventParseError() throws Exception {
        AddEventModel addEventModel = new AddEventModel("COMPETITION", "location",
                new CustomPair<>(before, after), new HashMap<>() {{
                        put("ddddddddender", "male");
                        put("organization", "abc");
                    }}
        );

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/addEvent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(addEventModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isNotBlank();
    }

    @Test
    public void testDeleteEvent() throws Exception {
        Event event = new Event(Event.Type.TRAINING, "ownerId",
                "location", new CustomPair<>(before, after), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        when(eventRepository.existsById(1)).thenReturn(true);
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        doNothing().when(eventRepository).deleteById(1L);

        when(mockAuthenticationManager.getUserId()).thenReturn("ownerId");

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/deleteEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));


        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isBlank();
    }

    @Test
    public void testDeleteEventInvalidParameterNegative() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/deleteEvent/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Invalid eventId");
    }

    @Test
    public void testDeleteEventInvalidParameterZero() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/deleteEvent/0")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Invalid eventId");
    }

    @Test
    public void testDeleteEventException() throws Exception {
        doThrow(new IllegalArgumentException()).when(eventRepository).deleteById(2L);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/deleteEvent/2")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isNotFound());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isBlank();
    }

    @Test
    public void testDeleteEventUnauthorized() throws Exception {
        Event event = new Event(Event.Type.TRAINING, "notTheOwnersId",
                "location", new CustomPair<>(before, after), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        when(eventRepository.existsById(1)).thenReturn(true);
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        doNothing().when(eventRepository).deleteById(1L);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/deleteEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isUnauthorized());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isBlank();
    }

    @Test
    public void testEditEventEventIdZero() throws Exception {
        EditEventModel editEventModel = new EditEventModel(Event.Type.TRAINING,
                "location", new CustomPair<>(before, after), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        when(mockAuthenticationManager.getUserId()).thenReturn("ownerId");

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/editEvent/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(editEventModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Invalid eventId: must be equal to or higher than 1.");
    }

    @Test
    public void testEditEventEventIdNegative() throws Exception {
        EditEventModel editEventModel = new EditEventModel(Event.Type.TRAINING,
                "location", new CustomPair<>(before, after), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        when(mockAuthenticationManager.getUserId()).thenReturn("ownerId");

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/editEvent/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(editEventModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Invalid eventId: must be equal to or higher than 1.");
    }

    @Test
    public void testEditEventEventNotFound() throws Exception {
        EditEventModel editEventModel = new EditEventModel(Event.Type.TRAINING,
                "location", new CustomPair<>(before, after), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        when(eventRepository.existsById(1)).thenReturn(false);
        when(mockAuthenticationManager.getUserId()).thenReturn("ownerId");

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/editEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(editEventModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void testEditEventInvalidModel() throws Exception {
        EditEventModel editEventModel = new EditEventModel(null, null, null, null);

        Event origin = new Event(Event.Type.COMPETITION, "ownerId",
                "origin", new CustomPair<>(before, after), new HashMap<>() {{
                        put("originGender", "female");
                        put("originOrganization", "def");
                    }}
        );

        Event edited = new Event(Event.Type.COMPETITION, "ownerId",
                "origin", new CustomPair<>(before, after), new HashMap<>() {{
                        put("originGender", "female");
                        put("originOrganization", "def");
                    }}
        );

        when(eventRepository.existsById(1)).thenReturn(true);
        when(eventRepository.findById(1)).thenReturn(Optional.of(origin));
        when(eventRepository.save(edited)).thenReturn(edited);
        when(mockAuthenticationManager.getUserId()).thenReturn("ownerId");

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/editEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(editEventModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());
    }

    @Test
    public void testEditEventRequirements() throws Exception {
        EditEventModel editEventModel = new EditEventModel(Event.Type.COMPETITION,
                null, null, new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        Event origin = new Event(Event.Type.COMPETITION, "ownerId",
                "origin", new CustomPair<>(before, after), new HashMap<>() {{
                        put("originGender", "female");
                        put("originOrganization", "def");
                    }}
        );

        Event edited = new Event(Event.Type.COMPETITION, "ownerId",
                "origin", new CustomPair<>(before, after), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        when(eventRepository.existsById(1)).thenReturn(true);
        when(eventRepository.findById(1)).thenReturn(Optional.of(origin));
        when(eventRepository.save(edited)).thenReturn(edited);
        when(mockAuthenticationManager.getUserId()).thenReturn("ownerId");

        String editedJson = new ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(edited);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/editEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(editEventModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo(editedJson);
    }

    @Test
    public void testEditEventType() throws Exception {
        EditEventModel editEventModel = new EditEventModel(Event.Type.TRAINING, null, null, null);

        Event origin = new Event(Event.Type.COMPETITION, "ownerId",
                "location", new CustomPair<>(before, after), new HashMap<>() {{
                        put("originGender", "female");
                        put("originOrganization", "def");
                    }}
        );

        Event edited = new Event(Event.Type.TRAINING, "ownerId",
                "location", new CustomPair<>(before, after), new HashMap<>() {{
                        put("originGender", "female");
                        put("originOrganization", "def");
                    }}
        );

        when(eventRepository.existsById(1)).thenReturn(true);
        when(eventRepository.findById(1)).thenReturn(Optional.of(origin));
        when(eventRepository.save(edited)).thenReturn(edited);
        when(mockAuthenticationManager.getUserId()).thenReturn("ownerId");

        String editedJson = new ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(edited);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/editEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(editEventModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo(editedJson);
    }

    @Test
    public void testEditEventLocation() throws Exception {
        EditEventModel editEventModel = new EditEventModel(null, "location", null, null);

        Event origin = new Event(Event.Type.COMPETITION, "ownerId",
                "origin", new CustomPair<>(before, after), new HashMap<>() {{
                        put("originGender", "female");
                        put("originOrganization", "def");
                    }}
        );

        Event edited = new Event(Event.Type.COMPETITION, "ownerId",
                "location", new CustomPair<>(before, after), new HashMap<>() {{
                        put("originGender", "female");
                        put("originOrganization", "def");
                    }}
        );

        when(eventRepository.existsById(1)).thenReturn(true);
        when(eventRepository.findById(1)).thenReturn(Optional.of(origin));
        when(eventRepository.save(edited)).thenReturn(edited);
        when(mockAuthenticationManager.getUserId()).thenReturn("ownerId");

        String editedJson = new ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(edited);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/editEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(editEventModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo(editedJson);
    }

    @Test
    public void testEditEventTimeframe() throws Exception {
        EditEventModel editEventModel = new EditEventModel(null, null, new CustomPair<>(before2, after2), null);

        Event origin = new Event(Event.Type.COMPETITION, "ownerId",
                "origin", new CustomPair<>(before, after), new HashMap<>() {{
                        put("originGender", "female");
                        put("originOrganization", "def");
                    }}
        );

        Event edited = new Event(Event.Type.COMPETITION, "ownerId",
                "origin", new CustomPair<>(before2, after2), new HashMap<>() {{
                        put("originGender", "female");
                        put("originOrganization", "def");
                    }}
        );

        when(eventRepository.existsById(1)).thenReturn(true);
        when(eventRepository.findById(1)).thenReturn(Optional.of(origin));
        when(eventRepository.save(edited)).thenReturn(edited);
        when(mockAuthenticationManager.getUserId()).thenReturn("ownerId");

        String editedJson = new ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(edited);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/editEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(editEventModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo(editedJson);
    }

    @Test
    public void testEditEventAll() throws Exception {
        EditEventModel editEventModel = new EditEventModel(Event.Type.COMPETITION,
                "location", new CustomPair<>(before2, after2), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        Event origin = new Event(Event.Type.COMPETITION, "ownerId",
                "origin", new CustomPair<>(before, after), new HashMap<>() {{
                        put("originGender", "female");
                        put("originOrganization", "def");
                    }}
        );

        Event edited = new Event(Event.Type.COMPETITION, "ownerId",
                "location", new CustomPair<>(before2, after2), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        when(eventRepository.existsById(1)).thenReturn(true);
        when(eventRepository.findById(1)).thenReturn(Optional.of(origin));
        when(eventRepository.save(edited)).thenReturn(edited);
        when(mockAuthenticationManager.getUserId()).thenReturn("ownerId");

        String editedJson = new ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(edited);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/editEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(editEventModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo(editedJson);
    }

    @Test
    public void testEditEventLocationBlank() throws Exception {
        EditEventModel editEventModel = new EditEventModel(Event.Type.COMPETITION,
                "", new CustomPair<>(before2, after2), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        Event origin = new Event(Event.Type.COMPETITION, "ownerId",
                "origin", new CustomPair<>(before, after), new HashMap<>() {{
                        put("originGender", "female");
                        put("originOrganization", "def");
                    }}
        );

        when(eventRepository.existsById(1)).thenReturn(true);
        when(eventRepository.findById(1)).thenReturn(Optional.of(origin));
        when(mockAuthenticationManager.getUserId()).thenReturn("ownerId");

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/editEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(editEventModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Invalid location given.");
    }

    @Test
    public void testEditEventTimeFrameInvalid() throws Exception {
        EditEventModel editEventModel = new EditEventModel(Event.Type.COMPETITION,
                "location", new CustomPair<>(after2, before2), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        Event origin = new Event(Event.Type.COMPETITION, "ownerId",
                "origin", new CustomPair<>(before, after), new HashMap<>() {{
                        put("originGender", "female");
                        put("originOrganization", "def");
                    }}
        );

        when(eventRepository.existsById(1)).thenReturn(true);
        when(eventRepository.findById(1)).thenReturn(Optional.of(origin));
        when(mockAuthenticationManager.getUserId()).thenReturn("ownerId");

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/editEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(editEventModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Invalid timeframe given.");
    }

    @Test
    public void testEditEventRequirementInvalidKey() throws Exception {
        EditEventModel editEventModel = new EditEventModel(Event.Type.COMPETITION,
                "location", new CustomPair<>(before2, after2), new HashMap<>() {{
                        put("", "male");
                        put("organization", "abc");
                    }}
        );

        Event origin = new Event(Event.Type.COMPETITION, "ownerId",
                "origin", new CustomPair<>(before, after), new HashMap<>() {{
                        put("originGender", "female");
                        put("originOrganization", "def");
                    }}
        );

        when(eventRepository.existsById(1)).thenReturn(true);
        when(eventRepository.findById(1)).thenReturn(Optional.of(origin));
        when(mockAuthenticationManager.getUserId()).thenReturn("ownerId");

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/editEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(editEventModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Invalid requirements given.");
    }

    @Test
    public void testEditEventRequirementInvalidValue() throws Exception {
        EditEventModel editEventModel = new EditEventModel(Event.Type.COMPETITION,
                "location", new CustomPair<>(before2, after2), new HashMap<>() {{
                        put("gender", "");
                        put("organization", "abc");
                    }}
        );

        Event origin = new Event(Event.Type.COMPETITION, "ownerId",
                "origin", new CustomPair<>(before, after), new HashMap<>() {{
                        put("originGender", "female");
                        put("originOrganization", "def");
                    }}
        );

        when(eventRepository.existsById(1)).thenReturn(true);
        when(eventRepository.findById(1)).thenReturn(Optional.of(origin));
        when(mockAuthenticationManager.getUserId()).thenReturn("ownerId");

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/editEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(editEventModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Invalid requirements given.");
    }

    @Test
    public void testEditEventIllegalArgumentException() throws Exception {
        EditEventModel editEventModel = new EditEventModel(Event.Type.COMPETITION,
                "location", new CustomPair<>(before2, after2), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        Event origin = new Event(Event.Type.COMPETITION, "ownerId",
                "origin", new CustomPair<>(before, after), new HashMap<>() {{
                        put("originGender", "female");
                        put("originOrganization", "def");
                    }}
        );

        Event edited = new Event(Event.Type.COMPETITION, "ownerId",
                "location", new CustomPair<>(before2, after2), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        when(eventRepository.existsById(1)).thenReturn(true);
        when(eventRepository.findById(1)).thenReturn(Optional.of(origin));
        when(eventRepository.save(edited)).thenThrow(new IllegalArgumentException());
        when(mockAuthenticationManager.getUserId()).thenReturn("ownerId");

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/editEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(editEventModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isInternalServerError());
    }

    @Test
    public void testEditEventUnauthorized() throws Exception {
        EditEventModel editEventModel = new EditEventModel(Event.Type.COMPETITION,
                "location", new CustomPair<>(before2, after2), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        Event origin = new Event(Event.Type.COMPETITION, "notTheOwnersId",
                "origin", new CustomPair<>(before, after), new HashMap<>() {{
                        put("originGender", "female");
                        put("originOrganization", "def");
                    }}
        );

        Event edited = new Event(Event.Type.COMPETITION, "notTheOwnersId",
                "location", new CustomPair<>(before2, after2), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );

        when(eventRepository.existsById(1)).thenReturn(true);
        when(eventRepository.findById(1)).thenReturn(Optional.of(origin));
        when(eventRepository.save(edited)).thenReturn(edited);
        when(mockAuthenticationManager.getUserId()).thenReturn("ownerId");

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/editEvent/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(editEventModel))
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isUnauthorized());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isBlank();
    }
}