package nl.tudelft.sem.template.scheduler.repos;

import java.util.Optional;
import nl.tudelft.sem.template.scheduler.domains.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    Optional<Match> findByUserIdAndEventId(String userId, Long eventId);

}
