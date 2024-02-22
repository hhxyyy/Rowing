package nl.tudelft.sem.template.user.controllers;

import nl.tudelft.sem.template.user.authentication.AuthManager;
import nl.tudelft.sem.template.user.domain.user.User;
import nl.tudelft.sem.template.user.domain.user.UserRepository;
import nl.tudelft.sem.template.user.models.*;
import nl.tudelft.sem.template.user.services.UserPostService;
import nl.tudelft.sem.template.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Optional;

/**
 * User controller.
 * This controller handles user interaction
 */
@RestController
public class UserAccountController {

    private final transient AuthManager authManager;
    private final transient UserRepository userRepository;
    private final transient UserService userService;
    private final transient UserPostService userPostService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager     Spring Security component used to authenticate and authorize the use
     * @param userRepository  user repository
     * @param userService     user service
     * @param userPostService user post service
     */
    @Autowired
    public UserAccountController(AuthManager authManager, UserRepository userRepository, UserService userService,
                                 UserPostService userPostService) {
        this.authManager = authManager;
        this.userRepository = userRepository;
        this.userService = userService;
        this.userPostService = userPostService;
    }

    /**
     * Gets the user's personal information.
     * Personal information can only be requested by logged-in users.
     * If the user has never set their personal information that will be communicated back to them.
     *
     * @return the user's personal information
     */
    @GetMapping("/getPersonalInfo")
    public ResponseEntity<String> getPersonalInfo() {
        Optional<User> user = userRepository.findById(authManager.getUsername());
        return user.map(value ->
                ResponseEntity.ok(value.toString())).orElseGet(() ->
                new ResponseEntity<>("No personal information found for user \""
                                     + authManager.getUsername() + "\".", HttpStatus.NOT_FOUND)
        );
    }

    /**
     * Endpoint for setting personal information of the user.
     *
     * @param request The personal information model
     *                User fills in their certificate, gender, and organization.
     *                This information gets stored to the User database.
     * @return OK if the registration is successful, BAD_REQUEST if invalid information was entered
     */
    @PostMapping("/setPersonalInfo")
    public ResponseEntity<String> setPersonalInfo(@RequestBody UserPersonalInformationSetUpModel request)
            throws ResponseStatusException {
        try {
            String s = userService.setPersonalInfo(request, userRepository, authManager.getUsername());
            if (s.equals("You entered invalid personal information")) {
                return ResponseEntity.badRequest().body("You entered invalid personal information");
            }
            return ResponseEntity.ok("Personal information was successfully stored.");
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.INSUFFICIENT_STORAGE, "error");
        }
    }

    /**
     * Request to occupy mentioned positions for some event.
     *
     * @param body The information about the positions that the user wants to fill.
     * @return OK if request was successful; BAD_REQUEST if unsuccessful.
     */
    @PostMapping("/request")
    public ResponseEntity<String> requestMatching(@RequestBody UserInformationModel body) {
        try {
            if (!body.checkIfValid()) {
                return ResponseEntity.badRequest().body("You incorrectly filled in the positions and/or availability."
                                                        + "\nMake sure to fill in your preference for all 5 positions and"
                                                        + "that your available timeframes always end before they start.");
            }

            String response = userPostService.postRequest(body, userRepository, authManager.getUsername());

            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(response);
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.badRequest().body("You have to fill in your "
                                                    + "details before you can request to enter an Event!");
        }
    }

    /**
     * The user picks from the list of events that he is presented with.
     *
     * @param body The event that the user picks from the available ones.
     * @return OK if pickEvent was successful, BAD_REQUEST if unsuccessful.
     */
    @PostMapping("/pickEvent")
    public ResponseEntity<String> pick(@RequestBody EventIdModel body) throws IOException, InterruptedException {
        if (!body.isValid()) {
            return ResponseEntity.badRequest().body("One or more fields are not valid.");
        }

        HttpResponse<String> response = userService.pick(body, authManager.getUsername());

        return ResponseEntity.status(response.statusCode()).contentType(MediaType.APPLICATION_JSON).body(response.body());
    }
}
