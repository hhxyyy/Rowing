package nl.tudelft.sem.template.event.domain.event.repo;

import lombok.*;
import nl.tudelft.sem.template.event.authentication.AuthManager;
import nl.tudelft.sem.template.event.domain.event.Builder;
import nl.tudelft.sem.template.event.domain.event.CompetitionEventBuilder;
import nl.tudelft.sem.template.event.domain.event.TrainingEventBuilder;
import nl.tudelft.sem.template.event.exceptions.InvalidModelException;
import nl.tudelft.sem.template.event.models.AddEventModel;
import nl.tudelft.sem.template.event.models.CustomPair;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Entity
@Table(name = "events", schema = "projects_EventMicroservice")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class Event {
    public enum Type {
        COMPETITION,
        TRAINING
    }

    /**
     * Identifier for the event.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /**
     * What kind of event it is.
     */
    @Column(name = "event_type", nullable = false)
    private Type type;

    /**
     * Identifier for the owner.
     */
    @Column(name = "ownerId", nullable = false)
    private String ownerId;

    /**
     * Location of the event.
     */
    @Column(name = "event_location", nullable = false)
    private String location;

    /**
     * Time of the event.
     */
    @Column(name = "event_timeFrame", nullable = false)
    @Convert(converter = TimeframeConverter.class)
    private CustomPair<Date, Date> timeFrame;

    /**
     * Requirements for the event.
     */
    @Column(name = "event_requirements", nullable = false)
    @Convert(converter = RequirementConverter.class)
    private Map<String, String> requirements;

    /**
     * Constructor for an event.
     *
     * @param type the type of event
     * @param ownerId the username of the owner of the event
     * @param location where the event takes place
     * @param timeFrame at what time the event starts and ends
     * @param requirements the requirements for the event
     */
    public Event(Type type, String ownerId, String location, CustomPair<Date, Date> timeFrame,
                 Map<String, String> requirements) {
        this.type = type;
        this.ownerId = ownerId;
        this.location = location;
        this.timeFrame = timeFrame;
        this.requirements = requirements;
    }

    /**
     * Parses a model and converts it to an <code>Event</code> object.
     *
     * @param model the model to convert
     * @param authManager the authorization details of the request
     * @return an <code>Event</code> object
     * @throws InvalidModelException thrown if the model has missing or incorrect data
     */
    public static Event parseModel(AddEventModel model, AuthManager authManager) throws InvalidModelException {
        Builder builder;

        if (model.getType().equalsIgnoreCase("competition")) {
            builder = new CompetitionEventBuilder();
        } else if (model.getType().equalsIgnoreCase("training")) {
            builder = new TrainingEventBuilder();
        } else {
            throw new InvalidModelException("The type of event \"" + model.getType() + "\" does not exist. "
                                            + "It must either be \"competition\" or \"training\".");
        }

        builder.setOwner(authManager.getUserId());
        builder.setLocation(model.getLocation());
        builder.setTimeFrame(model.getTimeFrame());
        builder.addRequirements(model.getRequirements());

        return builder.build();
    }
}
