package nl.tudelft.sem.template.event.domain;

import nl.tudelft.sem.template.event.domain.event.Builder;
import nl.tudelft.sem.template.event.domain.event.CompetitionEventBuilder;
import nl.tudelft.sem.template.event.domain.event.TrainingEventBuilder;
import nl.tudelft.sem.template.event.domain.event.repo.Event;
import nl.tudelft.sem.template.event.exceptions.InvalidModelException;
import nl.tudelft.sem.template.event.models.CustomPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class EventBuilderTest {
    private Date before;
    private Date after;

    @BeforeEach
    public void setup() {
        before = new Date(2022, Calendar.DECEMBER, 13, 4, 30);
        after = new Date(2022, Calendar.DECEMBER, 13, 5, 0);
    }

    @Test
    public void testCompetitionEventBuilderValid() {
        try {
            CustomPair<Date, Date> timeFrame = new CustomPair<>(before, after);
            Map<String, String> requirements = new HashMap<>() {{
                    put("Gender", "M");
                    put("Organization", "abc");
                }};

            Builder builder = new CompetitionEventBuilder();

            builder.setOwner("ownerId");
            builder.setLocation("location");
            builder.setTimeFrame(timeFrame);
            builder.addRequirements(requirements);

            Event result = builder.build();

            assertThat(result.getType()).isEqualTo(Event.Type.COMPETITION);
            assertThat(result.getId()).isEqualTo(0);
            assertThat(result.getOwnerId()).isEqualTo("ownerId");
            assertThat(result.getLocation()).isEqualTo("location");
            assertThat(result.getTimeFrame()).isEqualTo(timeFrame);
            assertThat(result.getRequirements()).isEqualTo(requirements);
        } catch (InvalidModelException e) {
            fail();
        }
    }

    @Test
    public void testTrainingEventBuilderValid() {
        try {
            CustomPair<Date, Date> timeFrame = new CustomPair<>(before, after);
            Map<String, String> requirements = new HashMap<>();

            Builder builder = new TrainingEventBuilder();

            builder.setOwner("ownerId");
            builder.setLocation("location");
            builder.setTimeFrame(timeFrame);
            builder.addRequirements(requirements);

            Event result = builder.build();

            assertThat(result.getType()).isEqualTo(Event.Type.TRAINING);
            assertThat(result.getId()).isEqualTo(0);
            assertThat(result.getOwnerId()).isEqualTo("ownerId");
            assertThat(result.getLocation()).isEqualTo("location");
            assertThat(result.getTimeFrame()).isEqualTo(timeFrame);
            assertThat(result.getRequirements()).isEqualTo(requirements);
        } catch (InvalidModelException e) {
            fail();
        }
    }

    @Test
    public void testEventBuilderInvalidOwnerIdNull() {
        assertThrows(InvalidModelException.class, () -> {
            Builder builder = new TrainingEventBuilder();

            builder.setOwner(null);
        });
    }

    @Test
    public void testEventBuilderInvalidOwnerIdBlank() {
        assertThrows(InvalidModelException.class, () -> {
            Builder builder = new TrainingEventBuilder();

            builder.setOwner(" ");
        });
    }

    @Test
    public void testEventBuilderInvalidLocationNull() {
        assertThrows(InvalidModelException.class, () -> {
            Builder builder = new TrainingEventBuilder();

            builder.setLocation(null);
        });
    }

    @Test
    public void testEventBuilderInvalidLocationBlank() {
        assertThrows(InvalidModelException.class, () -> {
            Builder builder = new TrainingEventBuilder();

            builder.setLocation(" ");
        });
    }

    @Test
    public void testEventBuilderInvalidTimeFrameNull() {
        assertThrows(InvalidModelException.class, () -> {
            Builder builder = new TrainingEventBuilder();

            builder.setTimeFrame(null);
        });
    }

    @Test
    public void testEventBuilderInvalidTimeFrameInvalid() {
        assertThrows(InvalidModelException.class, () -> {
            Builder builder = new TrainingEventBuilder();

            builder.setTimeFrame(new CustomPair<>(after, before));
        });
    }

    @Test
    public void testEventBuilderInvalidRequirementsNull() {
        try {
            Builder builder = new TrainingEventBuilder();
            builder.addRequirements(null);

            assertThat(builder.build().getRequirements()).isEmpty();

            builder = new CompetitionEventBuilder();
            builder.addRequirements(null);

            assertThat(builder.build().getRequirements()).isEmpty();
        } catch (InvalidModelException e) {
            fail();
        }
    }

    @Test
    public void testTrainingEventBuilderRequirementsInvalid() {
        assertThrows(InvalidModelException.class, () -> {
            Builder builder = new TrainingEventBuilder();

            builder.addRequirements(new HashMap<>() {{
                    put("not-blank", " ");
                }}
            );
        });

        assertThrows(InvalidModelException.class, () -> {
            Builder builder = new TrainingEventBuilder();

            builder.addRequirements(new HashMap<>() {{
                    put(" ", "not-blank");
                }}
            );
        });
    }

    @Test
    public void testCompetitionEventBuilderRequirementsInvalid() {
        assertThrows(InvalidModelException.class, () -> {
            Builder builder = new CompetitionEventBuilder();

            builder.addRequirements(new HashMap<>() {{
                    put("not-blank", " ");
                }}
            );
        });

        assertThrows(InvalidModelException.class, () -> {
            Builder builder = new CompetitionEventBuilder();

            builder.addRequirements(new HashMap<>() {{
                    put(" ", "not-blank");
                }}
            );
        });
    }

    @Test
    public void testCompetitionEventBuilderMissingRequirements() {
        assertThrows(InvalidModelException.class, () -> {
            Builder builder = new CompetitionEventBuilder();

            builder.addRequirements(new HashMap<>() {{
                    put("Gender", "M");
                }}
            );
        });

        assertThrows(InvalidModelException.class, () -> {
            Builder builder = new CompetitionEventBuilder();

            builder.addRequirements(new HashMap<>() {{
                    put("Organization", "abc corp.");
                }}
            );
        });
    }
}