package nl.tudelft.sem.template.user.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.user.domain.CustomPair;

import java.util.*;


public class EventModel {
    private String type;
    private transient String location;
    private transient CustomPair<Date, Date> timeFrame;
    private transient Map<String, String> requirements;

    /**
     * Creates a new event model.
     *
     * @param type the type of the event (competition or training)
     * @param location the location of the event
     * @param timeFrame the timeframe of the event
     * @param requirements the requirements to participate in the event.
     */
    public EventModel(String type, String location, CustomPair<Date, Date> timeFrame,
                      Map<String, String> requirements) {
        this.type = type;
        this.location = location;
        this.timeFrame = timeFrame;
        this.requirements = requirements;
    }

    public EventModel() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public CustomPair<Date, Date> getTimeFrame() {
        return timeFrame;
    }

    public Map<String, String> getRequirements() {
        return requirements;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        EventModel that = (EventModel) o;
        return type.equals(that.type) && location.equals(that.location)
                && timeFrame.equals(that.timeFrame) && requirements.equals(that.requirements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, location, timeFrame, requirements);
    }
}
