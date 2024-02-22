package nl.tudelft.sem.template.event.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.event.authentication.AuthManager;
import nl.tudelft.sem.template.event.domain.event.EventService;
import nl.tudelft.sem.template.event.domain.event.repo.Event;
import nl.tudelft.sem.template.event.exceptions.InvalidModelException;
import nl.tudelft.sem.template.event.models.AddEventModel;
import nl.tudelft.sem.template.event.models.EditEventModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EventController {

    private final transient AuthManager authManager;
    private final transient EventService eventService;
    private final transient String invalidEventId = "Invalid eventId: must be equal to or higher than 1.";

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     * @param eventService Event service which is used to communicate with the database
     */
    @Autowired
    public EventController(AuthManager authManager, EventService eventService) {
        this.authManager = authManager;
        this.eventService = eventService;
    }

    /**
     * Gets all the events stored in the database.
     *
     * @return all the events in the database
     */
    @GetMapping("/getAllEvents")
    public ResponseEntity<String> getAllEvents() {
        try {
            String json = new ObjectMapper().writerWithDefaultPrettyPrinter()
                    .writeValueAsString(eventService.getAllEvents());

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(json);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Adds a new event.
     *
     * @param model data specifying the details of the event to be added
     * @return the result of the request
     */
    @PostMapping("/addEvent")
    public ResponseEntity<String> addEvent(@RequestBody AddEventModel model) {
        try {
            String json = new ObjectMapper().writerWithDefaultPrettyPrinter()
                    .writeValueAsString(eventService.addEvent(model, authManager));

            return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(json);
        } catch (IllegalArgumentException | JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (InvalidModelException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Updates an existing event.
     *
     * @param model data specifying the details of the event that should be edited
     * @return the result of the request
     */
    @PatchMapping("/editEvent/{eventId}")
    public ResponseEntity<String> editEvent(@PathVariable long eventId, @RequestBody EditEventModel model) {
        if (eventId <= 0) {
            return ResponseEntity.badRequest().body(invalidEventId);
        }

        // Check if event exists
        if (!eventService.existsById(eventId)) {
            return ResponseEntity.notFound().build();
        }

        // Check if event owner is the same as the one sending the request
        if (!eventService.getEventById(eventId).getOwnerId().equals(authManager.getUserId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Check if model actually contains a change
        if (!model.isValid()) {
            return ResponseEntity.badRequest().body("No changes have been specified.");
        }

        try {
            String json = new ObjectMapper().writerWithDefaultPrettyPrinter()
                    .writeValueAsString(eventService.editEvent(eventId, model));

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(json);
        } catch (IllegalArgumentException | JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (InvalidModelException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Gets an event according to its event ID.
     *
     * @param eventId the ID of the event
     * @return the specific event
     */
    @GetMapping("/getEvent/{eventId}")
    public ResponseEntity<String> getEvent(@PathVariable long eventId) {
        if (eventId <= 0) {
            return ResponseEntity.badRequest().body(invalidEventId);
        }

        // Check if event exists
        if (!eventService.existsById(eventId)) {
            return ResponseEntity.notFound().build();
        }

        try {
            String json = new ObjectMapper().writerWithDefaultPrettyPrinter()
                    .writeValueAsString(eventService.getEventById(eventId));

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gets the owner's ID of an event according to its event ID.
     *
     * @param eventId the ID of the event
     * @return the owner's ID
     */
    @GetMapping("/getEventOwner/{eventId}")
    public ResponseEntity<String> getOwner(@PathVariable long eventId) {
        if (eventId <= 0) {
            return ResponseEntity.badRequest().body(invalidEventId);
        }

        // Check if event exists
        if (!eventService.existsById(eventId)) {
            return ResponseEntity.notFound().build();
        }

        Event event = eventService.getEventById(eventId);

        return ResponseEntity.ok(event.getOwnerId());
    }

    /**
     * Deletes an event according to its event ID.
     *
     * @param eventId the ID of the event
     */
    @DeleteMapping("/deleteEvent/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable long eventId) {
        if (eventId <= 0) {
            return ResponseEntity.badRequest().body(invalidEventId);
        }

        // Check if event exists
        if (!eventService.existsById(eventId)) {
            return ResponseEntity.notFound().build();
        }

        // Check if event owner is the same as the one sending the request
        if (!eventService.getEventById(eventId).getOwnerId().equals(authManager.getUserId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        eventService.deleteEvent(eventId);

        return ResponseEntity.ok().build();
    }
}
