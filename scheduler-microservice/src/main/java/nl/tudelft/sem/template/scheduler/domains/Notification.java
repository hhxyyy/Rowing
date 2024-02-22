package nl.tudelft.sem.template.scheduler.domains;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private long id;

    @Column(name = "OwnerId")
    private String ownerId;

    @Column(name = "UserId")
    private String userId;

    @Column(name = "EventID")
    private long eventId;

    @Column(name = "Position")
    private int position;

    /**
     * Controller for the notification class.
     *
     * @param ownerId the id of the event owner
     * @param userId the id of the user which sent the match request
     * @param eventId the event that the user wants to join
     * @param position the position on which the user wants to join
     */
    public Notification(String ownerId, String userId, long eventId, int position) {
        this.ownerId = ownerId;
        this.userId = userId;
        this.eventId = eventId;
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Notification that = (Notification) o;
        return this.getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
