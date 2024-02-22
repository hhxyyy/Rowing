package nl.tudelft.sem.template.scheduler.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.scheduler.models.CustomPair;
import nl.tudelft.sem.template.scheduler.models.EventModel;
import nl.tudelft.sem.template.scheduler.models.UserRequest;
import nl.tudelft.sem.template.scheduler.validators.AvailabilityValidator;
import nl.tudelft.sem.template.scheduler.validators.CertificateValidator;
import nl.tudelft.sem.template.scheduler.validators.CompetitionValidator;
import nl.tudelft.sem.template.scheduler.validators.Validator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service
public class EventServiceImpl implements EventService {

    /**
     * Parse the events from a response to a list of events.
     *
     * @param response the response in the string version
     * @return the list of the parsed events.
     */
    public List<EventModel> parseEvents(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response, new TypeReference<>(){});
        } catch (JsonProcessingException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all the events using a get request to the Event microservice.
     *
     * @return all the events
     * @throws IOException for json
     * @throws InterruptedException for json
     */
    public List<EventModel> getAllEvents() throws IOException, InterruptedException {
        HttpRequest eventRequest = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("http://localhost:8082/getAllEvents"))
            .header("Authorization", "Bearer " + SecurityContextHolder.getContext().getAuthentication().getCredentials())
            .build();

        //Retrieve response from Event
        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpResponse<String> response = httpClient.send(eventRequest, HttpResponse.BodyHandlers.ofString());
        return this.parseEvents(response.body());
    }

    /**
     * Parse the events from a response to a list of events.
     *
     * @param response the response in the string version
     * @return the list of the parsed events.
     */
    public CustomPair<EventModel, Integer> parsePickedEvent(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response, new TypeReference<>(){});
        } catch (JsonProcessingException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create the User object from the string received in a request.
     *
     * @param response the response in the string version
     * @return the parsed user request
     */
    public UserRequest parseUserRequest(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response, UserRequest.class);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method filters the events with some requirements.
     *
     * @param userRequest the request
     * @param events the events that need to be filtered
     * @return the events filtered along with the position that can be filled
     */
    public List<CustomPair<EventModel, Integer>> filterEvents(UserRequest userRequest, List<EventModel> events) {
        List<CustomPair<EventModel, Integer>> filteredEvents = new ArrayList<>();
        if (events == null) {
            return filteredEvents;
        }
        Validator validator = new AvailabilityValidator();
        //Creating the chain of responsibility
        Validator v1 = new CertificateValidator();
        validator.setNext(v1);
        Validator v2 = new CompetitionValidator();
        v1.setNext(v2);

        for (EventModel event : events) {
            for (int i = 0; i < 5; i++) {
                boolean[] positions = userRequest.getPositions();
                if (positions[i]) {
                    //boolean[] position = {false, false, false, false, false};
                    //position[i] = true;
                    UserRequest req = new UserRequest(userRequest.getUserId(), userRequest.getAvailability(),
                        i, userRequest.getRequirements());
                    if (validator.handle(req, event)) {
                        filteredEvents.add(new CustomPair<>(event, i));
                    }
                }
            }

        }
        return filteredEvents;
    }
}
