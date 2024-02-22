package nl.tudelft.sem.template.scheduler.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.scheduler.authentication.AuthManager;
import nl.tudelft.sem.template.scheduler.domains.Notification;
import nl.tudelft.sem.template.scheduler.services.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SchedulerNotificationController {
    private final transient NotificationService notificationService;
    private final transient AuthManager authManager;

    public SchedulerNotificationController(NotificationService notificationService, AuthManager authManager) {
        this.notificationService = notificationService;
        this.authManager = authManager;
    }

    /**
     * This method gets all notifications a user has in the database.
     *
     * @param userId the id of the user that requested the notification
     * @return a JSON string containing all notifications
     */
    @GetMapping("/getNotifications/{userId}")
    public ResponseEntity<String> getNotifications(@PathVariable String userId) {
        if (!userId.equals(authManager.getUserId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You cannot get notification for others!");
        }

        List<Notification> notifications = notificationService.getNotifications(userId);

        if (notifications.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No notifications were found!");
        }

        try {
            String response = new ObjectMapper().writerWithDefaultPrettyPrinter()
                    .writeValueAsString(notifications);

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
