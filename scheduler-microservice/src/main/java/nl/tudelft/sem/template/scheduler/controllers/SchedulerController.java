package nl.tudelft.sem.template.scheduler.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import nl.tudelft.sem.template.scheduler.authentication.AuthManager;
import nl.tudelft.sem.template.scheduler.domains.Notification;
import nl.tudelft.sem.template.scheduler.models.CustomPair;
import nl.tudelft.sem.template.scheduler.models.EventModel;
import nl.tudelft.sem.template.scheduler.models.NewCertificateModel;
import nl.tudelft.sem.template.scheduler.models.UserRequest;
import nl.tudelft.sem.template.scheduler.services.EventService;
import nl.tudelft.sem.template.scheduler.services.NotificationService;
import nl.tudelft.sem.template.scheduler.validators.AvailabilityValidator;
import nl.tudelft.sem.template.scheduler.validators.CertificateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
public class SchedulerController {
    private final transient EventService service;

    /**
     * Instantiates a new controller.
     *
     * @param service                the EventService used
     */
    @Autowired
    public SchedulerController(EventService service) {
        this.service = service;
    }

    /**
     * Gets example by id.
     *
     * @return the example found in the database with the given id
     */
    @PostMapping("/match")
    public ResponseEntity<String> matchUserWithEvent(@RequestBody String requestAsString)
        throws IOException, InterruptedException {
        UserRequest request = service.parseUserRequest(requestAsString);
        List<EventModel> events = service.getAllEvents();

        if (request == null || events == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        //Filter the events based on the user's characteristics and availability
        List<CustomPair<EventModel, Integer>> filteredEvents = service.filterEvents(request, events);
        //Make a request to the User to pick their preferred event from the list of events
        String requestBody = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(filteredEvents);
        return ResponseEntity.ok(requestBody);
    }
}
