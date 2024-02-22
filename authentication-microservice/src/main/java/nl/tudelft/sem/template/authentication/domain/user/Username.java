package nl.tudelft.sem.template.authentication.domain.user;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a NetID in our domain.
 */
@EqualsAndHashCode
public class Username {
    private final transient String usernameValue;

    public Username(String username) {
        // validate username
        this.usernameValue = username;
    }

    @Override
    public String toString() {
        return usernameValue;
    }
}
