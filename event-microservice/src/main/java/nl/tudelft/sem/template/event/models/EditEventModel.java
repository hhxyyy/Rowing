package nl.tudelft.sem.template.event.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.event.domain.event.repo.Event;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
public class EditEventModel {
    private Event.Type type;
    private String location;
    private CustomPair<Date, Date> timeFrame;
    private Map<String, String> requirements;

    /**
     * Constructor for an editEventModel.
     *
     * @param location new location where the event takes place
     * @param timeFrame at what new time the event starts and ends
     * @param requirements the new requirements for the event
     */
    public EditEventModel(Event.Type type, String location, CustomPair<Date, Date> timeFrame,
                          Map<String, String> requirements) {
        this.type = type;
        this.location = location;
        this.timeFrame = timeFrame;
        this.requirements = requirements;
    }

    /**
     * Validation for an editEventModel.
     */
    public boolean isValid() {
        return type != null || location != null || timeFrame != null || requirements != null;
    }
}
