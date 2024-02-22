package nl.tudelft.sem.template.scheduler.controllers;

import nl.tudelft.sem.template.scheduler.authentication.AuthManager;
import nl.tudelft.sem.template.scheduler.domains.Match;
import nl.tudelft.sem.template.scheduler.domains.Notification;
import nl.tudelft.sem.template.scheduler.repos.MatchRepository;
import nl.tudelft.sem.template.scheduler.repos.NotificationRepository;
import nl.tudelft.sem.template.scheduler.services.CommunicationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
public class SchedulerDatabaseController {

    private final transient MatchRepository matchRepository;
    private final transient NotificationRepository notificationRepository;
    private final transient AuthManager authManager;
    private final transient CommunicationHandler handler;

    /**
     * Instantiates a new controller.
     *
     * @param matchRepository        the repository which stores the matches
     * @param notificationRepository the repository which stores the notifications
     */
    @Autowired
    public SchedulerDatabaseController(MatchRepository matchRepository, NotificationRepository notificationRepository,
                                       AuthManager authManager, CommunicationHandler handler) {
        this.matchRepository = matchRepository;
        this.notificationRepository = notificationRepository;
        this.authManager = authManager;
        this.handler = handler;
    }

    /**
     * This method collects the user's preferred event and stores it in the database as a pending match.
     *
     * @param userId the id of the user
     * @param eventId the id of the event
     * @param position the position the user wants to fill
     * @return a confirmation that the request was sent successfully
     */
    @GetMapping("/getChoice/{userId}/{eventId}/{position}")
    public ResponseEntity<String> getUserChoice(@PathVariable String userId, @PathVariable long eventId,
                                                @PathVariable int position) {
        if (!userId.equals(authManager.getUserId()) || matchRepository.findByUserIdAndEventId(userId, eventId).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("You are allowed to chose only for yourself maximum 1 position!");
        }
        matchRepository.save(new Match(userId, eventId, position, true));

        try {
            String response = handler.getEventOwner(eventId);
            notificationRepository.save(new Notification(response, userId, eventId, position));
            return ResponseEntity
                    .ok("Your choice was registered. You will receive a notification in case the request is approved.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        }
    }

    /**
     * The method  retrieves the decision of the event owner and, based on it, creates a notification for the
     * user requesting the match and updates the match database accordingly.
     *
     * @param notificationId the id of the notification the owner reacted to
     * @param decision the decision of the owner, is true if the owner approves the user's request and false otherwise
     * @return a confirmation for the owner, announcing that the decision has been registered
     */
    @GetMapping("/getOwnerDecision/{notificationId}/{decision}")
    public ResponseEntity<String> getOwnerDecision(@PathVariable long notificationId, @PathVariable boolean decision)
            throws IOException, InterruptedException {
        Optional<Notification> notification = notificationRepository.findNotificationById(notificationId);
        if (notification.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification not found!");
        }
        long eventId = notification.get().getEventId();
        if (!handler.getEventOwner(eventId).equals(authManager.getUserId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are the owner of this event!");
        }
        Optional<Match> match = matchRepository.findByUserIdAndEventId(notification.get().getUserId(), eventId);
        matchRepository.deleteById(match.orElseThrow().getId());
        if (decision) {
            notificationRepository.save(new Notification(null, notification.get().getUserId(),
                    notification.get().getEventId(), notification.get().getPosition()));
            matchRepository.save(new Match(notification.get().getUserId(),
                    notification.get().getEventId(), notification.get().getPosition(), false));
        }
        notificationRepository.deleteById(notification.get().getId());
        return ResponseEntity.ok("The user will be announced regarding your decision.");
    }
}
