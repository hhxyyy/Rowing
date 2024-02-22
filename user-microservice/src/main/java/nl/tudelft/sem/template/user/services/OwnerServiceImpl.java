package nl.tudelft.sem.template.user.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.user.exceptions.InvalidModelException;
import nl.tudelft.sem.template.user.models.EventModel;
import nl.tudelft.sem.template.user.models.UserApprovalModel;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@SuppressWarnings("PMD")
public class OwnerServiceImpl implements OwnerService {

    public boolean isValidEventType(String raw) {
        return "Competition".equalsIgnoreCase(raw) || "Training".equalsIgnoreCase(raw);
    }

    @Override
    public HttpResponse<String> createEvent(EventModel event) throws IOException, InterruptedException {
        if (!isValidEventType(event.getType())) {
            throw new InvalidModelException("The type of an event can only be \"competition\" or \"training\".");
        }
        if (event.getRequirements().isEmpty()) {
            throw new InvalidModelException("The requirements field cannot be empty.");
        }
        event.setType(event.getType().toUpperCase());
        String requestMessage = new ObjectMapper()
                .writerWithDefaultPrettyPrinter().writeValueAsString(event);
        HttpRequest userRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestMessage))
                .uri(URI.create("http://localhost:8082/addEvent"))
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + SecurityContextHolder.getContext().getAuthentication().getCredentials())
                .build();
        HttpClient client = HttpClient.newBuilder().build();
        return client.send(userRequest, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> editEvent(long eventId, EventModel event) throws IOException, InterruptedException {
        if (!isValidEventType(event.getType())) {
            throw new InvalidModelException("The type of an event can only be \"competition\" or \"training\".");
        }

        event.setType(event.getType().toUpperCase());

        String requestMessage = new ObjectMapper()
                .writerWithDefaultPrettyPrinter().writeValueAsString(event);

        HttpRequest userRequest = HttpRequest.newBuilder()
                .method("PATCH", HttpRequest.BodyPublishers.ofString(requestMessage))
                .uri(URI.create("http://localhost:8082/editEvent/" + eventId))
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + SecurityContextHolder.getContext().getAuthentication().getCredentials())
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        return client.send(userRequest, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> cancelEvent(long eventId) throws IOException, InterruptedException {
        HttpRequest userRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8082/deleteEvent/" + eventId))
                .header("Authorization", "Bearer " + SecurityContextHolder.getContext().getAuthentication().getCredentials())
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        return client.send(userRequest, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public String approveApplicant(UserApprovalModel body) throws IOException, InterruptedException {
        HttpRequest ownerApproval = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8084/getOwnerDecision/" + body.getNotificationId() + "/" + body.isApprovalStatus()))
                .header("Authorization", "Bearer " + SecurityContextHolder.getContext().getAuthentication().getCredentials())
                .build();
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(ownerApproval, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

}
