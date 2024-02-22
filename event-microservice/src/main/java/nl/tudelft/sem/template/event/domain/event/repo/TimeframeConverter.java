package nl.tudelft.sem.template.event.domain.event.repo;

import nl.tudelft.sem.template.event.models.CustomPair;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Date;

@Converter
public class TimeframeConverter implements AttributeConverter<CustomPair<Date, Date>, String> {

    /**
     * Converts the value stored in the entity attribute into the data representation to
     * be stored in the database.
     *
     * @param attribute the entity attribute value to be converted
     * @return the converted data to be stored in the database column
     */
    @Override
    public String convertToDatabaseColumn(CustomPair<Date, Date> attribute) {
        return attribute.getFirst().getTime() + ":" + attribute.getSecond().getTime();
    }

    /**
     * Converts the data stored in the database column into the value to be stored in the
     * entity attribute.
     *
     * @param dbData the data from the database column to be converted
     * @return the converted value to be stored in the entity attribute
     */
    @Override
    public CustomPair<Date, Date> convertToEntityAttribute(String dbData) {
        String[] pair = dbData.split(":");

        return new CustomPair<>(new Date(Long.parseLong(pair[0])), new Date(Long.parseLong(pair[1])));
    }
}
