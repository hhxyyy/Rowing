package nl.tudelft.sem.template.scheduler.domains;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "matches", schema = "projects_SchedulerDB")
@RequiredArgsConstructor
@AllArgsConstructor
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private long id;

    @Column(name = "UserID")
    private String userId;

    @Column(name = "EventID")
    private long eventId;

    @Column(name = "Position")
    private int position;

    @Column(name = "Pending")
    private boolean isPending;

    /**
     * Constructor for the Match.
     *
     * @param userId   the id of the user
     * @param eventId  the id of the event
     * @param position the position filled
     */
    public Match(String userId, long eventId, int position, boolean isPending) {
        this.userId = userId;
        this.eventId = eventId;
        this.position = position;
        this.isPending = isPending;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Match match = (Match) o;
        return this.getId() == match.getId();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
