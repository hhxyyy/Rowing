package nl.tudelft.sem.template.scheduler.repos;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.scheduler.domains.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<List<Notification>> findNotificationByUserId(String userId);

    Optional<List<Notification>> findNotificationByOwnerId(String ownerId);

    Optional<Notification> findNotificationById(Long id);
}