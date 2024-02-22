package nl.tudelft.sem.template.user.domain.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A DDD repository for querying and persisting user aggregate roots.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    /**
     * Find user by Username.
     */
    Optional<User> findById(String username);

    /**
     * Check if an existing user already uses a Username.
     */
    boolean existsById(String username);
}
