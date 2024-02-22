package nl.tudelft.sem.template.user.models;

import nl.tudelft.sem.template.user.domain.CustomPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserInformationModelTest {

    private List<CustomPair<Date, Date>> availability;
    private boolean[] positions;

    @BeforeEach
    public void setUp() {
        Date before = new Date(2022, Calendar.DECEMBER, 13, 4, 30);
        Date after = new Date(2022, Calendar.DECEMBER, 13, 5, 0);
        availability = new ArrayList<>();
        availability.add(new CustomPair<>(before, after));
        positions = new boolean[]{true, false, false, false, false};
    }

    @Test
    public void testInvalidAvailability() {

        // Availability cannot end before it starts
        Date before1 = new Date(2022, Calendar.DECEMBER, 13, 4, 30);
        Date after1 = new Date(2022, Calendar.NOVEMBER, 13, 5, 0);

        Date before2 = new Date(2022, Calendar.DECEMBER, 13, 4, 30);
        Date after2 = new Date(2022, Calendar.DECEMBER, 13, 3, 0);

        availability.add(new CustomPair<>(before1, after1));
        availability.add(new CustomPair<>(before2, after2));

        UserInformationModel i = new UserInformationModel(availability, positions);
        assertFalse(i.checkIfValid());
    }

    @Test
    public void testValid() {
        UserInformationModel i = new UserInformationModel(availability, positions);
        assertTrue(i.checkIfValid());
    }

    @Test
    public void testInvalidPositions1() {

        boolean[] invalidPositions1 = {true};

        UserInformationModel i = new UserInformationModel(availability, invalidPositions1);

        assertFalse(i.checkIfValid());

    }

    @Test
    public void testInvalidPositions2() {

        boolean[] invalidPositions2 = {true, true, true, true, true, true};

        UserInformationModel i = new UserInformationModel(availability, invalidPositions2);

        assertFalse(i.checkIfValid());
    }

    @Test
    public void testInvalidPositions3() {

        boolean[] invalidPositions3 = {false, false, false, false, false};

        UserInformationModel i = new UserInformationModel(availability, invalidPositions3);

        assertFalse(i.checkIfValid());
    }

    @Test
    public void testInvalidPositions4() {

        UserInformationModel i = new UserInformationModel(availability, null);

        assertFalse(i.checkIfValid());
    }

    @Test
    public void testValidPositions() {

        boolean[] validPositions = {false, false, true, false, false};


        UserInformationModel i = new UserInformationModel(availability, validPositions);

        assertTrue(i.checkIfValid());
    }

}