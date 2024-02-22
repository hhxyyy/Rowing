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
public class UserServiceImpl implements UserService {

    @Override
    public String setPersonalInfo(UserPersonalInformationSetUpModel request,
                                  UserRepository userRepository,
                                  String username) throws ResponseStatusException {
        try {
            if (!request.isValid()) {
                return "You entered invalid personal information";
            }
            userRepository.save(new User(username, request.getCertificate(), request.getGender(),
                    request.getOrganization(), request.isUserProfessional()));

            return "Personal information was successfully stored.";
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "error");
        }
    }

    @Override
    public HttpResponse<String> pick(EventIdModel body, String username) throws IOException, InterruptedException {
        HttpRequest userChoice = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8084/getChoice/" + username + "/"
                                + body.getEventId() + "/" + body.getPosition()))
                .header("Authorization", "Bearer " + SecurityContextHolder.getContext().getAuthentication()
                        .getCredentials())
                .build();
        HttpClient client = HttpClient.newBuilder().build();

        return client.send(userChoice, HttpResponse.BodyHandlers.ofString());
    }
}
