package nl.tudelft.sem.template.user.services;

import nl.tudelft.sem.template.user.domain.user.UserRepository;
import nl.tudelft.sem.template.user.models.EventIdModel;
import nl.tudelft.sem.template.user.models.UserInformationModel;
import nl.tudelft.sem.template.user.models.UserPersonalInformationSetUpModel;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.http.HttpResponse;

public interface UserPostService {

    String wrapper(String requestMessage) throws IOException, InterruptedException;

    String postRequest(UserInformationModel body, UserRepository userRepository, String username)
            throws IOException, InterruptedException;
}
