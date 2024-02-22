package nl.tudelft.sem.template.event.domain.event;

import nl.tudelft.sem.template.event.authentication.AuthManager;
import nl.tudelft.sem.template.event.domain.event.repo.Event;
import nl.tudelft.sem.template.event.domain.event.repo.EventRepository;
import nl.tudelft.sem.template.event.exceptions.InvalidModelException;
import nl.tudelft.sem.template.event.models.AddEventModel;
import nl.tudelft.sem.template.event.models.EditEventModel;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class EventService {
    private final transient EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Gets all events inside the database.
     *
     * @return all events inside the database
     */
    public Iterable<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Adds an event to the database.
     *
     * @param model the model to add
     * @param authManager the authorization of the request
     * @throws InvalidModelException thrown if the model has missing or incorrect data
     * @throws IllegalArgumentException thrown if the saved entity is null
     */
    public Event addEvent(AddEventModel model, AuthManager authManager) throws
            IllegalArgumentException, InvalidModelException {
        return eventRepository.save(Event.parseModel(model, authManager));
    }

    /**
     * Edits an existing event in the database.
     *
     * @param model the model which specified what must be edited
     * @throws InvalidModelException thrown if the model has incorrect data
     * @throws IllegalArgumentException thrown if the entity could not be saved in the database
     */
    @SuppressWarnings("PMD")
    public Event editEvent(long eventId, EditEventModel model) throws IllegalArgumentException, InvalidModelException {
        Event edited = getEventById(eventId);

        if (model.getType() != null) {
            edited.setType(model.getType());
        }

        if (model.getRequirements() != null) {
            if (model.getRequirements().entrySet().stream()
                .anyMatch(entry -> entry.getKey().isBlank() || entry.getValue().isBlank())) {
                throw new InvalidModelException("Invalid requirements given.");
            }

            edited.setRequirements(model.getRequirements());
        }

        if (model.getLocation() != null) {
            if (model.getLocation().isBlank()) {
                throw new InvalidModelException("Invalid location given.");
            }

            edited.setLocation(model.getLocation());
        }

        if (model.getTimeFrame() != null) {
            if (model.getTimeFrame().getSecond().before(model.getTimeFrame().getFirst())) {
                throw new InvalidModelException("Invalid timeframe given.");
            }

            edited.setTimeFrame(model.getTimeFrame());
        }

        eventRepository.save(edited);
        return edited;
    }

    /**
     * Gets an event by the <code>eventId</code> of that event.
     *
     * @param eventId the ID of the event
     * @return the <code>Event</code> object, if found
     */
    public Optional<Event> searchEventById(long eventId) {
        return eventRepository.findById(eventId);
    }

    /**
     * Gets an event by the <code>eventId</code> of that event, assuming that it exists.
     *
     * @param eventId the ID of the event
     * @return the <code>Event</code> object
     * @throws IllegalStateException if the <code>eventId</code> is not found inside the database
     */
    public Event getEventById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalStateException("Event not found after asserting it exists"));
    }

    /**
     * Checks if an event exists inside the repository.
     *
     * @param eventId the ID of the event
     * @return if the event exists
     */
    public boolean existsById(long eventId) {
        return eventRepository.existsById(eventId);
    }

    /**
     * Deletes an event.
     *
     * @param eventId the ID of the event to delete
     */
    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }
}
