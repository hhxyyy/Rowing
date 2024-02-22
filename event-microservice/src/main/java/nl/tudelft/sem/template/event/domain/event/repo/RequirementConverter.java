package nl.tudelft.sem.template.event.domain.event.repo;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.HashMap;
import java.util.Map;

@Converter
public class RequirementConverter implements AttributeConverter<Map<String, String>, String> {

    /**
     * Converts the value stored in the entity attribute into the data representation to
     * be stored in the database.
     *
     * @param attribute the entity attribute value to be converted
     * @return the converted data to be stored in the database column
     */
    @Override
    public String convertToDatabaseColumn(Map<String, String> attribute) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<String, String> entry : attribute.entrySet()) {
            stringBuilder.append(entry.getKey())
                    .append(":")
                    .append(entry.getValue())
                    .append(";");
        }

        return stringBuilder.toString();
    }

    /**
     * Converts the data stored in the database column into the value to be stored in the
     * entity attribute.
     *
     * @param dbData the data from the database column to be converted
     * @return the converted value to be stored in the entity attribute
     */
    @Override
    public Map<String, String> convertToEntityAttribute(String dbData) {
        if (dbData.isBlank()) {
            return new HashMap<>();
        }

        Map<String, String> requirements = new HashMap<>();

        String[] entries = dbData.split(";");

        for (String entry : entries) {
            String[] keyVal = entry.split(":");
            requirements.put(keyVal[0], keyVal[1]);
        }

        return requirements;
    }
}