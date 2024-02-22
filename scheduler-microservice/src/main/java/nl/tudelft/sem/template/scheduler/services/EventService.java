package nl.tudelft.sem.template.scheduler.services;

import nl.tudelft.sem.template.scheduler.models.CustomPair;
import nl.tudelft.sem.template.scheduler.models.EventModel;
import nl.tudelft.sem.template.scheduler.models.UserRequest;
import nl.tudelft.sem.template.scheduler.validators.Validator;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface EventService {
    List<EventModel> parseEvents(String response);

    CustomPair<EventModel, Integer> parsePickedEvent(String response);

    List<CustomPair<EventModel, Integer>> filterEvents(UserRequest userRequest, List<EventModel> events);

    UserRequest parseUserRequest(String response);

    List<EventModel> getAllEvents() throws IOException, InterruptedException;
}
