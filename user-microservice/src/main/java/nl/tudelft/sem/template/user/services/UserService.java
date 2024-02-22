package nl.tudelft.sem.template.user.services;

import nl.tudelft.sem.template.user.domain.user.User;
import nl.tudelft.sem.template.user.domain.user.UserRepository;
import nl.tudelft.sem.template.user.models.EventIdModel;
import nl.tudelft.sem.template.user.models.UserInformationModel;
import nl.tudelft.sem.template.user.models.UserPersonalInformationSetUpModel;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Optional;

public interface UserService {

    String setPersonalInfo(UserPersonalInformationSetUpModel request, UserRepository userRepository, String username)
            throws ResponseStatusException;

    HttpResponse<String> pick(EventIdModel body, String username)
            throws IOException, InterruptedException;
}
