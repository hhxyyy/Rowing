package nl.tudelft.sem.template.event.domain.event;

import nl.tudelft.sem.template.event.domain.event.repo.Event;
import nl.tudelft.sem.template.event.exceptions.InvalidModelException;
import nl.tudelft.sem.template.event.models.CustomPair;

import java.util.Date;
import java.util.Map;

public abstract class EventBuilder implements Builder {
    protected transient Event.Type type;
    private transient String ownerId;
    private transient String location;
    private transient CustomPair<Date, Date> timeFrame;
    protected transient Map<String, String> requirements;

    @Override
    public void setOwner(String ownerId) throws InvalidModelException {
        if (ownerId == null || ownerId.isBlank()) {
            throw new InvalidModelException("Given ownerId cannot be blank.");
        }

        this.ownerId = ownerId;
    }

    @Override
    public void setLocation(String location) throws InvalidModelException {
        if (location == null || location.isBlank()) {
            throw new InvalidModelException("Given location cannot be blank.");
        }

        this.location = location;
    }

    @Override
    public void setTimeFrame(CustomPair<Date, Date> timeFrame) throws InvalidModelException {
        if (timeFrame == null || timeFrame.getSecond().before(timeFrame.getFirst())) {
            throw new InvalidModelException("Invalid timeframe given: end-time is before start-time");
        }

        this.timeFrame = timeFrame;
    }

    @Override
    public Event build() {
        return new Event(type, ownerId, location, timeFrame, requirements);
    }
}
