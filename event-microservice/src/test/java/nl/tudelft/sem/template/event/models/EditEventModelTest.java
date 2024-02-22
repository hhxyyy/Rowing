package nl.tudelft.sem.template.event.models;

import nl.tudelft.sem.template.event.domain.event.repo.Event;
import nl.tudelft.sem.template.event.models.CustomPair;
import nl.tudelft.sem.template.event.models.EditEventModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class EditEventModelTest {

    private Date before;
    private Date after;

    @BeforeEach
    public void setUp() {
        before = new Date(2022, Calendar.DECEMBER, 13, 4, 30);
        after = new Date(2022, Calendar.DECEMBER, 13, 5, 0);
    }

    @Test
    public void testModelValid() {
        EditEventModel editEventModel = new EditEventModel(Event.Type.TRAINING,
                "location", new CustomPair<>(before, after), new HashMap<>() {{
                        put("gender", "male");
                        put("organization", "abc");
                    }}
        );
        assertThat(editEventModel.isValid()).isTrue();
    }

    @Test
    public void testModelInvalid() {
        EditEventModel editEventModel = new EditEventModel(null, null, null, null);
        assertThat(editEventModel.isValid()).isFalse();
    }

    @Test
    public void testModelOnlyType() {
        EditEventModel editEventModel = new EditEventModel(Event.Type.TRAINING, null, null, null);
        assertThat(editEventModel.isValid()).isTrue();
    }

    @Test
    public void testModelOnlyLocation() {
        EditEventModel editEventModel = new EditEventModel(null, "location", null, null);
        assertThat(editEventModel.isValid()).isTrue();
    }

    @Test
    public void testModelOnlyTimeFrame() {
        EditEventModel editEventModel = new EditEventModel(null, null,
                new CustomPair<>(before, after), null);
        assertThat(editEventModel.isValid()).isTrue();
    }

    @Test
    public void testModelOnlyRequirement() {
        EditEventModel editEventModel = new EditEventModel(null, null, null, new HashMap<>() {{
                put("gender", "male");
                put("organization", "abc");
            }}
        );
        assertThat(editEventModel.isValid()).isTrue();
    }
}
