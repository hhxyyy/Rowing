package nl.tudelft.sem.template.event.domain.event.repo;

import nl.tudelft.sem.template.event.models.CustomPair;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class TimeframeConverterTest {
    TimeframeConverter converter = new TimeframeConverter();

    CustomPair<Date, Date> timeFrame = new CustomPair<>(new Date(1), new Date(2));
    String databaseString = "1:2";

    @Test
    void convertToDatabaseColumn() {
        assertThat(converter.convertToDatabaseColumn(timeFrame))
                .isEqualTo(databaseString);
    }

    @Test
    void convertToEntityAttribute() {
        assertThat(converter.convertToEntityAttribute(databaseString))
                .isEqualTo(timeFrame);
    }
}