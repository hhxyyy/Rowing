package nl.tudelft.sem.template.event.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
public class AddEventModel {
    private String type;
    private String location;
    private CustomPair<Date, Date> timeFrame;
    private Map<String, String> requirements;

    /**
     * Constructor for an addEventModel.
     *
     * @param type the type of event
     * @param location where the event takes place
     * @param timeFrame at what time the event starts and ends
     * @param requirements the requirements for the event
     */
    public AddEventModel(String type, String location, CustomPair<Date, Date> timeFrame, Map<String, String> requirements) {
        this.type = type;
        this.location = location;
        this.timeFrame = timeFrame;
        this.requirements = requirements;
    }
}
