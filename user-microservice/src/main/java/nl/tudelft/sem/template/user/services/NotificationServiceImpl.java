package nl.tudelft.sem.template.user.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.user.authentication.AuthManager;
import nl.tudelft.sem.template.user.domain.CustomPair;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.LinkedList;

@Service
@SuppressWarnings("PMD")
public class NotificationServiceImpl implements NotificationService {

    /**
     * Gets all the notifications from the Scheduler microservice.
     *
     * @param userId the user for which to get the notifications
     * @return the response of the Scheduler service
     * @throws IOException if an I/O error occurs when sending or receiving
     * @throws IllegalArgumentException if the {@code request} argument is not
     *         a request that could have been validly built as specified by {@link
     *         HttpRequest.Builder HttpRequest.Builder}.
     */
    public HttpResponse<String> requestAllNotifications(String userId) throws IOException, InterruptedException {
        HttpRequest notificationsRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8084/getNotifications/" + userId))
                .header("Authorization", "Bearer " + SecurityContextHolder.getContext().getAuthentication().getCredentials())
                .build();

        HttpClient httpClient = HttpClient.newBuilder().build();
        return httpClient.send(notificationsRequest, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Retrieves all the notifications stored in the scheduler and transforms it into a readable format.
     *
     * @param authManager the authentication of the request
     * @return a readable string
     * @throws IOException if an I/O error occurs when sending or receiving
     * @throws IllegalArgumentException if the {@code request} argument is not
     *         a request that could have been validly built as specified by {@link
     *         HttpRequest.Builder HttpRequest.Builder}.
     */
    @Override
    public String getNotifications(AuthManager authManager) throws IOException, InterruptedException {
        JsonNode root = getAllNotifications(authManager);

        if (root == null) {
            return "You currently have no notifications.";
        }

        CustomPair<LinkedList<String>, LinkedList<String>> requests = extractNotifications(root, authManager);

        if (requests.getFirst().isEmpty() && requests.getSecond().isEmpty()) {
            return "You currently have no notifications.";
        }

        return formatNotifications(requests.getFirst(), requests.getSecond());
    }

    private JsonNode getAllNotifications(AuthManager authManager) throws IOException, InterruptedException {
        HttpResponse<String> response = requestAllNotifications(authManager.getUsername());

        if (response.statusCode() != 200) {
            return null;
        }

        return new ObjectMapper().readTree(response.body());
    }

    private CustomPair<LinkedList<String>, LinkedList<String>> extractNotifications(JsonNode root, AuthManager authManager) {
        CustomPair<LinkedList<String>, LinkedList<String>> requests
                = new CustomPair<>(new LinkedList<>(), new LinkedList<>());

        if (root.isArray()) {
            for (Iterator<JsonNode> it = root.elements(); it.hasNext(); ) {
                JsonNode node = it.next();

                if (!validateNotification(node)) {
                    continue;
                }

                getNotification(node, authManager, requests);
            }
        }

        return requests;
    }

    private boolean validateNotification(JsonNode node) {
        return node.has("ownerId") && node.has("userId") && node.has("eventId")
                && node.has("position") && node.has("id");
    }

    private void getNotification(JsonNode node, AuthManager authManager,
                                 CustomPair<LinkedList<String>, LinkedList<String>> requests) {
        String ownerId = node.get("ownerId").asText().equals("null") ? "" : node.get("ownerId").asText();

        if (!ownerId.isBlank() && ownerId.equals(authManager.getUsername())) {
            getIncomingRequest(node, requests.getFirst());
        } else {
            getOutgoingRequest(authManager, node, requests.getSecond());
        }
    }

    private void getIncomingRequest(JsonNode node, LinkedList<String> incomingRequests) {
        incomingRequests.addFirst("Notification ID " + node.get("id") + ": The user "
                + node.get("userId") + " is requesting to join your event with the ID "
                + node.get("eventId") + " for position " + node.get("position") + "!");
    }

    private void getOutgoingRequest(AuthManager authManager, JsonNode node, LinkedList<String> outgoingRequests) {
        String ownerId = node.get("ownerId").asText().equals("null") ? "" : node.get("ownerId").asText();

        if (ownerId.isBlank()) {
            outgoingRequests.addFirst("You have have been accepted to the event with the ID "
                    + node.get("eventId") + " for position " + node.get("position") + ".");
        } else if (node.get("userId").asText().equals(authManager.getUsername())) {
            outgoingRequests.addLast("You have one pending request to an event with the ID "
                    + node.get("eventId") + " for position " + node.get("position") + ".");
        }
    }

    private String formatNotifications(LinkedList<String> incomingRequests, LinkedList<String> outgoingRequests) {
        StringBuilder formatted = new StringBuilder();

        addIncomingRequests(incomingRequests, formatted);

        formatted.append("\n\n");

        addOutgoingRequests(outgoingRequests, formatted);

        return formatted.toString();
    }

    private void addIncomingRequests(LinkedList<String> incomingRequests, StringBuilder formatted) {

        formatted.append("===== You have ").append(incomingRequests.size()).append(" user")
                .append(incomingRequests.size() == 1 ? "" : "s").append(" that want to join your event =====");

        for (String notification : incomingRequests) {
            formatted.append("\n").append(notification);
        }
    }

    private void addOutgoingRequests(LinkedList<String> outgoingRequests, StringBuilder formatted) {
        formatted.append("===== You have ").append(outgoingRequests.size())
                .append(" notification").append(outgoingRequests.size() == 1 ? " =====" : "s =====");

        for (String notification : outgoingRequests) {
            formatted.append("\n").append(notification);
        }
    }
}

