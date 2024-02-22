package nl.tudelft.sem.template.scheduler.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class CommunicationHandlerImpl implements CommunicationHandler {

    /**
     * This method sends a request to the Event microservice to retrieve the owner of a specific event.
     *
     * @param eventId the id of the requested event
     * @return the id of the event's owner
     * @throws IOException due to sending the request
     * @throws InterruptedException due to sending the request
     */
    public String getEventOwner(long eventId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8082/getEventOwner/" + eventId))
                .header("Authorization", "Bearer " + SecurityContextHolder.getContext().getAuthentication().getCredentials())
                .build();

        //Retrieve response from Event
        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
