package nl.tudelft.sem.template.event.domain.event;

import nl.tudelft.sem.template.event.domain.event.repo.Event;
import nl.tudelft.sem.template.event.exceptions.InvalidModelException;
import nl.tudelft.sem.template.event.models.CustomPair;

import java.util.Date;
import java.util.Map;

public interface Builder {
    /**
     * Sets the owner for the event.
     *
     * @param ownerId the owner's ID
     * @throws InvalidModelException thrown if the owner ID is null or blank
     */
    void setOwner(String ownerId) throws InvalidModelException;

    /**
     * Sets the location of the event.
     *
     * @param location the location of the event
     * @throws InvalidModelException thrown if the location is null or blank
     */
    void setLocation(String location) throws InvalidModelException;

    /**
     * Sets the time frame of the event.
     *
     * @param timeFrame the time frame of the event
     * @throws InvalidModelException thrown if timeframe is null or if the endtime is before the start time
     */
    void setTimeFrame(CustomPair<Date, Date> timeFrame) throws InvalidModelException;

    /**
     * Adds the requirements to the event.
     *
     * @param requirements the requirements to add
     * @throws InvalidModelException thrown if the requirements are null or contain a blank entry
     */
    void addRequirements(Map<String, String> requirements) throws InvalidModelException;

    /**
     * Builds the event.
     *
     * @return the event
     */
    Event build();
}
