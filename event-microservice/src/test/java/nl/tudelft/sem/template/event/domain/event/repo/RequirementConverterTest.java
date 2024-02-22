package nl.tudelft.sem.template.event.domain.event.repo;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RequirementConverterTest {
    RequirementConverter converter = new RequirementConverter();

    Map<String, String> requirements = Map.of(
            "key1", "value1",
            "key2", "value2",
            "key3", "value3"
    );

    String databaseString = "key1:value1;key2:value2;key3:value3;";

    @Test
    void convertToDatabaseColumn() {
        assertThat(converter.convertToDatabaseColumn(requirements))
                .contains(databaseString.split(";"));
    }

    @Test
    void convertToEntityAttribute() {
        assertThat(converter.convertToEntityAttribute(databaseString))
                .isEqualTo(requirements);
    }
}