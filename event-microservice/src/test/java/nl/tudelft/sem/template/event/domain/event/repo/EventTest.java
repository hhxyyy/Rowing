package nl.tudelft.sem.template.event.domain.event.repo;

import nl.tudelft.sem.template.event.authentication.AuthManager;
import nl.tudelft.sem.template.event.exceptions.InvalidModelException;
import nl.tudelft.sem.template.event.models.AddEventModel;
import nl.tudelft.sem.template.event.models.CustomPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles({"test", "mockAuthenticationManager"})
public class EventTest {

    @MockBean
    private transient AuthManager mockAuthenticationManager = mock(AuthManager.class);

    private Date before;
    private Date after;

    @BeforeEach
    public void setUp() {
        before = new Date(2022, Calendar.DECEMBER, 13, 4, 30);
        after = new Date(2022, Calendar.DECEMBER, 13, 5, 0);
    }

    @Test
    public void parseModelTraining() throws InvalidModelException {
        AddEventModel trainingModel = new AddEventModel("TRAINING", "location",
                new CustomPair<>(before, after), new HashMap<>() {{
                        put("Gender", "male");
                        put("Organization", "abc");
                    }}
        );
        Event event = new Event(Event.Type.TRAINING, "ExampleUser", "location",
                new CustomPair<>(before, after), new HashMap<>() {{
                        put("Gender", "male");
                        put("Organization", "abc");
                    }}
        );
        when(mockAuthenticationManager.getUserId()).thenReturn("ExampleUser");
        assertThat(Event.parseModel(trainingModel, mockAuthenticationManager)).isEqualTo(event);
    }

    @Test
    public void parseModelCompetition() throws InvalidModelException {
        AddEventModel competitionModel = new AddEventModel("COMPETITION", "location",
                new CustomPair<>(before, after), new HashMap<>() {{
                        put("Gender", "male");
                        put("Organization", "abc");
                    }}
        );
        Event event = new Event(Event.Type.COMPETITION, "ExampleUser", "location",
                new CustomPair<>(before, after), new HashMap<>() {{
                        put("Gender", "male");
                        put("Organization", "abc");
                    }}
        );
        when(mockAuthenticationManager.getUserId()).thenReturn("ExampleUser");
        assertThat(Event.parseModel(competitionModel, mockAuthenticationManager)).isEqualTo(event);
    }

    @Test
    public void parseModelCompetitionEndTimeBeforeStartTime() {
        AddEventModel competitionModel = new AddEventModel("COMPETITION", "location",
                new CustomPair<>(after, before), new HashMap<>() {{
                        put("Gender", "male");
                        put("Organization", "abc");
                    }}
        );

        when(mockAuthenticationManager.getUserId()).thenReturn("ExampleUser");
        assertThatThrownBy(() -> Event.parseModel(competitionModel, mockAuthenticationManager))
                .isInstanceOf(InvalidModelException.class)
                .hasMessage("Invalid timeframe given: end-time is before start-time");
    }

    @Test
    public void parseModelInvalidType() {
        AddEventModel competitionModel = new AddEventModel("MATCH", "location",
                new CustomPair<>(before, after), new HashMap<>() {{
                        put("Gender", "male");
                        put("Organization", "abc");
                    }}
        );

        when(mockAuthenticationManager.getUserId()).thenReturn("ExampleUser");
        assertThatThrownBy(() -> Event.parseModel(competitionModel, mockAuthenticationManager))
                .isInstanceOf(InvalidModelException.class);
    }

    @Test
    public void parseModelCompetitionWithoutGender() {
        AddEventModel competitionModel = new AddEventModel("COMPETITION", "location",
                new CustomPair<>(before, after), new HashMap<>() {{
                        put("dddddddd", "male");
                        put("Organization", "abc");
                    }}
        );

        when(mockAuthenticationManager.getUserId()).thenReturn("ExampleUser");
        assertThatThrownBy(() -> Event.parseModel(competitionModel, mockAuthenticationManager))
                .isInstanceOf(InvalidModelException.class)
                .hasMessage("Requirements did not include the gender");
    }

    @Test
    public void parseModelCompetitionWithoutOrganization() {
        AddEventModel competitionModel = new AddEventModel("COMPETITION", "location",
                new CustomPair<>(before, after), new HashMap<>() {{
                        put("Gender", "male");
                        put("oooooo", "abc");
                    }}
        );

        when(mockAuthenticationManager.getUserId()).thenReturn("ExampleUser");
        assertThatThrownBy(() -> Event.parseModel(competitionModel, mockAuthenticationManager))
                .isInstanceOf(InvalidModelException.class)
                .hasMessage("Requirements did not include the organization");
    }
}
