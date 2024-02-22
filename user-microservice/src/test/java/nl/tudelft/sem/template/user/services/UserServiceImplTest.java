package nl.tudelft.sem.template.user.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import nl.tudelft.sem.template.user.authentication.AuthManager;
import nl.tudelft.sem.template.user.domain.user.User;
import nl.tudelft.sem.template.user.domain.user.UserRepository;
import nl.tudelft.sem.template.user.domain.user.UserRequest;
import nl.tudelft.sem.template.user.domain.CustomPair;
import nl.tudelft.sem.template.user.models.UserInformationModel;
import nl.tudelft.sem.template.user.models.UserPersonalInformationSetUpModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test",  "mockAuthenticationManager"})
@AutoConfigureMockMvc
public class UserServiceImplTest {

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @MockBean
    private transient UserRepository userRepository;

    @MockBean
    private UserPostServiceImpl userPostService;
    private UserPostServiceImpl spyUpsI;

    private UserService userServiceAux;
    private Date before;
    private Date after;

    /**
     * Sets up the authorization and the variables that will be used in multiple tests.
     */
    @BeforeEach
    public void setUp() {
        when(mockAuthenticationManager.getUsername()).thenReturn("lexi");

        before = new Date(2022, Calendar.DECEMBER, 13, 4, 30);

        after = new Date(2022, Calendar.DECEMBER, 13, 5, 0);

        userServiceAux = new UserServiceImpl();
        userPostService = new UserPostServiceImpl();
        spyUpsI = spy(userPostService);
    }

    @Test
    void setPersonalInfo1() {
        User user = new User("lexi", "C4", "F", "TU Delft", true);
        UserPersonalInformationSetUpModel userPersonalInformationSetUpModel =
                new UserPersonalInformationSetUpModel("C4", "F", "TU Delft", true);
        when(mockAuthenticationManager.getUsername()).thenReturn("lexi");
        when(userRepository.findById("lexi")).thenReturn(Optional.of(user));
        assertEquals(userServiceAux.setPersonalInfo(userPersonalInformationSetUpModel,
                        userRepository, "lexi"),
                "Personal information was successfully stored.");
    }

    @Test
    void setPersonalInfo2() {
        UserPersonalInformationSetUpModel userPersonalInformationSetUpModel =
                new UserPersonalInformationSetUpModel("C4", "Fem", "TU Delft", true);
        when(mockAuthenticationManager.getUsername()).thenReturn("lexi");
        when(userRepository.findById("lexi")).thenReturn(Optional.empty());
        assertEquals(userServiceAux.setPersonalInfo(userPersonalInformationSetUpModel,
                        userRepository, "lexi"),
                "You entered invalid personal information");
    }

    @Test
    void postRequestBadEmptyUser() throws IOException, InterruptedException {
        List<CustomPair<Date, Date>> availability = new ArrayList<>();
        availability.add(new CustomPair<>(before, after));
        boolean[] positions = new boolean[]{true, false, false, false, false};

        UserInformationModel userInformationModel = new UserInformationModel(availability, positions);
        when(userRepository.findById("lexi")).thenReturn(Optional.empty());

        Map<String, String> requirements = new HashMap<>();
        requirements.put("Certificate", "C4");
        requirements.put("Gender", "F");
        requirements.put("Organization", "TU Delft");
        requirements.put("Professional", "true");

        UserRequest request = new UserRequest("lexi",
                availability,
                positions,
                requirements);
        String userRequest = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .writerWithDefaultPrettyPrinter().writeValueAsString(request);

        Mockito.doReturn(userRequest).when(spyUpsI).wrapper(anyString());

        assertThrows(IOException.class, () -> spyUpsI.postRequest(userInformationModel, userRepository, "lexi"));
    }

    @Test
    void postRequestGood() throws IOException, InterruptedException {

        User user = new User("lexi", "C4", "F", "TU Delft", true);
        List<CustomPair<Date, Date>> availability = new ArrayList<>();
        availability.add(new CustomPair<>(before, after));
        boolean[] positions = new boolean[]{true, false, false, false, false};

        UserInformationModel userInformationModel = new UserInformationModel(availability, positions);
        when(userRepository.findById("lexi")).thenReturn(Optional.of(user));

        Map<String, String> requirements = new HashMap<>();
        requirements.put("Certificate", "C4");
        requirements.put("Gender", "F");
        requirements.put("Organization", "TU Delft");
        requirements.put("Professional", "true");

        UserRequest request = new UserRequest("lexi",
                availability,
                positions,
                requirements);
        String userRequest = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .writerWithDefaultPrettyPrinter().writeValueAsString(request);

        Mockito.doReturn(userRequest).when(spyUpsI).wrapper(anyString());

        assertFalse(spyUpsI.postRequest(userInformationModel, userRepository, "lexi").isBlank());
    }

    @Test
    void postRequestBadEmptyUsername() throws IOException, InterruptedException {
        User user = new User("lexi", "C4", "F", "TU Delft", true);
        List<CustomPair<Date, Date>> availability = new ArrayList<>();
        availability.add(new CustomPair<>(before, after));
        boolean[] positions = new boolean[]{true, false, false, false, false};

        UserInformationModel userInformationModel = new UserInformationModel(availability, positions);
        when(userRepository.findById("")).thenReturn(Optional.of(user));

        Map<String, String> requirements = new HashMap<>();
        requirements.put("Certificate", "C4");
        requirements.put("Gender", "F");
        requirements.put("Organization", "TU Delft");
        requirements.put("Professional", "true");

        UserRequest request = new UserRequest("lexi",
                availability,
                positions,
                requirements);
        String userRequest = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .writerWithDefaultPrettyPrinter().writeValueAsString(request);

        Mockito.doReturn(userRequest).when(spyUpsI).wrapper(anyString());

        assertThrows(IOException.class, () -> spyUpsI.postRequest(userInformationModel, userRepository, ""));
    }

    @Test
    void postRequestBadEmptyUserAndUsername() {
        List<CustomPair<Date, Date>> availability = new ArrayList<>();
        availability.add(new CustomPair<>(before, after));
        boolean[] positions = new boolean[]{true, false, false, false, false};

        UserInformationModel userInformationModel = new UserInformationModel(availability, positions);

        assertThrows(IOException.class, () -> userPostService.postRequest(
                userInformationModel,
                this.userRepository,
                ""
        ));
    }

}
