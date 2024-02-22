package nl.tudelft.sem.template.user.controllers;

import nl.tudelft.sem.template.user.exceptions.InvalidModelException;
import nl.tudelft.sem.template.user.models.*;
import nl.tudelft.sem.template.user.services.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.http.HttpResponse;


/**
 * User controller.
 * This controller handles user interaction
 */
@RestController
public class UserOwnerController {
    private final transient OwnerService ownerService;

    /**
     * Instantiates a new controller.
     *
     * @param ownerService the owner service
     */
    @Autowired
    public UserOwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    /**
     * Creates an event.
     *
     * @return if successful, the created event
     */
    @PostMapping("/createEvent")
    public ResponseEntity<String> createEvent(@RequestBody EventModel event) {
        try {
            HttpResponse<String> response = ownerService.createEvent(event);

            return ResponseEntity.status(response.statusCode()).contentType(MediaType.APPLICATION_JSON)
                    .body(response.body());
        } catch (InvalidModelException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Edits an event.
     *
     * @return if successful, the edited event
     */
    @PatchMapping("/editEvent/{eventId}")
    public ResponseEntity<String> editEvent(@PathVariable long eventId, @RequestBody EventModel event) {
        try {
            HttpResponse<String> response = ownerService.editEvent(eventId, event);

            return ResponseEntity.status(response.statusCode()).contentType(MediaType.APPLICATION_JSON)
                    .body(response.body());
        } catch (InvalidModelException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Cancels an event.
     *
     * @return if it's successful or not
     */
    @DeleteMapping("/cancelEvent/{eventId}")
    public ResponseEntity<String> cancelEvent(@PathVariable long eventId) {
        try {
            HttpResponse<String> response = ownerService.cancelEvent(eventId);

            return ResponseEntity.status(response.statusCode()).contentType(MediaType.APPLICATION_JSON)
                    .body(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**f
     * Posts the approval for an applicant to an event to the Scheduler-microservice.
     *
     * @return Response whether the approval was successfully logged.
     */
    @PostMapping("/approveApplicant")
    public ResponseEntity<String> approveApplicant(@RequestBody UserApprovalModel body) {
        try {
            String response = ownerService.approveApplicant(body);

            return ResponseEntity.ok(response);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
