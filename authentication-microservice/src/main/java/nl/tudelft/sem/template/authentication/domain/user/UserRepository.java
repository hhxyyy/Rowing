package nl.tudelft.sem.template.authentication.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * A DDD repository for quering and persisting user aggregate roots.
 */
@Repository
public interface UserRepository extends JpaRepository<AppUser, String> {
    /**
     * Find user by Username.
     */
    Optional<AppUser> findByUsername(Username username);

    /**
     * Check if an existing user already uses a Username.
     */
    boolean existsByUsername(Username username);
}
