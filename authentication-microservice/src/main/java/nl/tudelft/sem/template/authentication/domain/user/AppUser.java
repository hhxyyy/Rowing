package nl.tudelft.sem.template.authentication.domain.user;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.authentication.domain.HasEvents;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

/**
 * A DDD entity representing an application user in our domain.
 */
@Entity
@Table(name = "users", schema = "projects_AuthenticateDB")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AppUser extends HasEvents {
    /**
     * Identifier for the application user.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "net_id", nullable = false, unique = true)
    @Convert(converter = UsernameAttributeConverter.class)
    private Username username;

    @Column(name = "password_hash", nullable = false)
    @Convert(converter = HashedPasswordAttributeConverter.class)
    private HashedPassword password;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    /**
     * Create new application user.
     *
     * @param username The NetId for the new user
     * @param password The password for the new user
     */
    public AppUser(Username username, HashedPassword password) {
        this.username = username;
        this.password = password;
        this.isAdmin = false;
        this.recordThat(new UserWasCreatedEvent(username));
    }
}
