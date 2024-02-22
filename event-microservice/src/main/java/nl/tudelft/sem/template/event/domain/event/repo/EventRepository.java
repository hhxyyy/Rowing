package nl.tudelft.sem.template.event.domain.event.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Find event by EventId.
     */
    Optional<Event> findById(long id);

    /**
     * Check if an event exists with an EventId.
     */
    boolean existsById(long id);
}
