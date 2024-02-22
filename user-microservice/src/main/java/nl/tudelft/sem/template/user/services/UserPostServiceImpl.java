package nl.tudelft.sem.template.user.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.user.domain.user.User;
import nl.tudelft.sem.template.user.domain.user.UserRepository;
import nl.tudelft.sem.template.user.domain.user.UserRequest;
import nl.tudelft.sem.template.user.models.EventIdModel;
import nl.tudelft.sem.template.user.models.UserInformationModel;
import nl.tudelft.sem.template.user.models.UserPersonalInformationSetUpModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserPostServiceImpl implements UserPostService {

    @Override
    public String postRequest(UserInformationModel body, UserRepository userRepository, String username)
            throws IOException, InterruptedException {
        if (!username.isBlank() && userRepository.findById(username)
                .isPresent()) {
            Map<String, String> requirements = new HashMap<>();

            requirements.put("Certificate", userRepository.findById(username).get()
                    .getCertificate());
            requirements.put("Gender", userRepository.findById(username).get().getGender());
            requirements.put("Organization", userRepository.findById(username).get()
                    .getOrganization());
            requirements.put("Professional", Boolean.toString(userRepository.findById(username).get()
                    .isUserProfessional()));

            UserRequest request = new UserRequest(username,
                    body.getAvailability(),
                    body.getPositions(),
                    requirements);

            String requestMessage = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request);
            return wrapper(requestMessage);
        }

        throw new IOException();
    }

    @Override
    public String wrapper(String requestMessage) throws IOException, InterruptedException {
        HttpRequest userRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestMessage))
                .uri(URI.create("http://localhost:8084/match"))
                .header("Authorization", "Bearer " + SecurityContextHolder.getContext()
                        .getAuthentication().getCredentials())
                .build();
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(userRequest, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
