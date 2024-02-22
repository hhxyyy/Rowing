package nl.tudelft.sem.template.user.controllers;

import nl.tudelft.sem.template.user.authentication.AuthManager;
import nl.tudelft.sem.template.user.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


/**
 * User controller.
 * This controller handles user interaction
 */
@RestController
public class UserNotificationsController {

    private final transient AuthManager authManager;
    private final transient NotificationService notificationService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager         Spring Security component used to authenticate and authorize the use
     * @param notificationService notification service
     */
    @Autowired
    public UserNotificationsController(AuthManager authManager, NotificationService notificationService) {
        this.authManager = authManager;
        this.notificationService = notificationService;
    }

    /**
     * Gets the notifications for the logged-in user by requesting them from the Scheduler-microservice.
     *
     * @return String that describes the user's notifications.
     */
    @GetMapping("/getNotifications")
    public ResponseEntity<String> getNotifications() {
        try {
            String response = notificationService.getNotifications(authManager);

            return ResponseEntity.ok(response);
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.status(500).build();
        }
    }
}
