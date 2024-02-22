package nl.tudelft.sem.template.user.domain;

import nl.tudelft.sem.template.user.domain.user.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserRequestTest {
    private Date before;
    private Date after;
    private List<CustomPair<Date, Date>> availability;

    boolean[] positions;

    /**
     * Setting up the values that we will use in multiple tests.
     */
    @BeforeEach
    public void setUp() {

        before = new Date(2022, Calendar.DECEMBER, 13, 4, 30);
        after = new Date(2022, Calendar.DECEMBER, 13, 5, 0);

        availability = List.of(new CustomPair<>(before, after));

        positions = new boolean[]{true, false, false, false, false};

    }

    @Test
    public void testValidExample() {

        UserRequest userRequest = new UserRequest("aadd", availability, positions, new HashMap<>());

        assertTrue(userRequest.checkIfValid());

    }

    @Test
    public void testInvalidUserId() {

        UserRequest userRequest = new UserRequest(null, availability, positions, new HashMap<>());

        assertFalse(userRequest.checkIfValid());

    }

    @Test
    public void testInvalidPositions1() {

        boolean[] invalidPositions = {true, false};

        UserRequest userRequest = new UserRequest("aadd", availability, invalidPositions, new HashMap<>());

        assertFalse(userRequest.checkIfValid());
    }

    @Test
    public void testInvalidPositions2() {

        boolean[] invalidPositions = {true, false, true, false, true, false};

        UserRequest userRequest = new UserRequest("aadd", availability, invalidPositions, new HashMap<>());

        assertFalse(userRequest.checkIfValid());
    }

    @Test
    public void testInvalidPositions3() {

        boolean[] invalidPositions = {false, false, false, false, false};

        UserRequest userRequest = new UserRequest("aadd", availability, invalidPositions, new HashMap<>());

        assertFalse(userRequest.checkIfValid());

    }

    @Test
    public void testInvalidAvailability() {

        // Event cannot end before it starts
        Date before1 = new Date(2022, Calendar.DECEMBER, 13, 4, 30);
        Date after1 = new Date(2022, Calendar.NOVEMBER, 13, 5, 0);

        Date before2 = new Date(2022, Calendar.DECEMBER, 13, 4, 30);
        Date after2 = new Date(2022, Calendar.DECEMBER, 13, 3, 0);

        List<CustomPair<Date, Date>> invalidAvailability = List.of(
                new CustomPair<>(before, after),
                new CustomPair<>(before1, after1),
                new CustomPair<>(before2, after2)
        );

        UserRequest userRequest = new UserRequest("aadd", invalidAvailability, positions, new HashMap<>());
        assertFalse(userRequest.checkIfValid());

    }

    @Test
    public void testInvalidRequirements1() {

        HashMap<String, String> m = new HashMap<>();

        m.put("Gender", "");

        UserRequest userRequest = new UserRequest("aadd", availability, positions, m);

        assertFalse(userRequest.checkIfValid());
    }

    @Test
    public void testInvalidRequirements2() {

        HashMap<String, String> m = new HashMap<>();

        m.put("Organization", "");

        UserRequest userRequest = new UserRequest("aadd", availability, positions, m);

        assertFalse(userRequest.checkIfValid());
    }

    @Test
    public void testInvalidRequirements3() {

        HashMap<String, String> m = new HashMap<>();

        m.put("Certificate", "");

        UserRequest userRequest = new UserRequest("aadd", availability, positions, m);

        assertFalse(userRequest.checkIfValid());
    }

    @Test
    public void testInvalidRequirements4() {

        HashMap<String, String> m = new HashMap<>();

        m.put("Professional", "");

        UserRequest userRequest = new UserRequest("aadd", availability, positions, m);

        assertFalse(userRequest.checkIfValid());
    }

    @Test
    public void testInvalidRequirements5() {

        HashMap<String, String> m = new HashMap<>();

        m.put("", "Xander");

        UserRequest userRequest = new UserRequest("aadd", availability, positions, m);

        assertFalse(userRequest.checkIfValid());
    }

    @Test
    public void testNullAvailability() {

        HashMap<String, String> m = new HashMap<>();

        m.put("Gender", "M");

        UserRequest userRequest = new UserRequest("aadd", null, positions, m);

        assertFalse(userRequest.checkIfValid());
    }

    @Test
    public void testNullPositions() {

        HashMap<String, String> m = new HashMap<>();

        m.put("Gender", "M");

        UserRequest userRequest = new UserRequest("aadd", availability, null, m);

        assertFalse(userRequest.checkIfValid());
    }

    @Test
    public void testNullRequirements() {

        UserRequest userRequest = new UserRequest("aadd", availability, positions, null);

        assertFalse(userRequest.checkIfValid());
    }

}
